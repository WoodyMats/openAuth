package com.woodymats.openauth.ui.courseDetails

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.databases.AppDatabase

class CourseDetailsViewModelFactory(private val sharedPreferences: SharedPreferences, private val database: AppDatabase, private val courseId: Long) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseDetailsViewModel(sharedPreferences, database, courseId) as T
    }
}