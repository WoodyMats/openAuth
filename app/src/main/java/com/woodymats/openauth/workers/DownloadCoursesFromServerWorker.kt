package com.woodymats.openauth.workers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.woodymats.openauth.databases.getInstance
import com.woodymats.openauth.repositories.CoursesRepository
import com.woodymats.openauth.utils.DOWNLOAD_DATA_FROM_SERVER_WORK_NAME
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.TAG_OUTPUT
import com.woodymats.openauth.utils.USER_ID
import com.woodymats.openauth.utils.USER_TOKEN
import com.woodymats.openauth.utils.WORKER_STATUS
import java.util.concurrent.TimeUnit

class DownloadCoursesFromServerWorker(ctx: Context, params: WorkerParameters) :
    CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        return try {
            val repository = CoursesRepository(getInstance(appContext))
            val userToken = appContext.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getString(
                USER_TOKEN, ""
            )
            val userId = appContext.getSharedPreferences(PREFERENCES, MODE_PRIVATE).getLong(
                USER_ID, -1L
            )
            val downloadedCourses = repository.getAllCoursesFromServer(userToken)
            val downloadedUserEnrollments = repository.downloadUserEnrollmentsFromServer(userToken)
            repository.clearCourseChaptersAnContentsTables()
            repository.insertDownloadedCoursesIntoDatabase(downloadedCourses)
            repository.insertUserEnrollmentsIntoDatabase(downloadedUserEnrollments, userId)

            val dailyWorkRequest = OneTimeWorkRequestBuilder<DownloadCoursesFromServerWorker>()
                .setInitialDelay(24, TimeUnit.HOURS)
                .setConstraints(
                    Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build())
                .addTag(TAG_OUTPUT)
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                DOWNLOAD_DATA_FROM_SERVER_WORK_NAME,
                ExistingWorkPolicy.APPEND_OR_REPLACE,
                dailyWorkRequest
            )

            Result.success(workDataOf(WORKER_STATUS to true))
        } catch (t: Throwable) {
            Result.failure(workDataOf(WORKER_STATUS to false))
        }
    }
}