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
import com.woodymats.openauth.models.SignUpEntity
import com.woodymats.openauth.repositories.UserRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Locale

class SignUpViewModel(private val app: Application) : AndroidViewModel(app) {

    private var user: SignUpEntity = SignUpEntity("", "", "", "")
    private val repository: UserRepository = UserRepository()
    private var confirmPasswordText: String = ""
    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private var _firstNameErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val firstNameErrorMessage: LiveData<String>
        get() = _firstNameErrorMessage

    private var _lastNameErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val lastNameErrorMessage: LiveData<String>
        get() = _lastNameErrorMessage

    private var _emailErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val emailErrorMessage: LiveData<String>
        get() = _emailErrorMessage

    private var _passwordErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val passwordErrorMessage: LiveData<String>
        get() = _passwordErrorMessage

    private var _confirmPasswordErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val confirmPasswordErrorMessage: LiveData<String>
        get() = _confirmPasswordErrorMessage

    private var _dateOfBirthErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val dateOfBirthErrorMessage: LiveData<String>
        get() = _dateOfBirthErrorMessage

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

    val dateOfBirthTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().isNullOrEmpty()) {
                    val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
                    val date = dateFormat.parse(s.toString())
                    user.setDateOfBirth(date?.time ?: -1)
                } else {
                    _dateOfBirthErrorMessage.value = app.getString(R.string.field_required)
                }
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

    val firstNameFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (user.isFirstNameValid()) {
                    0 -> _firstNameErrorMessage.value = app.getString(R.string.field_required)
                    else -> _firstNameErrorMessage.value = null
                }
            } else {
                _firstNameErrorMessage.value = null
            }
        }

    val lastNameFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (user.isLastNameValid()) {
                    0 -> _lastNameErrorMessage.value = app.getString(R.string.field_required)
                    else -> _lastNameErrorMessage.value = null
                }
            } else {
                _lastNameErrorMessage.value = null
            }
        }

    val emailFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (user.isEmailValid()) {
                    0 -> _emailErrorMessage.value = app.getString(R.string.email_empty)
                    1 -> _emailErrorMessage.value = app.getString(R.string.email_invalid)
                    else -> _emailErrorMessage.value = null
                }
            } else {
                _emailErrorMessage.value = null
            }
        }

    val passwordFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when {
                    user.isPasswordValid() == 0 -> {
                        _passwordErrorMessage.value = app.getString(R.string.password_empty)
                    }
                    user.isPasswordValid() == 1 -> {
                        _passwordErrorMessage.value = app.getString(R.string.set_bigger_password)
                    }
                    else -> {
                        _passwordErrorMessage.value = null
                    }
                }
            } else {
                _passwordErrorMessage.value = null
            }
        }

    val confirmPasswordFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when {
                    user.isConfirmPasswordValid(confirmPasswordText) == 0 -> {
                        _confirmPasswordErrorMessage.value = app.getString(R.string.password_empty)
                    }
                    user.isConfirmPasswordValid(confirmPasswordText) == 1 -> {
                        _confirmPasswordErrorMessage.value =
                            app.getString(R.string.set_bigger_password)
                    }
                    user.isConfirmPasswordValid(confirmPasswordText) == 2 -> {
                        _confirmPasswordErrorMessage.value =
                            app.getString(R.string.passwords_do_not_match)
                    }
                    else -> {
                        _confirmPasswordErrorMessage.value = null
                    }
                }
            } else {
                _confirmPasswordErrorMessage.value = null
            }
        }

    fun onSignUpClicked(v: View) {
        if (user.isDataValid(confirmPasswordText)) {
            _firstNameErrorMessage.value = null
            _lastNameErrorMessage.value = null
            _emailErrorMessage.value = null
            _passwordErrorMessage.value = null
            _confirmPasswordErrorMessage.value = null
            _dateOfBirthErrorMessage.value = null
            createAccount()
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
                } catch (e: HttpException) {
                    when (e.code()) {
                        403 -> _callStatus.value = ApiCallStatus.USEREXISTS
                        404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                        else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                    }
                }
                hideLoader()
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