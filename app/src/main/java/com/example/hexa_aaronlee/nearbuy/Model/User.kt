package com.example.hexa_aaronlee.nearbuy.Model

import android.text.TextUtils
import android.util.Patterns
import com.example.hexa_aaronlee.nearbuy.View.IUser

class User(override var email:String,override var password:String):IUser {
    override var isDataVaild: Boolean
        get() = (!TextUtils.isEmpty(email)&& Patterns.EMAIL_ADDRESS.matcher(email).matches()&&!TextUtils.isEmpty(password))
        set(value) {}
}