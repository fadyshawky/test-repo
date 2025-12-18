package com.neo.neopayplus.receipt

import java.math.BigDecimal
import java.util.Date

/**
 * Complete transaction data for receipt printing
 */
data class ReceiptData(
    // Transaction identification
    val orderId: String? = null,
    val transactionId: String? = null,
    val rrn: String? = null,
    val stan: Int? = null,
    
    // Terminal and merchant info
    val internalTerminalId: String,
    val internalMerchantId: String,
    val bankTerminalId: String? = null,
    val bankMerchantId: String? = null,
    
    // Merchant location (DE 43 - Card Acceptor Name/Location)
    val merchantName: String,
    val merchantCity: String? = null,
    val merchantStateProvince: String? = null,
    val merchantCountry: String? = null,
    
    // Card and transaction info
    val cardBrand: String? = null, // VISA, MASTERCARD, etc.
    val maskedPan: String? = null,
    val transactionType: ReceiptTransactionType, // SALE, REFUND, VOID
    val entryMode: ReceiptEntryMode, // CONTACTLESS, IC
    val amount: BigDecimal,
    val currency: String = "EGP",
    
    // EMV data
    val aid: String? = null,
    val applicationPreferredName: String? = null, // Application label/preferred name for chip transactions
    val authCode: String? = null,
    
    // Line items (for detailed receipt - Mastercard requirement #4)
    val lineItems: List<ReceiptLineItem> = emptyList(),
    
    // Transaction status
    val approved: Boolean,
    val responseCode: String? = null,
    val responseMessage: String? = null,
    
    // CVM (Cardholder Verification Method)
    val cvmMethod: ReceiptCvmMethod,
    val signatureBitmap: android.graphics.Bitmap? = null,
    
    // Receipt numbers
    val batchNumber: String? = null,
    val receiptNumber: String? = null,
    
    // Date and time
    val date: String,
    val time: String,
    
    // Logos (as asset paths, e.g., "images/receipt_logo.webp")
    val merchantLogoAssetPath: String? = null,
    val bankLogoAssetPath: String? = null
)

enum class ReceiptTransactionType {
    SALE,
    REFUND,
    VOID
}

enum class ReceiptEntryMode {
    CONTACTLESS,
    IC,
    MAGNETIC
}

enum class ReceiptCvmMethod {
    NO_PIN,
    OFFLINE_PIN,
    ONLINE_PIN,
    SIGNATURE
}

/**
 * Line item for detailed receipt (Mastercard requirement #4)
 */
data class ReceiptLineItem(
    val description: String,
    val quantity: Int = 1,
    val unitPrice: BigDecimal,
    val tax: BigDecimal = BigDecimal.ZERO
) {
    val subtotal: BigDecimal
        get() = unitPrice.multiply(BigDecimal(quantity)).add(tax)
}
