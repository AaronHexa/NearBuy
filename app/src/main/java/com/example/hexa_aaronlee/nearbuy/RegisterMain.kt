package com.example.hexa_aaronlee.nearbuy

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_register_main.*


class RegisterMain : AppCompatActivity() {

    var email :String = ""
    var password :String = ""
    var name :String = ""
    var tmpID : String = ""

    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatafaceReference: DatabaseReference
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var mCurrentUser : FirebaseUser

    lateinit var filePath : Uri
    lateinit var mStorage : FirebaseStorage
    lateinit var mStorageReference: StorageReference

    private val PICK_IMAGE_REQUEST = 1234
    var imageEdited : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)

        userPicSet.setOnClickListener{
            imageEdited = 1
            chooseImageProcess()
        }

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
                        tmpID = user!!.uid
                        println(" This is the current uid : $tmpID")

                        mStorage = FirebaseStorage.getInstance()
                        var mReference = mStorage.reference.child("ProfilePicture")
                        try {
                            mReference.child(tmpID).putFile(filePath).addOnSuccessListener {
                                taskSnapshot: UploadTask.TaskSnapshot? -> var url = taskSnapshot!!.downloadUrl
                                Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()
                            }
                        }catch (e: Exception) {
                            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                        }
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
    fun saveUserDataToDatabase(id: String)
    {
        mDatafaceReference = FirebaseDatabase.getInstance().reference.child("User")


        var photoPic = filePath.toString()


        val data = UserData(email,password,name,id,photoPic,"Email")

        mDatafaceReference.child(id).setValue(data)

    }

    fun chooseImageProcess(){


        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var uriTxt: String = ""
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data
                uriTxt = filePath.toString()
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver,filePath)
                userPicSet.setImageBitmap(bitmap)

            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
