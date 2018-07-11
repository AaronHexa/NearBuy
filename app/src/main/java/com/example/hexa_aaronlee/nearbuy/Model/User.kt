package com.example.hexa_aaronlee.nearbuy.Model

import android.text.TextUtils
import android.util.Patterns
import com.example.hexa_aaronlee.nearbuy.View.LoginView

class User(override var email:String,override var password:String):LoginView.Model {
    override var isDataVaild: Boolean
        get() = (!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches()&&!TextUtils.isEmpty(password))
        set(value) {}
}