package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_page.*
import android.R.string.cancel
import android.content.DialogInterface




class MainPage : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    lateinit var databaseR : DatabaseReference

    var personName : String? = null
    var personEmail : String?= null
    var user_id : String= ""

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
            startActivity(Intent(applicationContext,MapsActivity::class.java))
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

    override fun onBackPressed() {

        val builder = AlertDialog.Builder(this)

        builder.setIcon(R.drawable.ic_power_settings_new_black_24dp)
        builder.setTitle("Logout")
        builder.setMessage("Are You Sure You Want Logout?")
        builder.setCancelable(true)

        builder.setPositiveButton("Yes", { dialog, whichButton ->

            //Sign out from Firebase Auth
            FirebaseAuth.getInstance().signOut()

            //Sign out From Google
            mGoogleSignInClient.signOut()
            startActivity(Intent(applicationContext, LoginMain::class.java))
            finish()

            dialog.dismiss()
        })

        builder.setNegativeButton("Cancel", { dialog, whichButton ->
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }
}
