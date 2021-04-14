package com.woodymats.openauth.repositories

import android.content.SharedPreferences
import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.Chapter
import com.woodymats.openauth.models.ChapterNetworkEntity
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.CourseNetworkEntity
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.models.LoginEntity
import com.woodymats.openauth.network.RetrofitClient
import com.woodymats.openauth.utils.IS_USER_LOGGED_IN
import com.woodymats.openauth.utils.USER_TOKEN

class LoginRepository(private val database: AppDatabase) {

    suspend fun loginUser(loginEntity: LoginEntity, preferences: SharedPreferences) {
        val enrollments = RetrofitClient.apiInterface.getUserEnrollments("Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMmNiOWMxYmZkNDBkODdlM2MwNmNhM2IxZGY1ZmQ2YWMxMWIyMTY0YWVlNjI1ZGY5MWYwMGQzMDc0YjBmOGJmZGQ2MWZjNzVkN2M2MDIzNWIiLCJpYXQiOjE2MTg0Mjc1MTIuMDEzNDMsIm5iZiI6MTYxODQyNzUxMi4wMTM0MzUsImV4cCI6MTYxODQ0OTExMS45MTU4ODIsInN1YiI6IjciLCJzY29wZXMiOltdfQ.AYPe0vJjuuRJeSucnq40HOE7lkyqwCAmuVrgNg3_2LsbrWIX3OEkbbLc7vkZTBIUCu89RiVagv3-qxsSv0jHVQ")
        for (enrollment in enrollments) {
            val enrollmentDatabase = Enrollment(mapToCourseEntity(enrollment.course), mapToChapterListEntity(enrollment.course.chapters))
            database.courseDAO.insertEnrollment(enrollmentDatabase)
        }
        val userFromNetwork = RetrofitClient.apiInterface.loginUser(loginEntity)
        database.userDAO.insertUser(userFromNetwork)
        preferences.edit().putBoolean(IS_USER_LOGGED_IN, true).putString(USER_TOKEN, userFromNetwork.token).apply()
    }

    private fun mapToCourseEntity(networkEntity: CourseNetworkEntity): Course {
        return Course(
            id = networkEntity.id,
            title = networkEntity.title,
            description = networkEntity.description,
            courseImage = networkEntity.courseImage,
            author = networkEntity.author
        )
    }

    private fun mapToChapterEntity(networkEntity: ChapterNetworkEntity): Chapter {
        return Chapter(
            chapterId = networkEntity.chapterId,
            title = networkEntity.title,
            description = networkEntity.description,
            chapterImage = networkEntity.chapterImage,
            order = networkEntity.order,
            courseId = networkEntity.courseId
        )
    }

    private fun mapToChapterListEntity(networkEntityList: List<ChapterNetworkEntity>): List<Chapter> {
        return networkEntityList.map { mapToChapterEntity(it) }
    }
}