package com.woodymats.openauth.models

data class EnrollmentNetworkEntity(
    val course: CourseNetworkEntity,
    val id: Long,
    val status: Int?,
    val progress: Int?,
)
