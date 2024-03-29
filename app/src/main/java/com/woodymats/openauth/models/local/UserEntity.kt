package com.woodymats.openauth.models.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey(autoGenerate = false)
    var id: Long = 0L,

    @ColumnInfo(name = "firstName")
    var firstName: String = "",

    @ColumnInfo(name = "lastName")
    var lastName: String = "",

    @ColumnInfo(name = "profileImage")
    var profileImage: String = "",

    @ColumnInfo(name = "email")
    var email: String = "",

    @ColumnInfo(name = "token")
    var token: String = "",

    @ColumnInfo(name = "canCreateCourses")
    var canCreateCourses: Int = 0,

    @ColumnInfo(name = "dateOfBirth")
    var dateOfBirth: Long = 0L
)
