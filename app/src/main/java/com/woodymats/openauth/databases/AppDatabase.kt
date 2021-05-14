package com.woodymats.openauth.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.woodymats.openauth.models.local.ChapterEntity
import com.woodymats.openauth.models.local.ContentEntity
import com.woodymats.openauth.models.local.CourseEntity
import com.woodymats.openauth.models.local.UserEntity

@Database(entities = [UserEntity::class, CourseEntity::class, ChapterEntity::class, ContentEntity::class], version = 2, exportSchema = false)
@TypeConverters(DatabaseConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val userDAO: UserDAO
    abstract val courseDAO: CourseDAO
}

@Volatile
private lateinit var INSTANCE: AppDatabase

fun getInstance(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        // If instance is `null` make a new database instance.
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            )
                // Wipes and rebuilds instead of migrating if no Migration object.
                // Migration is not part of this lesson. You can learn more about
                // migration with Room in this blog post:
                // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                .fallbackToDestructiveMigration()
                .build()
        }
        // Return instance; smart cast to be non-null.
        return INSTANCE
    }
}
