package com.woodymats.openauth.ui.home

import android.app.Application
import android.content.Context.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.repositories.CoursesRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.DOWNLOAD_DATA_FROM_SERVER_WORK_NAME
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.TAG_OUTPUT
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import com.woodymats.openauth.utils.hasInternetConnection
import com.woodymats.openauth.workers.DownloadCoursesFromServerWorker
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeFragmentViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = CoursesRepository(getInstance(app))

    private val workManager = WorkManager.getInstance(app)
    internal val outputWorkInfo: LiveData<List<WorkInfo>>

    private val userToken: String =
        app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(USER_TOKEN, "").toString()
    private val userId: Long =
        app.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getLong(USER_ID, 0L)

    private var _enrollments: MutableLiveData<List<Enrollment>> = MutableLiveData(emptyList())
    val enrollments: LiveData<List<Enrollment>>
        get() = _enrollments

    private var _courses: MutableLiveData<List<CourseEntity>> = MutableLiveData(emptyList())
    val courses: LiveData<List<CourseEntity>>
        get() = _courses

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()
    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private val _showLoadingMyCourses: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoadingMyCourses: LiveData<Boolean>
        get() = _showLoadingMyCourses

    private val _showLoader: MutableLiveData<Boolean> = MutableLiveData(false)

    val showLoader: LiveData<Boolean>
        get() = _showLoader

    private fun showLoader() {
        _showLoader.value = true
    }

    private fun hideLoader() {
        _showLoader.value = false
    }

    private fun enqueueWorkIfCourseTableIsEmpty() {
        viewModelScope.launch {
            val isEmpty = repository.isCoursesTableEmpty()
            if (isEmpty) {
                workManager.enqueueUniqueWork(
                    DOWNLOAD_DATA_FROM_SERVER_WORK_NAME,
                    ExistingWorkPolicy.REPLACE,
                    OneTimeWorkRequestBuilder<DownloadCoursesFromServerWorker>()
                        .addTag(TAG_OUTPUT)
                        .setConstraints(Constraints.Builder()
                            .setRequiresBatteryNotLow(true)
                            .setRequiredNetworkType(NetworkType.UNMETERED)
                            .build()
                        ).build()
                )
            }
        }
    }

    init {

        showLoader()

        outputWorkInfo = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        enqueueWorkIfCourseTableIsEmpty()

        // if (haveToFetchFromServer()) {
            // clearAllCourseRelatedData()
        //     getAllCourses()
            // getUserEnrollments()
        // } else {
        //     getUserEnrollmentsFromCache()
        //     getAllCoursesFromCache()
        // }
    }

    fun getCoursesAndEnrollmentsFromCache() {
        getAllCoursesFromCache()
        getUserEnrollmentsFromCache()
        hideLoader()
    }

    private suspend fun clearAllCourseRelatedData() {
        repository.clearCourseChaptersAnContentsTables()
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
        return hasInternetConnection(app)
    }

    private fun getAllCourses() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader()
            try {
                if (hasInternetConnection(app)) {
                    clearAllCourseRelatedData()
                    _courses.value = repository.downloadAllCoursesAndGetThemFromCache(userToken)
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
            getUserEnrollments()
        }
    }

    private fun getUserEnrollments() {
        viewModelScope.launch {
            _callStatus.value = ApiCallStatus.LOADING
            showLoader()
            try {
                if (hasInternetConnection(app)) {
                    _enrollments.value = repository.downloadUserEnrollmentsAndGetThemFromCache(userToken, userId)
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