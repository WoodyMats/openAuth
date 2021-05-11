package com.woodymats.openauth.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "chapters_table")
data class Chapter(
    @PrimaryKey
    var chapterId: Long = 0L,

    @ColumnInfo(name = "chapterTitle")
    var title: String = "",

    @ColumnInfo(name = "chapterDescription")
    var description: String = "",

    @ColumnInfo(name = "chapterImage")
    var chapterImage: String = "",

    @ColumnInfo(name = "order")
    var order: Int = 0,

    @ColumnInfo(name = "course_id")
    @SerializedName("course_id")
    var courseId: Int
)
