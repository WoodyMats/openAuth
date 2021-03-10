package com.woodymats.openauth.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "lastName")
    var lastName: String = "",

    @ColumnInfo(name = "profileImage")
    var profileImage: String = "",

    @ColumnInfo(name = "phone")
    var phone: String = ""
)
