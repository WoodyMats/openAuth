package com.woodymats.openauth.repositories

import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.network.RetrofitClient

class SignUpRepository() {

    suspend fun registerUser(signUpEntity: SignUpEntity) {
        RetrofitClient.apiInterface.createAccount(signUpEntity)
    }
}