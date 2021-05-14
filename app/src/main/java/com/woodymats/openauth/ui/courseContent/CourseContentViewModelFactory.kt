package com.woodymats.openauth.ui.courseContent

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.databases.AppDatabase

class CourseContentViewModelFactory(
    private val sharedPreferences: SharedPreferences,
    private val database: AppDatabase,
    private val chapterId: Long
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseContentViewModel(sharedPreferences, database, chapterId) as T
    }
}