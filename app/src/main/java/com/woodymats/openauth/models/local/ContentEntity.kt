package com.woodymats.openauth.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "contents_table")
data class ContentEntity(
    @PrimaryKey
    var id: Long = 0L,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "completed")
    var completed: Boolean = false,

    @ColumnInfo(name = "content")
    var content: String = "",

    @ColumnInfo(name = "order")
    var order: Int = 0,

    @ColumnInfo(name = "chapter_id")
    @SerializedName(value = "chapter_id")
    var chapterId: Long

)
