package com.woodymats.openauth.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.woodymats.openauth.MainActivity
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.ActivityLoginBinding
import com.woodymats.openauth.ui.signup.SignUpActivity
import com.woodymats.openauth.utils.ApiCallStatus

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpObservers()
        setContentView(binding.root)
    }

    private fun setUpObservers() {
        viewModel.goToSignUp.observe(this, {
            if (it) {
                startActivity(Intent(this@LoginActivity, SignUpActivity::class.java))
                viewModel.onSignUpNavigateFinish()
            }
        })

        viewModel.emailErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.emailEditTextLayout.error = null
            } else {
                binding.emailEditTextLayout.error = it
            }
        })

        viewModel.passwordErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.passwordEditTextLayout.error = null
            } else {
                binding.passwordEditTextLayout.error = it
            }
        })

        // viewModel.errorMessage.observe(this, {
        //     if (!it.isNullOrEmpty()) {
        //         Snackbar.make(
        //             binding.root,
        //             it,
        //             Snackbar.LENGTH_LONG
        //         ).show()
        //     }
        // })

        viewModel.callStatus.observe(this, {
            when (it) {
                ApiCallStatus.UNKNOWNERROR -> Snackbar.make(
                    binding.root,
                    R.string.unknown_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.NOINTERNETERROR -> Snackbar.make(
                    binding.root,
                    R.string.no_internet_connection,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.AUTHERROR -> Snackbar.make(
                    binding.root,
                    R.string.wrong_credentials,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SERVERERROR -> Snackbar.make(
                    binding.root,
                    R.string.server_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SUCCESS -> {
                    startActivity(
                        Intent(this@LoginActivity, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    )
                    finish()
                }
            }
        })
    }

    private fun setUpViewModel() {
        binding.lifecycleOwner = this
        val application = requireNotNull(this).application
        val viewModelFactory = LoginViewModelFactory(application)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        // binding.executePendingBindings()
    }
}