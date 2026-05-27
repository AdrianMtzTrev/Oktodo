package com.example.oktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oktodo.data.local.entity.FriendRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendRequestDao {
    @Query("SELECT * FROM friend_requests WHERE toUserId = :userId AND status = 'pending' ORDER BY createdAt DESC")
    fun getPendingIncoming(userId: String): Flow<List<FriendRequestEntity>>

    @Query("SELECT * FROM friend_requests WHERE fromUserId = :userId ORDER BY createdAt DESC")
    fun getOutgoing(userId: String): Flow<List<FriendRequestEntity>>

    @Query("SELECT * FROM friend_requests WHERE (fromUserId = :fromUserId AND toUserId = :toUserId) OR (fromUserId = :toUserId AND toUserId = :fromUserId) LIMIT 1")
    suspend fun findExisting(fromUserId: String, toUserId: String): FriendRequestEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(request: FriendRequestEntity)

    @Query("UPDATE friend_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}
