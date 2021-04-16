package com.woodymats.openauth.models

import com.google.gson.annotations.SerializedName

data class CourseNetworkEntity(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val courseImage: String = "",
    @SerializedName("user_id")
    val userId: Long,
    val author: String = "",
    val chapters: List<ChapterNetworkEntity>
)
