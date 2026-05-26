package com.example.oktodo.data.local.dao

import androidx.room.*
import com.example.oktodo.data.local.entity.ShopItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopItemDao {
    @Query("SELECT * FROM shop_items WHERE userId = :userId ORDER BY price ASC")
    fun getItemsForUser(userId: String): Flow<List<ShopItemEntity>>

    @Query("SELECT COUNT(*) FROM shop_items WHERE userId = :userId")
    suspend fun countForUser(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopItemEntity)

    @Query("UPDATE shop_items SET isPurchased = 1 WHERE id = :id AND userId = :userId")
    suspend fun markPurchased(id: String, userId: String)

    @Query("UPDATE shop_items SET isEquipped = 0 WHERE id = :id AND userId = :userId")
    suspend fun unequip(id: String, userId: String)

    @Transaction
    suspend fun equipAtomically(id: String, userId: String) {
        unequipAllForUser(userId)
        equip(id, userId)
    }

    @Query("UPDATE shop_items SET isEquipped = 0 WHERE userId = :userId")
    suspend fun unequipAllForUser(userId: String)

    @Query("UPDATE shop_items SET isEquipped = 1 WHERE id = :id AND userId = :userId")
    suspend fun equip(id: String, userId: String)
}
