package com.example.oktodo.data.local.entity

import androidx.room.Entity

@Entity(tableName = "shop_items", primaryKeys = ["id", "userId"])
data class ShopItemEntity(
    val id: String,
    val userId: String,
    val title: String,
    val emoji: String,
    val price: Int,
    val category: String,
    val isPurchased: Boolean,
    val isEquipped: Boolean = false
)
