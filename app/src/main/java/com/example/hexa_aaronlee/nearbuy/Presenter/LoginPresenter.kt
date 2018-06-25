package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.DatabaseData.UserData
import com.example.hexa_aaronlee.nearbuy.Model.User
import com.example.hexa_aaronlee.nearbuy.View.LoginView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

public class LoginPresenter(internal var view: LoginView.View) : LoginView.Presenter {

    lateinit var mDatafaceReference : DatabaseReference

    override fun saveDataProcess(email: String,password: String, user_id: String, name: String, profilePhoto: String)
    {
        mDatafaceReference = FirebaseDatabase.getInstance().getReference("User")

        val data = UserData(email,password,name,user_id,profilePhoto,"Google")

        mDatafaceReference.child(user_id).setValue(data).addOnCompleteListener {
            System.out.println("Save Done !!!!!!!")
        }
    }


    override fun clickGoogleBtn() {
        view.loginGoogle()
    }


    override fun clickRegisterTxt() {
        view.regiterDirected()
    }


    override fun clickLoginBtn(email:String,password : String) {
        var user = User(email,password)
        val isLoginSuccess = user.isDataVaild
        if(isLoginSuccess)
        {
            view.loginToNext()
        }
        else
        {
            view.loginFaild()
        }
    }

}