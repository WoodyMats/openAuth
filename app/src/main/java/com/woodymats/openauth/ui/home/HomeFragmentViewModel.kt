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
import com.woodymats.openauth.repositories.CoursesRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import com.woodymats.openauth.utils.hasInternetConnection
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeFragmentViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = CoursesRepository(getInstance(app))

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

    private val _showLoadingMyCourses: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoadingMyCourses: LiveData<Boolean>
        get() = _showLoadingMyCourses

    private val _showLoadingAllCourses: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoadingAllCourses: LiveData<Boolean>
        get() = _showLoadingAllCourses

    private fun showLoader(loader: MutableLiveData<Boolean>) {
        loader.value = true
    }

    private fun hideLoader(loader: MutableLiveData<Boolean>) {
        loader.value = false
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
            showLoader(_showLoadingMyCourses)
            _enrollments.value = repository.getUserEnrollmentsFromCache(userId)
            hideLoader(_showLoadingMyCourses)
        }
    }

    private fun getAllCoursesFromCache() {
        viewModelScope.launch {
            showLoader(_showLoadingAllCourses)
            _courses.value = repository.getAllCoursesFromCache()
            hideLoader(_showLoadingAllCourses)
        }
    }

    private fun haveToFetchFromServer(): Boolean {
        // TODO(Define a policy to retrieve data from server)
        return false
    }

    private fun getAllCourses() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader(_showLoadingAllCourses)
            try {
                if (hasInternetConnection(app)) {
                    _courses.value = repository.getAllCourses(userToken)
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _callStatus.value = ApiCallStatus.NOINTERNETERROR
                    _courses.value = emptyList()
                }
                hideLoader(_showLoadingAllCourses)
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                _courses.value = emptyList()
                hideLoader(_showLoadingAllCourses)
            }
        }
    }

    private fun getUserEnrollments() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader(_showLoadingMyCourses)
            try {
                if (hasInternetConnection(app)) {
                    _enrollments.value = repository.getUserEnrollmentsFromServer(userToken, userId)
                    _callStatus.value = ApiCallStatus.SUCCESS
                } else {
                    _callStatus.value = ApiCallStatus.NOINTERNETERROR
                    _enrollments.value = emptyList()
                }
                hideLoader(_showLoadingMyCourses)
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    403 -> _callStatus.value = ApiCallStatus.AUTHERROR
                    404 -> _callStatus.value = ApiCallStatus.SERVERERROR
                    else -> _callStatus.value = ApiCallStatus.UNKNOWNERROR
                }
                _enrollments.value = emptyList()
                hideLoader(_showLoadingMyCourses)
            }
        }
    }
}