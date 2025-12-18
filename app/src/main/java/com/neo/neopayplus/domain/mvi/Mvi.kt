package com.neo.neopayplus.domain.mvi

interface UiIntent

interface UiEffect

sealed class UiState {
    data object Idle : UiState()
}

