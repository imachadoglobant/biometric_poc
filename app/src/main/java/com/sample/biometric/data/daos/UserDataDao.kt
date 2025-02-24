package com.sample.biometric.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.sample.biometric.data.entities.UserDataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {

    @Insert(onConflict = REPLACE)
    suspend fun save(entity: UserDataEntity)

    @Query("SELECT * FROM UserDataEntity LIMIT 1")
    fun getFirst(): Flow<UserDataEntity>

    @Query("DELETE FROM UserDataEntity")
    suspend fun deleteAll()

}