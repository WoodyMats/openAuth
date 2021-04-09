package com.woodymats.openauth.databases

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.woodymats.openauth.models.Course

@Dao
interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("SELECT * FROM courses_table")
    suspend fun getAllCourses(): List<Course>
}