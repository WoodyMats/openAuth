package com.woodymats.openauth.ui.splash

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN

class SplashViewModel(private val preferences: SharedPreferences) : ViewModel() {

    private var _isUserLoggedIn: MutableLiveData<Boolean> = MutableLiveData(false)
    val isUserLoggedIn: LiveData<Boolean>
        get() = _isUserLoggedIn

    init {
        _isUserLoggedIn.value = preferences.getBoolean(IS_USER_LOGGED_IN, false)
    }
}