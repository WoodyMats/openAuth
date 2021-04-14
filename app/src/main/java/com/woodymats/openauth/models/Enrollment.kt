package com.woodymats.openauth.models

import androidx.room.Embedded
import androidx.room.Relation

data class Enrollment(
    @Embedded var course: Course,
    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )
    val chapters: List<Chapter>
)
