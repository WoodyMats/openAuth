package com.woodymats.openauth.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses_table")
data class Course(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,

    @ColumnInfo(name = "courseTitle")
    var title: String = "",

    @ColumnInfo(name = "courseDescription")
    var description: String = "",

    @ColumnInfo(name = "courseImage")
    var courseImage: String = "",

    @ColumnInfo(name = "author")
    var author: String = "",

    @ColumnInfo(name = "chapters")
    var chapters: List<Chapter>

)
