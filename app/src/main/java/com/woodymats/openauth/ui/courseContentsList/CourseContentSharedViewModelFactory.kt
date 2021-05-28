package com.woodymats.openauth.ui.courseContentsList

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.databases.AppDatabase

class CourseContentSharedViewModelFactory(
    private val sharedPreferences: SharedPreferences,
    private val database: AppDatabase
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CourseContentSharedViewModel(sharedPreferences, database) as T
    }
}