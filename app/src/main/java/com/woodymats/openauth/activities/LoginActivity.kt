package com.woodymats.openauth.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.woodymats.openauth.interfaces.LoginResultCallBack
import com.woodymats.openauth.MainActivity
import com.woodymats.openauth.R
import com.woodymats.openauth.viewModels.LoginViewModel
import com.woodymats.openauth.viewModels.LoginViewModelFactory
import com.woodymats.openauth.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val viewModelFactory = LoginViewModelFactory(application)
        binding.viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        viewModel.loginButtonHandler.observe(this, Observer {
            when(it) {
                0 -> Snackbar.make(binding.root, R.string.password_empty, Snackbar.LENGTH_LONG)
                    .show()
                1 -> Snackbar.make(binding.root, R.string.email_empty, Snackbar.LENGTH_LONG)
                    .show()
                2 -> Snackbar.make(binding.root, R.string.email_invalid, Snackbar.LENGTH_LONG)
                    .show()
                else -> {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                    // "Have to add API call for login!! (onSuccess)"
                }
            }
        })

        setContentView(binding.root)
    }

}