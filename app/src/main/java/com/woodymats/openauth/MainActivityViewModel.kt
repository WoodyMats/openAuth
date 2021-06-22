package com.woodymats.openauth

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.local.UserEntity
import com.woodymats.openauth.repositories.UserRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.TAG_OUTPUT
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainActivityViewModel(
    private val app: Application,
    private val token: String,
    private val database: AppDatabase,
    private val preferences: SharedPreferences
) : AndroidViewModel(app) {

    private val repository: UserRepository = UserRepository(database)

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private var _user: MutableLiveData<UserEntity> = MutableLiveData(null)
    val user: LiveData<UserEntity>
        get() = _user

    fun logoutUser() {
        viewModelScope.launch {
            try {
                _callStatus.value = ApiCallStatus.LOADING
                repository.logoutUser(database, token, preferences)
                WorkManager.getInstance(app).cancelAllWorkByTag(TAG_OUTPUT)
                WorkManager.getInstance(app).pruneWork()
                _callStatus.value = ApiCallStatus.SUCCESS
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
            } catch (e: Exception) {
                _callStatus.value = ApiCallStatus.NOINTERNETERROR
            }
        }
    }

    fun refreshUser() {
        viewModelScope.launch {
            _user.value = repository.getUser()
        }
    }
}
