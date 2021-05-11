package com.woodymats.openauth.models

import android.text.TextUtils
import android.util.Patterns
import androidx.databinding.BaseObservable

data class SignUpEntity(
    private var firstName: String,
    private var lastName: String,
    private var email: String,
    private var password: String,
    private var dateOfBirth: Long = -1,
    private var canCreateCourses: Int = 0
) : BaseObservable() {

    fun isFirstNameValid(): Int {
        return if (TextUtils.isEmpty(firstName)) 0 else -1
    }

    fun isLastNameValid(): Int {
        return if (TextUtils.isEmpty(lastName)) 0 else -1
    }

    fun isEmailValid(): Int {
        return if (TextUtils.isEmpty(email)) {
            0
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            1
        } else {
            -1
        }
    }

    fun isPasswordValid(): Int {
        return when {
            TextUtils.isEmpty(password) -> 0
            password.length < 6 -> 1
            else -> -1
        }
    }

    fun isConfirmPasswordValid(confirmPassword: String): Int {
        return when {
            TextUtils.isEmpty(confirmPassword) -> 0
            password.length < 6 -> 1
            confirmPassword != password -> 2
            else -> -1
        }
    }

    fun isDataValid(confirmPasswordText: String): Boolean {
        return isFirstNameValid() == -1 && isLastNameValid() == -1 && isPasswordValid() == -1 && isConfirmPasswordValid(confirmPasswordText) == -1
            && confirmPasswordText == password && dateOfBirth != -1L
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

    fun setDateOfBirth(dateOfBirth: Long) {
        this.dateOfBirth = dateOfBirth
    }
}
