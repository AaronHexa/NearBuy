package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_page.*


class MainPage : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    lateinit var databaseR : DatabaseReference

    var personName : String? = null
    var personEmail : String?= null
    var user_id : String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mAuth = FirebaseAuth.getInstance()

        user_id = mAuth.currentUser!!.uid
        UserDetail.user_id = user_id

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()


        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)

        signOutBtn.setOnClickListener{

            //Sign out from Firebase Auth
            FirebaseAuth.getInstance().signOut()

            //Sign out From Google
            mGoogleSignInClient.signOut()

            startActivity(Intent(applicationContext,LoginMain::class.java))
            finish()
        }

        getUserDataFromDatabase()

        historyBtn.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,ChatHistory::class.java)) }

        profileBtn.setOnClickListener{
            finish()
            startActivity(Intent(applicationContext,ProfileInfo::class.java))
        }

        mapBtn.setOnClickListener {
            finish()
            startActivity(Intent(applicationContext,MapDisplay::class.java))
        }

    }

    fun getUserDataFromDatabase()
    {
        databaseR = FirebaseDatabase.getInstance().reference.child("User").child(user_id)


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(UserData::class.java)
                personName = data?.name
                personEmail = data?.email

                UserDetail.email = data?.email
                UserDetail.username = data?.name
                System.out.println("..................."+UserDetail.username+" "+ UserDetail.user_id)

                setUIUpdate()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    fun setUIUpdate()
    {
        nameTxt.text = personName
    }
}
