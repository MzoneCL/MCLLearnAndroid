package com.example.mvvm_pattern_2021.ui_state

import com.example.mvvm_pattern_2021.beans.Fruit

data class MainActivityUIState (
    val isLoading: Boolean = false,
    val fruits: List<Fruit> = emptyList(),
    val error: String? = null
)