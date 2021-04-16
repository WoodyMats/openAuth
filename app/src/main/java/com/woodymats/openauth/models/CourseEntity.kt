package com.woodymats.openauth.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "courses_table")
data class CourseEntity(
    @PrimaryKey
    var id: Long = 0L,

    @ColumnInfo(name = "courseTitle")
    var title: String = "",

    @ColumnInfo(name = "courseDescription")
    var description: String = "",

    @ColumnInfo(name = "courseImage")
    var courseImage: String = "",

    @ColumnInfo(name = "user_id")
    @SerializedName("user_id")
    var userId: Long,

    @ColumnInfo(name = "author")
    var author: String = "",

    // @ColumnInfo(name = "chapters")
    // @Relation(parentColumn = "id", entityColumn = "course_id", entity = Chapter::class)
    // var chapters: List<Chapter>

)
