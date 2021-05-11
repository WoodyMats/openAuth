package com.woodymats.openauth.ui.courseDetails

import android.content.SharedPreferences
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.CourseEntity
import com.woodymats.openauth.models.EnrollToCourseModel
import com.woodymats.openauth.repositories.CoursesRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.USER_TOKEN
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.lang.Exception

class CourseDetailsViewModel(
    private val preferences: SharedPreferences,
    private val database: AppDatabase,
    private val courseId: Long
) : ViewModel() {

    private var userToken = preferences.getString(USER_TOKEN, "").toString()
    private val repository = CoursesRepository(database)

    private var courseTemp: CourseEntity? = null

    private var _course: MutableLiveData<CourseEntity> = MutableLiveData(null)
    val course: LiveData<CourseEntity>
        get() = _course

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()
    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private val _showLoadingBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val showLoadingBar: LiveData<Boolean>
        get() = _showLoadingBar

    private var _hideEnrollLinear: MutableLiveData<Boolean> = MutableLiveData(false)
    val hideEnrollLinear: LiveData<Boolean>
        get() = _hideEnrollLinear

    init {
        getCourseFromCache()
    }

    private fun showLoader() {
        _showLoadingBar.value = true
    }

    private fun hideLoader() {
        _showLoadingBar.value = false
    }

    private fun getCourseFromCache() {
        viewModelScope.launch {
            _course.value =
                repository.getCourseById(courseId)
            courseTemp = database.courseDAO.getCourseById(courseId)
        }
    }

    fun onEnrollToCourseClicked(v: View) {
        enrollToCourse()
    }

    private fun enrollToCourse() {
        viewModelScope.launch {
            try {
                showLoader()
                repository.enrollToCourse(userToken, EnrollToCourseModel(courseId = courseId))
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
            hideLoader()
        }
    }

    fun hideEnrollLinear() {
        _hideEnrollLinear.value = true
    }
}