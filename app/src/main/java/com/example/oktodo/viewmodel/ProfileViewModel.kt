package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.oktodo.ui.model.ProfileUiState
import com.example.oktodo.ui.model.ShopItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _shopItems = MutableStateFlow(
        listOf(
            ShopItem("1", "Sombrero mágico", "🎩", 40, "Accesorio"),
            ShopItem("2", "Lentes cool", "🕶️", 25, "Accesorio"),
            ShopItem("3", "Bufanda morada", "🧣", 30, "Ropa"),
            ShopItem("4", "Corona mini", "👑", 60, "Premium"),
            ShopItem("5", "Moño elegante", "🎀", 20, "Accesorio"),
            ShopItem("6", "Traje espacial", "🧑‍🚀", 90, "Skin")
        )
    )
    val shopItems: StateFlow<List<ShopItem>> = _shopItems.asStateFlow()

    fun updateProfile(username: String, avatarEmoji: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            avatarEmoji = avatarEmoji
        )
    }

    fun addPoints(points: Int) {
        _uiState.value = _uiState.value.copy(
            points = _uiState.value.points + points
        )
    }
}