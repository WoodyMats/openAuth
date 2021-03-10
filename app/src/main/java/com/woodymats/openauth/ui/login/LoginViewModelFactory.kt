package com.woodymats.openauth.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.repositories.LoginRepository

class LoginViewModelFactory (private val app: Application, private val repository: LoginRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(app, repository) as T
    }
}