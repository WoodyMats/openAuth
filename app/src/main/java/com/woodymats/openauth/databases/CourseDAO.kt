package com.woodymats.openauth.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woodymats.openauth.models.Chapter
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.Enrollment

@Dao
interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<Chapter>)

    suspend fun insertEnrollment(enrollment: Enrollment) {
        insertCourse(enrollment.course)
        insertChapters(enrollment.chapters)
    }

    // @Delete
    // suspend fun deleteCourse(course: Course)

    @Transaction
    @Query("SELECT * FROM courses_table")
    suspend fun getUserEnrollments(): List<Enrollment>
}