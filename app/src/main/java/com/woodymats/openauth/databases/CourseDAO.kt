package com.woodymats.openauth.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woodymats.openauth.models.Chapter
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.CourseEntity
import com.woodymats.openauth.models.Enrollment

@Dao
interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseEntities(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseEntity(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    suspend fun insertEnrollment(enrollment: Enrollment) {
        insertCourseEntity(enrollment.course)
        insertChapters(enrollment.chapters)
    }

    suspend fun insertCourse(course: Course) {
        insertCourseEntity(course.course)
        insertChapters(course.chapters)
    }

    // @Delete
    // suspend fun deleteCourse(course: Course)

    @Transaction
    @Query("SELECT * FROM courses_table WHERE user_id == :id")
    suspend fun getUserEnrollments(id: Long): List<Enrollment>

    @Transaction
    @Query("SELECT * FROM courses_table")
    suspend fun getAllCourses(): List<Course>

    @Query("SELECT * FROM courses_table WHERE id == :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity
}