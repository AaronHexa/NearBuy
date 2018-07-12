package com.example.hexa_aaronlee.nearbuy.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.Presenter.LoginPresenter
import com.example.hexa_aaronlee.nearbuy.R
import com.example.hexa_aaronlee.nearbuy.View.LoginView
import kotlinx.android.synthetic.main.activity_login_main.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.*
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*


class LoginMainActivity : AppCompatActivity(), LoginView.View {

    var presenter: LoginPresenter? = null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var gso: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    private lateinit var mAuth: FirebaseAuth

    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var firebaseAuth: FirebaseAuth

    val REQUEST_LOCATION_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        mAuth = FirebaseAuth.getInstance()

        presenter = LoginPresenter(this)

        mFirebaseDatabase = FirebaseDatabase.getInstance()

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance()

        //AskPermission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission()
        }


        //if the objects getcurrentuser method is not null
        //means user is already logged in

        if (firebaseAuth.currentUser != null) {
            //close this activity
            finish()
            //opening profile activity
            startActivity(Intent(applicationContext, MainPageActivity::class.java))
        }

        //Button fuction
        googleBtn.setOnClickListener {
            presenter!!.clickGoogleBtn()
        }

        loginBtn.setOnClickListener {
            presenter!!.clickLoginBtn(emailEdit.text.toString(), passwordEdit.text.toString())
        }

        registerDirect.setOnClickListener {
            presenter!!.clickRegisterTxt()
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        loginLayout.setOnClickListener {
            hideKeyboard()
        }
    }

    override fun loginToNext() {
        loginProcess()
    }

    override fun regiterDirected() {
        startActivity(Intent(applicationContext, RegisterMainActivity::class.java))
    }

    override fun loginFaild() {
        Toast.makeText(applicationContext, "Please Fill In Email & Password", Toast.LENGTH_SHORT).show()
        emailLoginAlert.visibility = View.VISIBLE
        passwordLoginAlert.visibility = View.VISIBLE
    }

    override fun loginGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, "Google sign in failed" + e, Toast.LENGTH_SHORT).show()

            }

        }
    }

    fun hideKeyboard()
    {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val progressDialog = ProgressDialog(this)
        //displaying a progress dialog

        progressDialog.setMessage("Logging Please Wait...")
        progressDialog.show()

        Log.d("firebaseAuthWithGoogle:",(acct.id!!))
        System.out.println("firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        System.out.println("signInWithCredential:success")
                        val uid = mAuth.currentUser!!.uid
                        getDetailGoogle(uid)
                        progressDialog.dismiss()
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss()
                        System.out.println("signInWithCredential:failure")
                        Toast.makeText(applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }

                }
    }

    fun getDetailGoogle(user_id: String) {

        var personName: String = ""
        var personEmail: String = ""
        var personPhoto: String = ""
        val password = "---"
        val gender = "Male"
        val phoneNum = "---"

        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (acct != null) {
            personName = acct.displayName.toString()
            personEmail = acct.email.toString()
            personPhoto = acct.photoUrl.toString()
            Log.d("Data :","$personName...$user_id...$personEmail...$personPhoto")
            updateNextPage(personEmail, password, user_id, personName, personPhoto,gender,phoneNum)
        }

    }

    fun updateNextPage(email: String, password: String, user_id: String, name: String, profilePhoto: String,gender : String, phoneNum: String) {

        presenter?.saveDataProcess(email, password, user_id, name, profilePhoto,gender,phoneNum)

        val intent = Intent(applicationContext, MainPageActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun loginProcess() {

        val email = emailEdit.text.toString().trim()
        val password = passwordEdit.text.toString().trim()

        val progressDialog = ProgressDialog(this)
        //displaying a progress dialog

        progressDialog.setMessage("Logging Please Wait...")
        progressDialog.show()

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, { task ->
                    progressDialog.dismiss()
                    //if the task is successfull
                    if (task.isSuccessful) {

                        Toast.makeText(applicationContext, "Login Successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(applicationContext, MainPageActivity::class.java)
                        finish()
                        startActivity(intent)

                    }
                    progressDialog.dismiss()
                }).addOnFailureListener {
                    Toast.makeText(applicationContext, "Login Failed: Invalid Email or Password", Toast.LENGTH_SHORT).show()
                    Log.e("The read error : ", it.toString())
                    progressDialog.dismiss()
                }
    }

    fun checkLocationPermission(): Boolean {
        return if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);

            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_CODE);
            }
            false

        } else
            true
    }
}

