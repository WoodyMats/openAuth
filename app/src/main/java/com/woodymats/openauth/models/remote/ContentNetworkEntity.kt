package com.woodymats.openauth.models.remote

import com.google.gson.annotations.SerializedName

data class ContentNetworkEntity(
    var id: Long = 0L,
    var title: String = "",
    var completed: Boolean = false,
    var content: String = "",
    var order: Int = 0,
    @SerializedName(value = "chapter_id")
    var chapterId: Long
)
