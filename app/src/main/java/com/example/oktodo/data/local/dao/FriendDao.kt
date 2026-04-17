package com.example.oktodo.data.local.dao

import androidx.room.*
import com.example.oktodo.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends ORDER BY name ASC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT COUNT(*) FROM friends")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(friend: FriendEntity)

    @Delete
    suspend fun delete(friend: FriendEntity)
}
