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
    val items: Flow<List<ShopItem>> = dao.getAllItems().map { list -> list.map { it.toDomain() } }

    val equippedItem: Flow<ShopItem?> = items.map { list -> list.find { it.isEquipped } }

    suspend fun purchase(item: ShopItem, userPoints: Int): Boolean {
        if (userPoints < item.price) return false
        dao.markPurchased(item.id)
        prefs.purchaseItem(item.price)
        return true
    }

    suspend fun equip(id: String) {
        dao.unequipAll()
        dao.equip(id)
    }

    suspend fun unequip(id: String) {
        dao.unequip(id)
    }

    suspend fun seedIfEmpty() {
        if (dao.count() > 0) return
        listOf(
            ShopItemEntity("s1", "Sombrero mágico", "🎩", 40, "Accesorio", false, false),
            ShopItemEntity("s2", "Lentes cool", "🕶️", 25, "Accesorio", false, false),
            ShopItemEntity("s3", "Bufanda morada", "🧣", 30, "Ropa", false, false),
            ShopItemEntity("s4", "Corona mini", "👑", 60, "Premium", false, false),
            ShopItemEntity("s5", "Moño elegante", "🎀", 20, "Accesorio", false, false),
            ShopItemEntity("s6", "Traje espacial", "🧑‍🚀", 90, "Skin", false, false)
        ).forEach { dao.insert(it) }
    }
}
