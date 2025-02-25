package com.sample.biometric.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sample.biometric.data.dao.UserDataDao
import com.sample.biometric.data.entity.UserDataEntity
import timber.log.Timber
import java.util.concurrent.Executors

@Database(
    entities = [
        UserDataEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    companion object {

        private const val DATABASE_NAME = "app_database.db"

        @Volatile
        private var instance: AppDatabase? = null

        private val lock = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(lock) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context = context.applicationContext,
                klass = AppDatabase::class.java,
                name = DATABASE_NAME
            ).setQueryCallback(
                queryCallback = { sqlQuery, bindArgs ->
                    Timber.tag("ROOM").d("$sqlQuery $bindArgs")
                },
                executor = Executors.newSingleThreadExecutor()
            )
                .fallbackToDestructiveMigration()
                .build()
    }

    abstract fun userDao(): UserDataDao

}