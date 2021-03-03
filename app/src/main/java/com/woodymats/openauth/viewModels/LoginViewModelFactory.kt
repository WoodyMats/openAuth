package com.woodymats.openauth.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.interfaces.LoginResultCallBack

class LoginViewModelFactory (private val app: Application): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(app) as T
    }
}