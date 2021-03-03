package com.woodymats.openauth.viewModels

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.woodymats.openauth.interfaces.LoginResultCallBack
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.R

class LoginViewModel(app: Application): AndroidViewModel(app) {

    private val user: LoginEntity = LoginEntity("", "")

    //create function to set Email after user finish enter text
    val emailTextWatcher: TextWatcher
        get()= object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user .setEmail(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        }

    private val _loginButtonHandler = MutableLiveData<Int>()

    // The external immutable LiveData for the navigation property
    val loginButtonHandler: LiveData<Int>
        get() = _loginButtonHandler

    //create function to set Password after user finish enter text
    val passwordTextWatcher: TextWatcher
        get()= object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user .setPassword(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        }


    //create function to process Login Button clicked
    fun onLoginClicked(v: View) {
        _loginButtonHandler.value = user.isDataValid()
    }
}