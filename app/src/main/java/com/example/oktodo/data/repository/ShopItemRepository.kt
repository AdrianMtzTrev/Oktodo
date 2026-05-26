package com.example.oktodo.data.repository

import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.local.dao.ShopItemDao
import com.example.oktodo.data.local.entity.ShopItemEntity
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.ui.model.ShopItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopItemRepository @Inject constructor(
    private val dao: ShopItemDao,
    private val prefs: UserPreferencesDataStore
) {
    fun getItemsForUser(userId: String): Flow<List<ShopItem>> =
        dao.getItemsForUser(userId).map { list -> list.map { it.toDomain() } }

    fun getEquippedItemForUser(userId: String): Flow<ShopItem?> =
        getItemsForUser(userId).map { list -> list.find { it.isEquipped } }

    suspend fun purchase(item: ShopItem, userId: String, userPoints: Int): Boolean {
        if (userPoints < item.price) return false
        val deducted = prefs.purchaseItem(item.price)
        if (!deducted) return false
        dao.markPurchased(item.id, userId)
        return true
    }

    suspend fun equip(id: String, userId: String) {
        dao.equipAtomically(id, userId)
    }

    suspend fun unequip(id: String, userId: String) {
        dao.unequip(id, userId)
    }

    suspend fun seedIfEmpty(userId: String) {
        if (dao.countForUser(userId) > 0) return
        listOf(
            ShopItemEntity("s1", userId, "Sombrero mágico", "🎩", 40, "Accesorio", false, false),
            ShopItemEntity("s2", userId, "Lentes cool", "🕶️", 25, "Accesorio", false, false),
            ShopItemEntity("s3", userId, "Bufanda morada", "🧣", 30, "Ropa", false, false),
            ShopItemEntity("s4", userId, "Corona mini", "👑", 60, "Premium", false, false),
            ShopItemEntity("s5", userId, "Moño elegante", "🎀", 20, "Accesorio", false, false),
            ShopItemEntity("s6", userId, "Traje espacial", "🧑‍🚀", 90, "Skin", false, false)
        ).forEach { dao.insert(it) }
    }
}
