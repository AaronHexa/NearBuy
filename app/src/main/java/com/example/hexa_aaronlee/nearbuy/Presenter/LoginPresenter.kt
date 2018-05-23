package com.example.hexa_aaronlee.nearbuy.Presenter

import com.example.hexa_aaronlee.nearbuy.Model.User
import com.example.hexa_aaronlee.nearbuy.View.LoginView

public class LoginPresenter(internal var view: LoginView.view) : LoginView.presenter {

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