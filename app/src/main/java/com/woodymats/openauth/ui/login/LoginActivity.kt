package com.woodymats.openauth.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.woodymats.openauth.MainActivity
import com.woodymats.openauth.R
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.databinding.ActivityLoginBinding
import com.woodymats.openauth.network.RetrofitClient
import com.woodymats.openauth.repositories.LoginRepository
import com.woodymats.openauth.utils.ApiCallStatus

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this).application
        val viewModelFactory = LoginViewModelFactory(application)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
        // binding.executePendingBindings()

        viewModel.callStatus.observe(this, Observer {
            when (it) {
                ApiCallStatus.ERROR -> Snackbar.make(
                    binding.root,
                    R.string.password_empty,
                    Snackbar.LENGTH_LONG
                ).show()
                ApiCallStatus.LOADING -> Snackbar.make(
                    binding.root,
                    R.string.email_invalid,
                    Snackbar.LENGTH_LONG
                ).show()
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