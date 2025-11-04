package com.neo.neopayplus.vm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionViewModel : ViewModel() {
    private val _amountCents = MutableStateFlow(0L)
    val amountCents: StateFlow<Long> = _amountCents
    
    fun setAmount(cents: Long) { _amountCents.value = cents }
}

