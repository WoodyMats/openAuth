package com.woodymats.openauth.models

data class CourseNetworkEntity(
    val id: Long = 0L,
    val title: String = "",
    val description: String = "",
    val courseImage: String = "",
    val author: String = "",
    val chapters: List<ChapterNetworkEntity>
)
