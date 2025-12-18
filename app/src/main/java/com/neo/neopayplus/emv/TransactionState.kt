package com.neo.neopayplus.emv

/**
 * Transaction state for UI updates.
 * 
 * Used by EMVHandler callbacks and UI screens to track transaction progress.
 */
sealed class TransactionState {
    object Idle : TransactionState()
    data class Processing(val message: String) : TransactionState()
    data class PinRequired(val isOnline: Boolean) : TransactionState()
    data class Completed(val approved: Boolean, val rc: String, val msg: String, val rrn: String?) : TransactionState()
    data class Error(val message: String) : TransactionState()
}

