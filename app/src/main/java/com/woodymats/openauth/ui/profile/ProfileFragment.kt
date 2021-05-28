package com.woodymats.openauth.ui.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.woodymats.openauth.R
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.databinding.ProfileFragmentBinding
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.getRealPathFromUri
import com.woodymats.openauth.utils.hideKeyboard
import java.io.File
import java.io.IOException
import java.lang.String
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: ProfileFragmentBinding
    private var fragmentContext: Context? = null

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
                val updatedProfileImage: Bitmap?
                try {
                    val tempFile = File(getRealPathFromUri(resultIntent?.data, fragmentContext!!) ?: "")
                    val fileSize: Int = String.valueOf(tempFile.length() / 1048576).toInt()
                    if (fileSize <= 2) {
                        updatedProfileImage = BitmapFactory.decodeStream(
                            fragmentContext!!.contentResolver.openInputStream(resultIntent!!.data!!)
                        )
                        binding.profileImageView.setImageBitmap(updatedProfileImage)
                    } else {
                        Snackbar.make(binding.root, R.string.image_over_limit, Snackbar.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(
                        fragmentContext!!,
                        getString(R.string.error_fetching_image_from_gallery),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        setUpViewModel()
    }

    private fun setUpViewModel() {
        val viewModelFactory =
            ProfileViewModelFactory(getInstance(fragmentContext!!), requireActivity().application)
        viewModel = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(ProfileViewModel::class.java)
    }

    private fun setUpListeners() {
        binding.dateOfBirthEditText.setOnClickListener {
            showDatePicker()
            fragmentContext?.hideKeyboard(it)
        }

        binding.profileImageView.setOnClickListener {
            onRequestPermission()
        }

        binding.replaceImageView.setOnClickListener {
            onRequestPermission()
        }

        binding.dateOfBirthEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                showDatePicker()
                fragmentContext?.hideKeyboard(v)
            }
        }
    }

    private fun setUpObservers() {
        viewModel.firstNameErrorMessage.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.fistNameEditTextLayout.error = null
            } else {
                binding.fistNameEditTextLayout.error = it
            }
        })

        viewModel.lastNameErrorMessage.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.lastNameEditTextLayout.error = null
            } else {
                binding.lastNameEditTextLayout.error = it
            }
        })

        viewModel.dateOfBirthErrorMessage.observe(viewLifecycleOwner, {
            if (it.isNullOrEmpty()) {
                binding.dateOfBirthEditTextLayout.error = null
            } else {
                binding.dateOfBirthEditTextLayout.error = it
            }
        })

        viewModel.generalErrorMessage.observe(viewLifecycleOwner, {
            if (!it.isNullOrEmpty()) {
                Snackbar.make(
                    binding.root,
                    it,
                    com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
                ).show()
            }
        })

        viewModel.callStatus.observe(viewLifecycleOwner, {
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
                    R.string.error_to_identify_user,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SERVERERROR -> Snackbar.make(
                    binding.root,
                    R.string.server_error,
                    Snackbar.LENGTH_LONG
                ).show()

                ApiCallStatus.SUCCESS -> {
                    // finish()
                }
                else -> { //Nothing to do!
                }
            }
        })
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
        picker.show(requireFragmentManager(), "DATE_PICKER")
    }

    private fun onRequestPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                openPhotoGallery()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ProfileFragmentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setUpListeners()
        setUpObservers()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                viewModel.toggleEditMode()
                Toast.makeText(
                    fragmentContext,
                    "Edit is " + if (viewModel.editMode.value!!) "ON" else "OFF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }

    override fun onDetach() {
        fragmentContext = null
        super.onDetach()
    }
}