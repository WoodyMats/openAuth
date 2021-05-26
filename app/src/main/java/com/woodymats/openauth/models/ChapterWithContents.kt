package com.woodymats.openauth.models

import androidx.room.Embedded
import androidx.room.Relation
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.local.ContentEntity

data class ChapterWithContents(
    @Embedded val chapter: ChapterEntity,
    @Relation(
        parentColumn = "chapterId",
        entityColumn = "chapter_id"
    )
    val contents: List<ContentEntity>
)
