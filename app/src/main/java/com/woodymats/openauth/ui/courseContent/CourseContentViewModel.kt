package com.woodymats.openauth.ui.courseContent

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.repositories.CoursesRepository
import com.woodymats.openauth.utils.ApiCallStatus
import com.woodymats.openauth.utils.USER_TOKEN
import kotlinx.coroutines.launch

class CourseContentViewModel(
    private val preferences: SharedPreferences,
    private val database: AppDatabase,
    private val chapterId: Long
) : ViewModel() {

    private var userToken = preferences.getString(USER_TOKEN, "").toString()
    private val repository = CoursesRepository(database)

    private var _chapter: MutableLiveData<ChapterEntity> = MutableLiveData(null)
    val chapter: LiveData<ChapterEntity>
        get() = _chapter

    private var _contentsList: MutableLiveData<List<ContentEntity>> = MutableLiveData(emptyList())
    val contentsList: LiveData<List<ContentEntity>>
        get() = _contentsList

    private var _currentContent: MutableLiveData<ContentEntity> = MutableLiveData(null)
    val currentContent: LiveData<ContentEntity>
        get() = _currentContent

    private var currentContentPosition: MutableLiveData<Int> = MutableLiveData(0)

    private val _callStatus: MutableLiveData<ApiCallStatus> = MutableLiveData()
    val callStatus: LiveData<ApiCallStatus>
        get() = _callStatus

    private val _showLoadingBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val showLoadingBar: LiveData<Boolean>
        get() = _showLoadingBar

    init {
        showLoader()
        getChapterAndContentsFromCache()
        hideLoader()
    }

    private fun showLoader() {
        _showLoadingBar.value = true
    }

    private fun hideLoader() {
        _showLoadingBar.value = false
    }

    private fun getChapterAndContentsFromCache() {
        viewModelScope.launch {
            _chapter.value = repository.getChapterById(chapterId)
            _contentsList.value = _chapter.value?.contents ?: emptyList()
            if (_contentsList.value!!.isNotEmpty()) {
                _currentContent.value = _contentsList.value!![currentContentPosition.value ?: 0]
            }
        }
    }

    fun goToNextContent() {
        if ((currentContentPosition.value ?: -1) >= 0 && (currentContentPosition.value ?: -1) <= ((contentsList.value?.size ?: 0) - 1)) {
            currentContentPosition.value =+ 1
            _currentContent.value = _contentsList.value!![currentContentPosition.value ?: 0]
        }
    }

    fun goToPreviousContent() {
        if ((currentContentPosition.value ?: -1) > 0) {
            currentContentPosition.value =- 1
            _currentContent.value = _contentsList.value!![currentContentPosition.value ?: 0]
        }
    }

}