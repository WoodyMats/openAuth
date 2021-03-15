package com.woodymats.openauth.models

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.BaseObservable

data class SignUpEntity(
    private var firstName: String,
    private var lastName: String,
    private var email: String,
    private var password: String,
    private var dateOfBirth: String
) : BaseObservable() {

    fun isDataValid(confirmPassword: String): Int {
        return if (TextUtils.isEmpty(password) || TextUtils.isEmpty(email) || TextUtils.isEmpty(firstName) || TextUtils.isEmpty(lastName)
            || TextUtils.isEmpty(confirmPassword))
            0
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            1
        else if (password.length < 6)
            2
        else if (confirmPassword != password)
            3
        else
            -1
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun setPassword(password: String) {
        this.password = password
    }
}
