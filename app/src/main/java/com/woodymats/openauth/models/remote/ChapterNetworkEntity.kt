package com.woodymats.openauth.models.remote

import com.google.gson.annotations.SerializedName

data class ChapterNetworkEntity(
    @SerializedName("id")
    val chapterId: Long = 0L,
    val title: String = "",
    val description: String = "",
    val chapterImage: String = "",
    val order: Int,
    @SerializedName("course_id")
    val courseId: Int,
    val contents: List<ContentNetworkEntity>?
)
