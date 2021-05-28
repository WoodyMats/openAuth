package com.woodymats.openauth.models.remote

data class EnrollmentNetworkEntity(
    val course: CourseNetworkEntity,
    val id: Long,
    val status: Int?,
    val progress: Int?,
)
