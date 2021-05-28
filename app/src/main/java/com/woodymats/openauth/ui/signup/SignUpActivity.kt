package com.woodymats.openauth.ui.signup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.woodymats.openauth.R
import com.woodymats.openauth.databinding.ActivitySignUpBinding
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.hideKeyboard
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by lazy {
        ViewModelProvider(this).get(SignUpViewModel::class.java)
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openPhotoGallery()
            } else {
                requestPermissionWithExplanation()
            }
        }
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                viewModel.setProfileImageFile(resultIntent?.data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setUpViewModel()
        setUpObservers()
        setUpListeners()
        setContentView(binding.root)
    }

    private fun setUpListeners() {
        binding.dateOfBirthEditText.setOnClickListener {
            showDatePicker()
            hideKeyboard(it)
        }

        binding.profileImageUploadButton.setOnClickListener {
            onRequestPermission()
        }

        binding.dateOfBirthEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePicker()
                hideKeyboard(v)
            }
        }
    }

    private fun onRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openPhotoGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) -> {
                requestPermissionWithExplanation()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun openPhotoGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.type = "image/*"
        pickImageLauncher.launch(galleryIntent)
    }

    private fun requestPermissionWithExplanation() {
        Snackbar.make(
            binding.root,
            getString(R.string.write_files_permission_required),
            Snackbar.LENGTH_INDEFINITE,
        ).setAction("OK") {
            requestPermissionLauncher.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }.show()
    }

    private fun setUpObservers() {
        viewModel.profileImageFile.observe(this, {
            if (it != null) {
                binding.profileImageUploadButton.text = it.name
            }
        })

        viewModel.firstNameErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.fistNameEditTextLayout.error = null
            } else {
                binding.fistNameEditTextLayout.error = it
            }
        })

        viewModel.lastNameErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.lastNameEditTextLayout.error = null
            } else {
                binding.lastNameEditTextLayout.error = it
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

        viewModel.confirmPasswordErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.confirmPasswordEditTextLayout.error = null
            } else {
                binding.confirmPasswordEditTextLayout.error = it
            }
        })

        viewModel.dateOfBirthErrorMessage.observe(this, {
            if (it.isNullOrEmpty()) {
                binding.dateOfBirthEditTextLayout.error = null
            } else {
                binding.dateOfBirthEditTextLayout.error = it
            }
        })

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

                ApiCallStatus.USEREXISTS -> Snackbar.make(
                    binding.root,
                    R.string.user_exists,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SERVERERROR -> Snackbar.make(
                    binding.root,
                    R.string.server_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SUCCESS -> {
                    finish()
                }
                else -> { //Nothing to do!
                }
            }
        })
    }

    private fun setUpViewModel() {
        binding.lifecycleOwner = this
        val application = requireNotNull(this).application
        val viewModelFactory = SignUpViewModelFactory(application)
        binding.viewModel =
            ViewModelProvider(this, viewModelFactory).get(SignUpViewModel::class.java)
    }

    private fun showDatePicker() {
        val calendar = MaterialDatePicker.Builder.datePicker()
        calendar.setInputMode(MaterialDatePicker.INPUT_MODE_TEXT)
        calendar.setTitleText(R.string.date_of_birth)
        calendar.setCalendarConstraints(
            CalendarConstraints.Builder().setEnd(
                Calendar.getInstance(
                    Locale.getDefault()
                ).timeInMillis
            ).build()
        )
        val picker = calendar.build()
        picker.addOnPositiveButtonClickListener { dateLong ->
            val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
            val dateString = dateFormat.format(Date(dateLong))
            binding.dateOfBirthEditText.setText(dateString)
        }
        picker.show(supportFragmentManager, "DATE_PICKER")
    }
}