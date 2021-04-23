package com.woodymats.openauth.ui.courseDetails

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.ui.login.LoginViewModel

class CourseDetailsViewModelFactory(private val app: Application, private val sharedPreferences: SharedPreferences) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseDetailsViewModel(app, sharedPreferences) as T
    }
}