package com.woodymats.openauth.ui.profile

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.R
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.local.UserEntity
import com.woodymats.openauth.repositories.UserRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.USER_TOKEN
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileViewModel(private val database: AppDatabase, private val app: Application) :
    AndroidViewModel(app) {

    private val repository: UserRepository = UserRepository(database)

    private var _profileImageFile: MutableLiveData<File?> = MutableLiveData(null)
    val profileImageFile: LiveData<File?>
        get() = _profileImageFile

    fun setProfileImageFile(profileImageUri: Uri?) {
        try {
            val temp = getRealPathFromUri(profileImageUri) ?: ""
            _profileImageFile.value = File(getRealPathFromUri(profileImageUri) ?: "")
        } catch (e: IOException) {
            // Nothing for now
        }
    }

    private fun getRealPathFromUri(contentUri: Uri?): String? {
        var cursor: Cursor? = null
        return try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = app.contentResolver.query(contentUri!!, proj, null, null, null)
            val columnIndex: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    private var _isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private var _editMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val editMode: LiveData<Boolean>
        get() = _editMode

    private var _user: MutableLiveData<UserEntity> = MutableLiveData(null)
    val user: LiveData<UserEntity>
        get() = _user

    private var _firstNameErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val firstNameErrorMessage: LiveData<String>
        get() = _firstNameErrorMessage

    private var _lastNameErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val lastNameErrorMessage: LiveData<String>
        get() = _lastNameErrorMessage

    private var _dateOfBirthErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val dateOfBirthErrorMessage: LiveData<String>
        get() = _dateOfBirthErrorMessage

    private var _generalErrorMessage: MutableLiveData<String> = MutableLiveData(null)
    val generalErrorMessage: LiveData<String>
        get() = _generalErrorMessage

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()
    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    val firstNameTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.value?.firstName = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    val lastNameTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                user.value?.lastName = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    val firstNameFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (TextUtils.isEmpty(user.value?.firstName)) {
                    true -> _firstNameErrorMessage.value = app.getString(R.string.field_required)
                    else -> _firstNameErrorMessage.value = null
                }
            } else {
                _firstNameErrorMessage.value = null
            }
        }

    val lastNameFocusChangeListener: View.OnFocusChangeListener
        get() = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                when (TextUtils.isEmpty(user.value?.lastName)) {
                    true -> _lastNameErrorMessage.value = app.getString(R.string.field_required)
                    else -> _lastNameErrorMessage.value = null
                }
            } else {
                _lastNameErrorMessage.value = null
            }
        }

    val dateOfBirthTextWatcher: TextWatcher
        get() = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.toString().isNullOrEmpty()) {
                    val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
                    val date = dateFormat.parse(s.toString())
                    user.value?.dateOfBirth = (date?.time ?: -1)
                } else {
                    _dateOfBirthErrorMessage.value = app.getString(R.string.field_required)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        }

    init {
        getUserFromCache()
    }

    fun onSaveButtonClicked(v: View) {
        val userToUpdate = user.value
        if (!(userToUpdate?.firstName.isNullOrEmpty() || userToUpdate?.lastName.isNullOrEmpty() || userToUpdate?.dateOfBirth == -1L)) {
            clearErrorMessages()
            updateUser()
            toggleEditMode()
        } else {
            _generalErrorMessage.value = app.getString(R.string.fill_all_fields)
        }
    }

    private fun clearErrorMessages() {
        _firstNameErrorMessage.value = null
        _lastNameErrorMessage.value = null
        _dateOfBirthErrorMessage.value = null
        _generalErrorMessage.value = null
    }

    private fun updateUser() {
        viewModelScope.launch {
            try {
                showLoader()
                val userToUpdate = user.value!!
                val token =
                    app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(USER_TOKEN, "")
                        ?: ""
                if (profileImageFile.value != null) {
                    _user.value = repository.updateUserWithFile(
                        token,
                        userToUpdate.firstName,
                        userToUpdate.lastName,
                        userToUpdate.dateOfBirth,
                        profileImageFile.value!!
                    )
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _user.value = repository.updateUserWithoutFile(
                        token,
                        userToUpdate.firstName,
                        userToUpdate.lastName,
                        userToUpdate.dateOfBirth
                    )
                    _callStatus.value = ApiCallStatus.SUCCESS
                }
                hideLoader()
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                hideLoader()
            }
        }
    }

    private fun getUserFromCache() {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }

    private fun showLoader() {
        _isLoading.value = true
    }

    private fun hideLoader() {
        _isLoading.value = false
    }

    fun toggleEditMode() {
        _editMode.value = !(_editMode.value ?: false)
    }

    fun showDateOfBirth(): String {
        val dateFormat = SimpleDateFormat("MM/dd/yy", Locale.getDefault())
        return dateFormat.format(Date(user.value?.dateOfBirth ?: -1L))
    }
}