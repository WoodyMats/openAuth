package com.woodymats.openauth.databases

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.woodymats.openauth.models.ChapterWithContents
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.Course
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.models.Enrollment
import com.woodymats.openauth.models.local.ContentEntity

@Dao
interface CourseDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseEntities(courses: List<CourseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourseEntity(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<ChapterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<ContentEntity>)

    suspend fun insertEnrollment(enrollment: Enrollment) {
        insertCourseEntity(enrollment.course)
        insertChapters(enrollment.chapters)
        for (chapter in enrollment.chapters) {
            insertContents(chapter.contents)
        }
    }

    suspend fun insertChaptersWithContents(chapters: List<ChapterEntity>) {
        insertChapters(chapters)
        for (chapter in chapters) {
            insertContents(chapter.contents)
        }

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

    @Query("SELECT * FROM courses_table")
    suspend fun getAllCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses_table WHERE id == :courseId")
    suspend fun getCourseById(courseId: Long): CourseEntity

    @Query("SELECT * FROM chapters_table WHERE chapterId == :chapterId")
    suspend fun getChapterById(chapterId: Long): ChapterEntity

    @Query("SELECT * FROM contents_table WHERE chapter_id == :chapterId ORDER BY `order`")
    suspend fun getChapterContents(chapterId: Long): List<ContentEntity>

    @Query("UPDATE contents_table SET completed = 1 WHERE id == :contentId")
    suspend fun setContentAsCompleted(contentId: Long): Int

    @Query("SELECT * FROM chapters_table WHERE course_id == :courseId")
    suspend fun getCourseChapters(courseId: Long): List<ChapterEntity>

    @Query("DELETE FROM courses_table")
    suspend fun deleteAllCourses()

    @Query("DELETE FROM chapters_table")
    suspend fun deleteAllChapters()

    @Query("DELETE FROM contents_table")
    suspend fun deleteAllContents()

    @Transaction
    @Query("SELECT * FROM chapters_table WHERE course_id == :courseId")
    suspend fun getCourseChaptersWithContents(courseId: Long): List<ChapterWithContents>

    @Query("SELECT COUNT(id) FROM courses_table")
    suspend fun getCourseRows(): Int

    @Query("UPDATE courses_table SET user_id = :userId WHERE id == :courseId")
    suspend fun updateCourseToEnrollment(userId: Long, courseId: Long): Int

    // @Query("SELECT * FROM courses_table WHERE courseTitle LIKE :query")
    // suspend fun search(query : String) : LiveData<List<CourseEntity>>
}