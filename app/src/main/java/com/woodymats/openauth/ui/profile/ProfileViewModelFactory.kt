package com.woodymats.openauth.ui.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.woodymats.openauth.databases.AppDatabase

class ProfileViewModelFactory(private val database: AppDatabase, private val app: Application) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfileViewModel(database, app) as T
    }
}