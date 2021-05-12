package com.woodymats.openauth.models

import androidx.room.Embedded
import androidx.room.Relation
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.local.CourseEntity

data class Course(
    @Embedded var course: CourseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "course_id"
    )
    val chapters: List<ChapterEntity>
)
