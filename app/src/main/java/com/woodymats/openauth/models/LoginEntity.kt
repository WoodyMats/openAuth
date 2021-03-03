package com.woodymats.openauth.models

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.BaseObservable

data class LoginEntity(private var email: String, private  var password: String): BaseObservable() {

    fun isDataValid(): Int {
        return if (TextUtils.isEmpty(password))
            0
        else if (TextUtils.isEmpty(email))
            1
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            2
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