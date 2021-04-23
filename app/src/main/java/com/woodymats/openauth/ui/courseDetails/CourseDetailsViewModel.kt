package com.woodymats.openauth.ui.courseDetails

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class CourseDetailsViewModel(private val app: Application, private val preferences: SharedPreferences) : AndroidViewModel(app) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }

    val text: LiveData<String> = _text
}