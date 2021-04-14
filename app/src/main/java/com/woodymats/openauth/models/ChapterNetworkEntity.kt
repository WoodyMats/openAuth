package com.woodymats.openauth.models

import com.google.gson.annotations.SerializedName

data class ChapterNetworkEntity(
    @SerializedName("chapter_id")
    val chapterId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val chapterImage: String = "",
    val order: Int,
    @SerializedName("course_id")
    val courseId: Int
)
