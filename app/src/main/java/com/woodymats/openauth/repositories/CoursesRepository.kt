package com.woodymats.openauth.repositories

import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.Chapter
import com.woodymats.openauth.models.ChapterNetworkEntity
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.CourseEntity
import com.woodymats.openauth.models.CourseNetworkEntity
import com.woodymats.openauth.models.EnrollToCourseModel
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.network.RetrofitClient

class CoursesRepository(private val database: AppDatabase) {

    suspend fun getUserEnrollmentsFromServer(userToken: String?, userId: Long): List<Enrollment> {
        val enrollments = RetrofitClient.apiInterface.getUserEnrollments(userToken!!)
        for (enrollment in enrollments) {
            val enrollmentDatabase = Enrollment(
                mapToCourseEntity(enrollment.course, userId),
                mapToChapterListEntity(enrollment.course.chapters)
            )
            database.courseDAO.insertEnrollment(enrollmentDatabase)
        }
        return getUserEnrollmentsFromCache(userId)
    }

    suspend fun getUserEnrollmentsFromCache(userId: Long): List<Enrollment> =
        database.courseDAO.getUserEnrollments(userId)

    suspend fun getAllCourses(userToken: String?): List<Course> {
        val courses = RetrofitClient.apiInterface.getAllCourses(userToken!!)
        for (course in courses) {
            val tempCourse = Course(mapToCourseEntity(course), mapToChapterListEntity(course.chapters))
            database.courseDAO.insertCourse(tempCourse)
        }
        return getAllCoursesFromCache()
    }

    suspend fun enrollToCourse(token: String, enrollToCourseModel: EnrollToCourseModel) {
        RetrofitClient.apiInterface.enrollToCourse(token, enrollToCourseModel)
    }

    suspend fun getCourseById(courseId: Long): CourseEntity = database.courseDAO.getCourseById(courseId)

    suspend fun getAllCoursesFromCache(): List<Course> = database.courseDAO.getAllCourses()

    private fun mapToCourseEntity(networkEntity: CourseNetworkEntity): CourseEntity {
        return CourseEntity(
            id = networkEntity.id,
            title = networkEntity.title,
            description = networkEntity.description,
            courseImage = networkEntity.courseImage,
            userId = networkEntity.userId,
            author = networkEntity.author,
            chapters = mapToChapterListEntity(networkEntity.chapters)
        )
    }

    private fun mapToCourseEntity(networkEntity: CourseNetworkEntity, userId: Long): CourseEntity {
        return CourseEntity(
            id = networkEntity.id,
            title = networkEntity.title,
            description = networkEntity.description,
            courseImage = networkEntity.courseImage,
            userId = userId,
            author = networkEntity.author,
            chapters = mapToChapterListEntity(networkEntity.chapters)
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
        return if (!networkEntityList.isNullOrEmpty()) {
            networkEntityList.map {
                mapToChapterEntity(it)
            }
        } else {
            emptyList()
        }
    }
}