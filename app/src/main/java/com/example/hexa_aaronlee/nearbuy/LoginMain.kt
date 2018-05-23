package com.example.hexa_aaronlee.nearbuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils

import android.widget.Toast
import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.Presenter.LoginPresenter
import com.example.hexa_aaronlee.nearbuy.View.LoginView
import kotlinx.android.synthetic.main.activity_login_main.*
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*


class LoginMain : AppCompatActivity(), LoginView.view{

    var presenter : LoginPresenter? = null

    lateinit var mGoogleSignInClient : GoogleSignInClient
    lateinit var gso : GoogleSignInOptions
    val RC_SIGN_IN : Int = 1
    private lateinit var mAuth : FirebaseAuth

    lateinit var mFirebaseDatabase: FirebaseDatabase
    lateinit var mDatafaceReference: DatabaseReference
    lateinit var firebaseAuth : FirebaseAuth

    private var typeSignIn : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_main)

        mAuth = FirebaseAuth.getInstance()

        presenter = LoginPresenter(this)

        mFirebaseDatabase = FirebaseDatabase.getInstance()

        //getting firebase auth object
        firebaseAuth = FirebaseAuth.getInstance()


        //if the objects getcurrentuser method is not null
        //means user is already logged in

        if (firebaseAuth.currentUser != null) {
            //close this activity
            finish()
            //opening profile activity
            startActivity(Intent(applicationContext, MainPage::class.java))
        }

        //Button fuction
        googleBtn.setOnClickListener{
            presenter!!.clickGoogleBtn()
        }

        loginBtn.setOnClickListener{
            presenter!!.clickLoginBtn(emailEdit.text.toString(),passwordEdit.text.toString())
        }

        registerDirect.setOnClickListener{
            presenter!!.clickRegisterTxt()
        }

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    override fun loginToNext() {
        typeSignIn = "Email"
        loginProcess()
        Toast.makeText(applicationContext,"Login Successfully",Toast.LENGTH_SHORT).show()
    }

    override fun regiterDirected() {
        startActivity( Intent(applicationContext,RegisterMain::class.java))
    }

    override fun loginFaild() {
        Toast.makeText(applicationContext,"Login Fail",Toast.LENGTH_SHORT).show()
    }

    override fun loginGoogle() {
        typeSignIn = "Google"
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
                Toast.makeText(this,"Google sign in failed" + e,Toast.LENGTH_SHORT).show()
                // ...
            }

        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {

        val progressDialog = ProgressDialog(this)
        //displaying a progress dialog

        progressDialog.setMessage("Logging Please Wait...")
        progressDialog.show()

        System.out.println("firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        System.out.println("signInWithCredential:success")
                        val uid = mAuth.currentUser?.uid
                        getDetailGoogle(uid)
                        progressDialog.dismiss()
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss()
                        System.out.println("signInWithCredential:failure")
                        Toast.makeText(applicationContext, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
    }

    fun getDetailGoogle(user_id:String?){

        var personName :String? = null
        var personEmail:String? = null
        var personPhoto :String? = null
        var password = "-"

        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (acct != null) {
            personName = acct.displayName
            personEmail = acct.email
            personPhoto = acct.photoUrl.toString()
            System.out.println(personName + " " + user_id + " " +personEmail + " " +personPhoto + " " )
            updateNextPage(personEmail,password,user_id,personName,personPhoto)
        }

    }

    fun updateNextPage(email: String?,password: String?,user_id: String?,name: String?,profilePhoto: String?){

        saveDatabase(email,password,user_id,name,profilePhoto)

        var intent = Intent(applicationContext,MainPage::class.java)
        intent.putExtra("Type Sign In",typeSignIn)
        startActivity(intent)
        finish()
    }

    fun saveDatabase(email: String?,password: String?, user_id: String?, name: String?, profilePhoto: String?)
    {
        mDatafaceReference = mFirebaseDatabase.getReference("User").child("Google")

        val data = UserData(email,password,user_id,name,profilePhoto)

        mDatafaceReference.child(user_id).setValue(data).addOnCompleteListener {
            System.out.println("Save Done !!!!!!!")
        }
    }

    fun loginProcess() {

        var email = emailEdit.text.toString().trim()
        var password = passwordEdit.text.toString().trim()

        val progressDialog = ProgressDialog(this)
        //displaying a progress dialog

        progressDialog.setMessage("Logging Please Wait...")
        progressDialog.show()

        //logging in the user
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener<AuthResult> { task ->
                    progressDialog.dismiss()
                    //if the task is successfull
                    if (task.isSuccessful) {

                        var intent = Intent(applicationContext,MainPage::class.java)
                        intent.putExtra("Type Sign In",typeSignIn)
                        finish()
                        startActivity(intent)


                        fun onCancelled(databaseError: DatabaseError) {
                            println("The read failed: " + databaseError.code)
                        }
                    }
                    progressDialog.dismiss()
                })
    }
}

