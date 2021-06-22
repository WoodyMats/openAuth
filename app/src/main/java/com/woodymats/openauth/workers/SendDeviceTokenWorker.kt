package com.woodymats.openauth.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.woodymats.openauth.repositories.UserRepository
import com.woodymats.openauth.utils.PREFERENCES
import com.woodymats.openauth.utils.USER_TOKEN

class SendDeviceTokenWorker(ctx: Context, params: WorkerParameters) :
CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val appContext = applicationContext

        return try {
            val repository = UserRepository()
            val deviceId = inputData.getString("deviceId") ?: ""
            val userToken = appContext.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(
                USER_TOKEN, ""
            ) ?: ""
            repository.sendDeviceToken(userToken, deviceId)

            Result.success()
        } catch (t: Throwable) {
            Result.failure()
        }
    }
}