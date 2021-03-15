package com.woodymats.openauth.ui.signup

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
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.repositories.SignUpRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpViewModel (private val app: Application) : AndroidViewModel(app) {

    private var user: SignUpEntity = SignUpEntity("", "", "", "", "")
    private val repository: SignUpRepository = SignUpRepository(getInstance(app))
    private var confirmPasswordText: String = ""
    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private var _errorMessage: MutableLiveData<String> = MutableLiveData("")

    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoading: LiveData<Boolean>
        get() = _showLoading

    val firstNameTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.setFirstName(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    val lastNameTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.setLastName(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

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

    val confirmPasswordTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                confirmPasswordText = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    fun onSignUpClicked(v: View) {
        when (user.isDataValid(confirmPasswordText)) {
            -1 -> {
                _errorMessage.value = ""
                createAccount()
            }
            0 -> _errorMessage.value = app.getString(R.string.fill_all_fields)
            1 -> _errorMessage.value = app.getString(R.string.email_invalid)
            2 -> _errorMessage.value = app.getString(R.string.set_bigger_password)
        }
    }

    private fun createAccount() {
        viewModelScope.launch {
            if (hasInternetConnection(app)) {
                try {
                    showLoader()
                    _callStatus.value = ApiCallStatus.LOADING
                    repository.registerUser(user)
                    _callStatus.value = ApiCallStatus.SUCCESS
                    hideLoader()
                } catch (e : HttpException) {
                    hideLoader()
                    when (e.code()) {
                        403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                        404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                        else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                    }
                }
            } else {
                _callStatus.value = ApiCallStatus.NOINTERNETERROR
            }
        }
    }

    private fun showLoader() {
        _showLoading.value = true
    }

    private fun hideLoader() {
        _showLoading.value = false
    }
}