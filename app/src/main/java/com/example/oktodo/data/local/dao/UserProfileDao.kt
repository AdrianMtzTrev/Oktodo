package com.example.oktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oktodo.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles ORDER BY displayName ASC")
    fun getAllProfiles(): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    suspend fun getById(id: String): UserProfileEntity?

    @Query("SELECT * FROM user_profiles WHERE displayName LIKE '%' || :query || '%' OR username LIKE '%' || :query || '%' ORDER BY displayName ASC")
    fun search(query: String): Flow<List<UserProfileEntity>>

    @Query("SELECT * FROM user_profiles WHERE username = :username LIMIT 1")
    suspend fun findByUsername(username: String): UserProfileEntity?

    @Query("SELECT COUNT(*) FROM user_profiles WHERE username = :username")
    suspend fun isUsernameTaken(username: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(profile: UserProfileEntity)

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun count(): Int
}
