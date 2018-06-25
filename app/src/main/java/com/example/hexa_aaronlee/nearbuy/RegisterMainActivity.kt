package com.example.hexa_aaronlee.nearbuy

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.Presenter.RegisterPresenter
import com.example.hexa_aaronlee.nearbuy.View.RegisterView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register_main.*


class RegisterMainActivity : AppCompatActivity(), RegisterView.View {

    var email: String = ""
    var password: String = ""
    var name: String = ""
    var tmpID: String = ""


    lateinit var firebaseAuth: FirebaseAuth

    lateinit var mPresenter: RegisterPresenter

    lateinit var filePath: Uri

    private val PICK_IMAGE_REQUEST = 1234
    var imageEdited: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)

        userPicSet.setOnClickListener {
            imageEdited = 1
            chooseImageProcess()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        mPresenter = RegisterPresenter(this)

        registerBtn.setOnClickListener {
            registerProcess()
        }
    }

    fun registerProcess() {
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
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    //checking if success
                    if (task.isSuccessful) {

                        val user = FirebaseAuth.getInstance().currentUser
                        tmpID = user!!.uid
                        println(" This is the current uid : $tmpID")

                        if (imageEdited == 0) {
                            filePath = Uri.parse("android.resource://" + applicationContext.packageName + "/drawable/guest_icon")
                        }

                        //save Profile Pic to Storage
                        mPresenter.saveProfilePicToStorage(tmpID, filePath)

                    } else {
                        //display some message here
                        Toast.makeText(this, "Registration Error", Toast.LENGTH_LONG).show()
                    }
                    progressDialog.dismiss()
                }
    }

    override fun toastUploadSuccess(uriTxt: String) {
        Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()

        //save in database
        mPresenter.saveUserDataToDatabase(email, password, name, tmpID, uriTxt)
        finish()
        startActivity(Intent(applicationContext, MainPageActivity::class.java))
    }

    override fun toastUploadFailed(e: Exception) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
    }

    fun chooseImageProcess() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath = data!!.data
                Picasso.get()
                        .load(filePath)
                        .resize(200, 200)
                        .centerCrop()
                        .into(userPicSet)

            }
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
