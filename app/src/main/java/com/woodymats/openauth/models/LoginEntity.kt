package com.woodymats.openauth.models

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.BaseObservable

data class LoginEntity(private var email: String, private  var password: String): BaseObservable() {

    fun isEmailValid(): Int {
        return if (TextUtils.isEmpty(email))
            0
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            1
        else
            -1
    }

    fun isPasswordValid(): Int {
        return if (TextUtils.isEmpty(password))
            0
        else
            -1
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }

}