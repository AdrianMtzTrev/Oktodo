package com.example.oktodo.data.local.dao

import androidx.room.*
import com.example.oktodo.data.local.entity.SharedEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SharedEventDao {
    @Query("SELECT * FROM shared_events ORDER BY date ASC")
    fun getAllEvents(): Flow<List<SharedEventEntity>>

    @Query("SELECT COUNT(*) FROM shared_events")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: SharedEventEntity)

    @Query("DELETE FROM shared_events WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("UPDATE shared_events SET title=:title, date=:date, time=:time, location=:location WHERE id=:id")
    suspend fun update(id: String, title: String, date: String, time: String, location: String)

    @Query("UPDATE shared_events SET canEditJoined=:canEditJoined WHERE id=:id")
    suspend fun setEditors(id: String, canEditJoined: String)
}
