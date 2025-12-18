package com.neo.neopayplus.domain.payment.model

import java.math.BigDecimal
import java.util.Date

/**
 * Domain model representing a payment transaction.
 * This is a pure Kotlin data class with no Android dependencies.
 */
data class Transaction(
    val id: String? = null,
    val amount: BigDecimal,
    val currency: String = "EGP",
    val entryMode: EntryMode,
    val type: TransactionType,
    val status: TransactionStatus,
    val rrn: String? = null,
    val stan: Int? = null,
    val authCode: String? = null,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    val timestamp: Date = Date(),
    val cardPan: String? = null,
    val cardExpiry: String? = null,
    val cardholderName: String? = null
) {
    val isApproved: Boolean
        get() = status == TransactionStatus.APPROVED
    
    val isDeclined: Boolean
        get() = status == TransactionStatus.DECLINED
    
    val isPending: Boolean
        get() = status == TransactionStatus.PENDING
}

enum class EntryMode {
    ICC,      // Chip card
    NFC,      // Contactless
    MAGNETIC  // Swipe
}

enum class TransactionType {
    PURCHASE,
    REFUND,
    REVERSAL,
    VOID
}

enum class TransactionStatus {
    PENDING,
    APPROVED,
    DECLINED,
    ERROR,
    CANCELLED
}

