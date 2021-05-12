package com.woodymats.openauth.databases

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.local.ContentEntity

class DatabaseConverters {

    @TypeConverter
    fun stringToChapterList(chapterJson: String): List<ChapterEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<ChapterEntity>>() {}.type
        return gson.fromJson(chapterJson, type)
    }

    @TypeConverter
    fun chapterListToString(chapterEntityList: List<ChapterEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ChapterEntity>>() {}.type
        return gson.toJson(chapterEntityList, type)
    }

    @TypeConverter
    fun stringToContentsList(contentsJson: String): List<ContentEntity> {
        val gson = Gson()
        val type = object : TypeToken<List<ContentEntity>>() {}.type
        return gson.fromJson(contentsJson, type)
    }

    @TypeConverter
    fun contentsListToString(contentsEntityList: List<ContentEntity>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ContentEntity>>() {}.type
        return gson.toJson(contentsEntityList, type)
    }
}