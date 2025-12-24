package com.neo.neopayplus.receipt

import java.math.BigDecimal

/**
 * Data structure for settlement report receipt
 */
data class SettlementReceiptData(
    // Header info
    val merchantLogoAssetPath: String? = null,
    val merchantName: String,
    val internalTerminalId: String,
    val internalMerchantId: String,
    val date: String, // Format: YYYY-MM-DD
    val time: String, // Format: HH:mm:ss
    val batchNumber: String,
    val batchStatus: String, // "ACCEPTED" or "APPROVED"
    
    // Brand-specific totals
    val visaTotals: BrandTotals,
    val mastercardTotals: BrandTotals,
    val meezaTotals: BrandTotals,
    
    // General totals (all brands combined)
    val generalTotals: BrandTotals
)

/**
 * Totals for a specific card brand
 * All amounts are calculated by backend - POS only displays them
 */
data class BrandTotals(
    val brandName: String, // "VISA", "MASTERCARD", "MEEZA", or "GENERAL TOTAL"
    val saleCount: Int,
    val saleAmount: BigDecimal,
    val voidSaleCount: Int,
    val voidSaleAmount: BigDecimal,
    val refundCount: Int,
    val refundAmount: BigDecimal,
    val totalAmount: BigDecimal // Total for this brand (sales - refunds) - calculated by backend
) {
    val totalCount: Int
        get() = saleCount + voidSaleCount + refundCount
    // Note: totalAmount is provided by backend, not calculated on POS
    // Backend calculates: sales - refunds (voids are for reporting only, not subtracted)
}

