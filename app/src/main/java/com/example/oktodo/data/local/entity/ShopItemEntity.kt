package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_items")
data class ShopItemEntity(
    @PrimaryKey val id: String,
    val title: String,
    val emoji: String,
    val price: Int,
    val category: String,
    val isPurchased: Boolean,
    val isEquipped: Boolean = false
)
