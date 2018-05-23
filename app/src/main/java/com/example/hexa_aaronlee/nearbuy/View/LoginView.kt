package com.example.hexa_aaronlee.nearbuy.View

public interface LoginView {

    interface view {
        fun loginToNext()
        fun regiterDirected()
        fun loginFaild()
        fun loginGoogle()
    }

    interface presenter {
        fun clickRegisterTxt()
        fun clickLoginBtn(email:String,password : String)
        fun clickGoogleBtn()
    }
}