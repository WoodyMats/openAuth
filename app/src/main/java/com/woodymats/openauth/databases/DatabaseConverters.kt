package com.woodymats.openauth.databases

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woodymats.openauth.models.Chapter

class DatabaseConverters {

    @TypeConverter
    fun stringToChapterList(chapterJson: String): List<Chapter> {
        val gson = Gson()
        val type = object : TypeToken<List<Chapter>>() {}.type
        return gson.fromJson(chapterJson, type)
    }

    @TypeConverter
    fun chapterListToString(chapterList: List<Chapter>): String {
        val gson = Gson()
        val type = object : TypeToken<List<Chapter>>() {}.type
        return gson.toJson(chapterList, type)
    }

}