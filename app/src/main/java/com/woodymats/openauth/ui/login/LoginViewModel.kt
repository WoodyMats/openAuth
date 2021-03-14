package com.woodymats.openauth.ui.login

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.User
import com.woodymats.openauth.repositories.LoginRepository
import com.woodymats.openauth.utils.ApiCallStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val user: LoginEntity = LoginEntity("", "")

    private val repository = LoginRepository(getInstance(app))

    private var _dataState: MutableLiveData<User> = MutableLiveData()

    val dataState: LiveData<User>
        get() = _dataState

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoading: LiveData<Boolean>
        get() = _showLoading

    //create function to set Email after user finish enter text
    val emailTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.setEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    var errorMessage = ""

    //create function to set Password after user finish enter text
    val passwordTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.setPassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    //create function to process Login Button clicked
    fun onLoginClicked(v: View) {
        _callStatus.value = ApiCallStatus.LOADING
        if (user.isDataValid() == -1) {
            userLogin(user)
        }
    }

    private fun userLogin(loginEntity: LoginEntity) {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            _showLoading.value = true
            try {
                delay(2000)
                repository.loginUser(loginEntity)
                _showLoading.value = false
            } catch (e: IOException) {
                Log.d("Hii", e.stackTraceToString())
                _callStatus.value = ApiCallStatus.ERROR
                _showLoading.value = false
            }
        }
    }
}