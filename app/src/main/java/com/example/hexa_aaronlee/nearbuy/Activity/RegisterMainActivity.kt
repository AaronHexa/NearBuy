package com.example.hexa_aaronlee.nearbuy.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.Presenter.RegisterPresenter
import com.example.hexa_aaronlee.nearbuy.View.RegisterView
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_register_main.*
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.hexa_aaronlee.nearbuy.R
import java.util.regex.Pattern


class RegisterMainActivity : AppCompatActivity(), RegisterView.View {

    var email: String = ""
    var password: String = ""
    var name: String = ""
    var tmpID: String = ""
    var genderSelection: String = ""
    var numPhone: String = ""


    lateinit var firebaseAuth: FirebaseAuth

    lateinit var mPresenter: RegisterPresenter

    lateinit var filePath: Uri

    private val PICK_IMAGE_REQUEST = 1234
    var imageEdited: Int = 0
    var selectedGender = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)

        val actionBar = this.supportActionBar!!

        actionBar.title = "Registration"
        actionBar.setDisplayHomeAsUpEnabled(true)


        userPicSet.setOnClickListener {
            imageEdited = 1
            chooseImageProcess()
        }

        firebaseAuth = FirebaseAuth.getInstance()

        mPresenter = RegisterPresenter(this)


        registerBtn.setOnClickListener {
            registerProcess()
        }

        registerLayout.setOnClickListener {
            hideKeyboard()
        }

        maleGenderRegister.setOnClickListener {
            selectedGender = 1

            val imgIcon = findViewById<TextView>(R.id.maleGenderRegister)
            val backgroundGradient = imgIcon.background as GradientDrawable
            backgroundGradient.setColor(resources.getColor(R.color.colorLightBlue))

            val imgIcon2 = findViewById<TextView>(R.id.femaleGenderRegister)
            val backgroundGradient2 = imgIcon2.background as GradientDrawable
            backgroundGradient2.setColor(resources.getColor(R.color.colorLightGrey))
            genderSelection = "Male"
        }

        femaleGenderRegister.setOnClickListener {
            selectedGender = 1

            val imgIcon = findViewById<TextView>(R.id.femaleGenderRegister)
            val backgroundGradient = imgIcon.background as GradientDrawable
            backgroundGradient.setColor(resources.getColor(R.color.colorLightBlue))

            val imgIcon2 = findViewById<TextView>(R.id.maleGenderRegister)
            val backgroundGradient2 = imgIcon2.background as GradientDrawable
            backgroundGradient2.setColor(resources.getColor(R.color.colorLightGrey))
            genderSelection = "Female"
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun registerProcess() {
        //getting email and password from edit texts
        email = emailRegister.text.toString().trim()
        password = passwordRegister.text.toString().trim()
        name = nameRegister.text.toString().trim()
        numPhone = phoneRegister.text.toString().trim()

        val regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!_-]")

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter Email", Toast.LENGTH_SHORT).show()
            emailRegisterAlert.visibility = View.VISIBLE
            return
        }

        if (TextUtils.isEmpty(password) || password.length < 6) {
            Toast.makeText(this, "Please enter Password", Toast.LENGTH_SHORT).show()
            passwordRegisterAlert.visibility = View.VISIBLE
            return
        }

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter Name", Toast.LENGTH_SHORT).show()
            nameRegisterAlert.visibility = View.VISIBLE
            return
        }else if(regex.matcher(name).find()){
            Toast.makeText(this, "No Special Character", Toast.LENGTH_SHORT).show()
            nameRegisterAlert.text = "No Special Character"
            nameRegisterAlert.visibility = View.VISIBLE
        }

        if (selectedGender == 0) {
            Toast.makeText(this, "Please select Gender", Toast.LENGTH_SHORT).show()
            genderRegisterAlert.visibility = View.VISIBLE
        }

        if (TextUtils.isEmpty(numPhone) || numPhone.length != 10) {
            Toast.makeText(this, "Please enter Phone Number", Toast.LENGTH_SHORT).show()
            phoneRegisterAlert.visibility = View.VISIBLE
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
                        Log.i(" Current uid : ", "$tmpID")

                        if (imageEdited == 0) {
                            filePath = Uri.parse("android.resource://" + applicationContext.packageName + "/drawable/guest_icon")
                        }

                        //save Profile Pic to Storage
                        mPresenter.saveProfilePicToStorage(tmpID, filePath)

                    }
                    progressDialog.dismiss()
                }.addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Registration Error", Toast.LENGTH_LONG).show()

                    Log.e("Registration Error : ", it.toString())
                }
    }

    override fun toastUploadSuccess(uriTxt: String) {
        Toast.makeText(this, "Successfully Uploaded :)", Toast.LENGTH_LONG).show()

        //save in database
        mPresenter.saveUserDataToDatabase(email, password, name, tmpID, uriTxt, genderSelection, numPhone)
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

    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        //startActivity(Intent(applicationContext, LoginMain::class.java))
        finish()
    }
}
