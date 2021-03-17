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

    private var _emailErrorMessage: MutableLiveData<String> = MutableLiveData(null)

    val emailErrorMessage: LiveData<String>
        get() = _emailErrorMessage

    private var _passwordErrorMessage: MutableLiveData<String> = MutableLiveData("")

    val passwordErrorMessage: LiveData<String>
        get() = _passwordErrorMessage

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

    val emailFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                when (user.isEmailValid()) {
                    0 -> _emailErrorMessage.value = app.getString(R.string.email_empty)
                    1 -> _emailErrorMessage.value = app.getString(R.string.email_invalid)
                    else -> _emailErrorMessage.value = null
                }
            }
        }

    val passwordFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (user.isPasswordValid() == 0) {
                    _passwordErrorMessage.value = app.getString(R.string.password_empty)
                } else {
                    _passwordErrorMessage.value = null
                }
            }
        }

    //create function to process Login Button clicked
    fun onLoginClicked(v: View) {
        if ((user.isEmailValid() == -1) && (user.isPasswordValid() == -1)) {
            _emailErrorMessage.value = null
            _passwordErrorMessage.value = null
            userLogin(user)
        } else {
            _emailErrorMessage.value = app.getString(R.string.fill_all_fields)
            _passwordErrorMessage.value = app.getString(R.string.fill_all_fields)
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
        _passwordErrorMessage.value = null
        _passwordErrorMessage.value = null
    }
}