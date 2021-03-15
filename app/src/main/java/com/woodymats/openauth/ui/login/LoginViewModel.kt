package com.woodymats.openauth.ui.login

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.R
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.models.User
import com.woodymats.openauth.repositories.LoginRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginViewModel(private val app: Application) : AndroidViewModel(app) {

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

    private val _goToSignUp: MutableLiveData<Boolean> = MutableLiveData(false)

    val goToSignUp: LiveData<Boolean>
        get() = _goToSignUp

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

    private var _errorMessage: MutableLiveData<String> = MutableLiveData("")

    val errorMessage: LiveData<String>
        get() = _errorMessage

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
        when(user.isDataValid()) {
            -1 -> {
                _errorMessage.value = ""
                userLogin(user)
            }
            0 -> _errorMessage.value = app.getString(R.string.password_empty)
            1 -> _errorMessage.value = app.getString(R.string.email_empty)
            2 -> _errorMessage.value = app.getString(R.string.email_invalid)

        }
    }

    fun onSignUpClicked(v: View) {
        _goToSignUp.value = true
    }

    private fun showLoader() {
        _showLoading.value = true
    }

    private fun hideLoader() {
        _showLoading.value = false
    }

    private fun userLogin(loginEntity: LoginEntity) {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader()
            try {
                if (hasInternetConnection(app)) {
                    repository.loginUser(loginEntity)
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _callStatus.value = ApiCallStatus.NOINTERNETERROR
                }
                hideLoader()
            } catch (e: HttpException) {
                when (e.code()) {
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                hideLoader()
            }
        }
    }

    fun onSignUpNavigateFinish() {
        _goToSignUp.value = false
        _showLoading.value = false
        _callStatus.value = ApiCallStatus.LOADING
        _errorMessage.value = ""
    }
}