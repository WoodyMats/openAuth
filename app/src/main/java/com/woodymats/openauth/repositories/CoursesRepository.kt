package com.woodymats.openauth.repositories

import com.woodymats.openauth.databases.AppDatabase
import com.woodymats.openauth.models.ChapterWithContents
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.remote.ChapterNetworkEntity
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.models.remote.CourseNetworkEntity
import com.woodymats.openauth.models.EnrollToCourseModel
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.models.remote.ContentNetworkEntity
import com.woodymats.openauth.models.remote.EnrollmentNetworkEntity
import com.woodymats.openauth.network.RetrofitClient

class CoursesRepository(private val database: AppDatabase) {

    suspend fun downloadUserEnrollmentsAndGetThemFromCache(userToken: String?, userId: Long): List<Enrollment> {
        val enrollments = downloadUserEnrollmentsFromServer(userToken)
        insertUserEnrollmentsIntoDatabase(enrollments, userId)
        return getUserEnrollmentsFromCache(userId)
    }

    suspend fun downloadUserEnrollmentsFromServer(userToken: String?): List<EnrollmentNetworkEntity> {
        return RetrofitClient.apiInterface.getUserEnrollments(userToken!!)
    }

    suspend fun isCoursesTableEmpty(): Boolean {
        val rows = database.courseDAO.getCourseRows()
        return (rows < 1)
    }

    suspend fun insertUserEnrollmentsIntoDatabase(enrollments: List<EnrollmentNetworkEntity>, userId: Long) {
        for (enrollment in enrollments) {
            val enrollmentDatabase = Enrollment(
                mapToCourseEntity(enrollment.course, userId),
                mapToChapterListEntity(enrollment.course.chapters)
            )
            database.courseDAO.insertEnrollment(enrollmentDatabase)
        }
    }

    suspend fun getUserEnrollmentsFromCache(userId: Long): List<Enrollment> =
        database.courseDAO.getUserEnrollments(userId)

    suspend fun downloadAllCoursesAndGetThemFromCache(userToken: String?): List<CourseEntity> {
        val courses = getAllCoursesFromServer(userToken)
        insertDownloadedCoursesIntoDatabase(courses)
        return getAllCoursesFromCache()
    }

    suspend fun getAllCoursesFromServer(userToken: String?): List<CourseNetworkEntity> {
        return RetrofitClient.apiInterface.getAllCourses(userToken ?: "")
    }

    suspend fun insertDownloadedCoursesIntoDatabase(courses: List<CourseNetworkEntity>) {
        for (course in courses) {
            val tempCourse = Course(mapToCourseEntity(course), mapToChapterListEntity(course.chapters))
            database.courseDAO.insertCourse(tempCourse)
        }
    }

    suspend fun clearCourseChaptersAnContentsTables() {
        database.courseDAO.deleteAllContents()
        database.courseDAO.deleteAllChapters()
        database.courseDAO.deleteAllCourses()
    }

    suspend fun enrollToCourse(token: String, enrollToCourseModel: EnrollToCourseModel): CourseEntity {
        val chaptersFromNetworkResponse = RetrofitClient.apiInterface.enrollToCourse(token, enrollToCourseModel)
        database.courseDAO.insertChaptersWithContents(mapToChapterListEntity(chaptersFromNetworkResponse))
        return database.courseDAO.getCourseById(enrollToCourseModel.courseId)
    }

    suspend fun getCourseById(courseId: Long): CourseEntity = database.courseDAO.getCourseById(courseId)

    suspend fun getChapterById(chapterId: Long): ChapterEntity = database.courseDAO.getChapterById(chapterId)

    suspend fun getAllCoursesFromCache(): List<CourseEntity> = database.courseDAO.getAllCourses()

    suspend fun getChapterContentsList(chapterId: Long): List<ContentEntity> = database.courseDAO.getChapterContents(chapterId)

    suspend fun setContentAsCompleted(id: Long): Int = database.courseDAO.setContentAsCompleted(id)

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

    private fun mapToChapterEntity(networkEntity: ChapterNetworkEntity): ChapterEntity {
        return ChapterEntity(
            chapterId = networkEntity.chapterId,
            title = networkEntity.title,
            description = networkEntity.description,
            chapterImage = networkEntity.chapterImage,
            order = networkEntity.order,
            courseId = networkEntity.courseId,
            contents = mapToContentsListEntity(networkEntity.contents ?: emptyList())
        )
    }

    private fun mapToContentsListEntity(networkEntityList: List<ContentNetworkEntity>): List<ContentEntity> {
        return if (!networkEntityList.isNullOrEmpty()) {
            networkEntityList.map {
                mapToContentEntity(it)
            }
        } else {
            emptyList()
        }
    }

    private fun mapToContentEntity(contentNetworkEntity: ContentNetworkEntity): ContentEntity {
        return ContentEntity(
            id = contentNetworkEntity.id,
            title = contentNetworkEntity.title,
            content = contentNetworkEntity.content,
            order = contentNetworkEntity.order,
            chapterId = contentNetworkEntity.chapterId
        )
    }

    private fun mapToChapterListEntity(networkEntityList: List<ChapterNetworkEntity>): List<ChapterEntity> {
        return if (!networkEntityList.isNullOrEmpty()) {
            networkEntityList.map {
                mapToChapterEntity(it)
            }
        } else {
            emptyList()
        }
    }

    suspend fun getChaptersWithContentsList(courseId: Long): List<ChapterWithContents> {
        return database.courseDAO.getCourseChaptersWithContents(courseId)
    }
}