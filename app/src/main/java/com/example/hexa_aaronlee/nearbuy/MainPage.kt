package com.example.hexa_aaronlee.nearbuy

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main_page.*
import kotlinx.android.synthetic.main.activity_register_main.*
import java.util.ArrayList

class MainPage : AppCompatActivity() {

    private lateinit var mAuth : FirebaseAuth
    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    lateinit var databaseR : DatabaseReference

    var personName : String? = null
    var getTypeSignIn :String?= null
    var personEmail : String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        mAuth = FirebaseAuth.getInstance()

        var user = mAuth.currentUser

        val bundle = intent.extras
        getTypeSignIn = bundle.getString("Type Sign In")
        System.out.println(".................."+getTypeSignIn)

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
                    .addOnCompleteListener(this, OnCompleteListener<Void> {
                        // ...
                    })
            startActivity(Intent(applicationContext,LoginMain::class.java))
            finish()
        }

        getUserDataFromDatabase()

    }

    fun getUserDataFromDatabase()
    {
        databaseR = FirebaseDatabase.getInstance().reference.child("User")


        databaseR.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //personName =
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }
}
