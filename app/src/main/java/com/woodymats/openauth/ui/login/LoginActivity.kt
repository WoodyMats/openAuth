package com.woodymats.openauth.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
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
    lateinit var repository: LoginRepository
    lateinit var database: AppDatabase
    private val viewModel: LoginViewModel by lazy {
        ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        val application = requireNotNull(this).application
        database = AppDatabase.getInstance(application)
        repository = LoginRepository(RetrofitClient.apiInterface, database.userDAO)
        val viewModelFactory = LoginViewModelFactory(application, repository)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        viewModel.callStatus.observe(this, Observer {
            when (it) {
                ApiCallStatus.ERROR -> {
                    binding.loading.visibility = GONE
                    Snackbar.make(binding.root, R.string.password_empty, Snackbar.LENGTH_LONG)
                        .show()
                }
                ApiCallStatus.LOADING -> binding.loading.visibility = VISIBLE
                // Snackbar.make(binding.root, R.string.email_invalid, Snackbar.LENGTH_LONG)
                //         .show()
                else -> {
                    binding.loading.visibility = GONE
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