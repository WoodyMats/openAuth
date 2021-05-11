package com.woodymats.openauth.models

data class EnrollToCourseModel(
    val status: Int = 0,
    val progress: Int = 0,
    val courseId: Long
)
