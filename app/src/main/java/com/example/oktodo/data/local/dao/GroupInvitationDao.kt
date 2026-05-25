package com.example.oktodo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.oktodo.data.local.entity.GroupInvitationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupInvitationDao {
    @Query("SELECT * FROM group_invitations WHERE toUserId = :userId AND status = 'pending' ORDER BY createdAt DESC")
    fun getPendingForUser(userId: String): Flow<List<GroupInvitationEntity>>

    @Query("SELECT * FROM group_invitations WHERE toUserId = :toUserId AND groupId = :groupId LIMIT 1")
    suspend fun findExisting(toUserId: String, groupId: String): GroupInvitationEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(invitation: GroupInvitationEntity)

    @Query("UPDATE group_invitations SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)
}
