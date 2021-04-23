package com.woodymats.openauth

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.repositories.UserRepository
import com.woodymats.openauth.utils.ApiCallStatus
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class MainActivityViewModel(
    private val token: String,
    private val database: AppDatabase,
    private val preferences: SharedPreferences
) : ViewModel() {

    private val repository: UserRepository = UserRepository()

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    fun logoutUser() {
        viewModelScope.launch {
            try {
                _callStatus.value = ApiCallStatus.LOADING
                repository.logoutUser(database, "Bearer $token", preferences)
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
}