package com.example.hexa_aaronlee.nearbuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register_main.*


class RegisterMain : AppCompatActivity() {

    var email :String? = ""
    var password :String? = ""
    var name :String? = ""

    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatafaceReference: DatabaseReference
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var mCurrentUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)

        firebaseAuth = FirebaseAuth.getInstance()

        registerBtn.setOnClickListener{
            registerProcess()
        }
    }

    fun registerProcess(){
        //getting email and password from edit texts
        email = emailRegister.text.toString().trim()
        password = passwordRegister.text.toString().trim()
        name = nameRegister.text.toString().trim()

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show()
            return
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show()
            return
        }

        //if the email and password are not empty
        //displaying a progress dialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Registering Please Wait...")
        progressDialog.show()

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    //checking if success
                    if (task.isSuccessful) {

                        val user = FirebaseAuth.getInstance().currentUser
                        val tmpID = user!!.uid
                        println(" This is the current uid : $tmpID")

                        //save in database
                        saveUserDataToDatabase(tmpID)

                        finish()
                        startActivity(Intent(applicationContext, MainPage::class.java))
                    } else {
                        //display some message here
                        Toast.makeText(this, "Registration Error", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                }
    }

    //Save Into Firebase Database
    fun saveUserDataToDatabase(id: String?)
    {
        mDatafaceReference = FirebaseDatabase.getInstance().reference.child("User")

        var photoPic = R.drawable.guest_icon.toString()

        val data = UserData(email,password,id,name,photoPic)

        mDatafaceReference.child("Email").child(id).setValue(data)
    }
}
