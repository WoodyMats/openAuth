package com.woodymats.openauth.ui.login

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.User
import com.woodymats.openauth.repositories.LoginRepository
import com.woodymats.openauth.utils.ApiCallStatus
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(app: Application, private val repository: LoginRepository) :
    AndroidViewModel(app) {

    private val user: LoginEntity = LoginEntity("", "")

    private val _dataState: MutableLiveData<User> = MutableLiveData()

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

    fun userLogin(loginEntity: LoginEntity) {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            try {
                val userFromNetwork = repository.api.loginUser(loginEntity)
                repository.userDAO.insertUser(userFromNetwork)
                _dataState.value = repository.userDAO.getUser()
            } catch (e: Exception) {
                _callStatus.value = ApiCallStatus.ERROR
            }
        }
    }
}