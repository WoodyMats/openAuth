package com.woodymats.openauth.ui.home

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.repositories.HomeRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import com.woodymats.openauth.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeFragmentViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = HomeRepository(getInstance(app))

    private val userToken: String = "Bearer " + app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(USER_TOKEN, "")
    private val userId: Long = app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getLong(USER_ID, 0L)

    private var _enrollments: MutableLiveData<List<Enrollment>> = MutableLiveData(emptyList())
    val enrollments: LiveData<List<Enrollment>>
        get() = _enrollments

    private var _courses: MutableLiveData<List<Course>> = MutableLiveData(emptyList())
    val courses: LiveData<List<Course>>
        get() = _courses

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()

    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private val _showLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoading: LiveData<Boolean>
        get() = _showLoading

    private fun showLoader() {
        _showLoading.value = true
    }

    private fun hideLoader() {
        _showLoading.value = false
    }

    init {
        if (haveToFetchFromServer()) {
            getUserEnrollments()
            getAllCourses()
        } else {
            getUserEnrollmentsFromCache()
            getAllCoursesFromCache()
        }
    }

    private fun getUserEnrollmentsFromCache() {
        viewModelScope.launch {
            _enrollments.value = repository.getUserEnrollmentsFromCache(userId)
        }
    }

    private fun getAllCoursesFromCache() {
        viewModelScope.launch {
            _courses.value = repository.getAllCoursesFromCache()
        }
    }

    private fun haveToFetchFromServer(): Boolean {
        // TODO(Define a policy to retrieve data from server)
        return true
    }

    private fun getAllCourses() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader()
            try {
                if (hasInternetConnection(app)) {
                    _courses.value = repository.getAllCourses(userToken)
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _callStatus.value = ApiCallStatus.NOINTERNETERROR
                    _courses.value = emptyList()
                }
                hideLoader()
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                _courses.value = emptyList()
                hideLoader()
            }
        }
    }

    private fun getUserEnrollments() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader()
            try {
                if (hasInternetConnection(app)) {
                    _enrollments.value = repository.getUserEnrollmentsFromServer(userToken, userId)
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _callStatus.value = ApiCallStatus.NOINTERNETERROR
                    _enrollments.value = emptyList()
                }
                hideLoader()
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                _enrollments.value = emptyList()
                hideLoader()
            }
        }
    }
}