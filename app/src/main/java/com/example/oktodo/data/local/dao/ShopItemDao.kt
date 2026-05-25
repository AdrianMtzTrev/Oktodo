package com.example.oktodo.data.local.dao

import androidx.room.*
import com.example.oktodo.data.local.entity.ShopItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {
    @Query("SELECT * FROM shop_items ORDER BY price ASC")
    fun getAllItems(): Flow<List<ShopItemEntity>>

    @Query("SELECT COUNT(*) FROM shop_items")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopItemEntity)

    @Query("UPDATE shop_items SET isPurchased = 1 WHERE id = :id")
    suspend fun markPurchased(id: String)

    @Query("UPDATE shop_items SET isEquipped = 0 WHERE id = :id")
    suspend fun unequip(id: String)

    @Transaction
    suspend fun equipAtomically(id: String) {
        unequipAll()
        equip(id)
    }

    @Query("UPDATE shop_items SET isEquipped = 0")
    suspend fun unequipAll()

    @Query("UPDATE shop_items SET isEquipped = 1 WHERE id = :id")
    suspend fun equip(id: String)
}
