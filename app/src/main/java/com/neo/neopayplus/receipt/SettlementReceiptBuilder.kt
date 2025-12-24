package com.neo.neopayplus.receipt

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Builder for settlement report receipts
 */
object SettlementReceiptBuilder {
    
    private const val RECEIPT_WIDTH = 32
    
    fun build(data: SettlementReceiptData): List<ReceiptLine> {
        val lines = mutableListOf<ReceiptLine>()
        
        // Logo (centered)
        if (data.merchantLogoAssetPath != null) {
            lines.add(ReceiptLine.Logo(data.merchantLogoAssetPath))
            lines.add(ReceiptLine.Empty)
        }
        
        // Merchant name (centered)
        lines.add(ReceiptLine.Text(data.merchantName, Alignment.CENTER, FontSize.NORMAL, bold = true))
        
        // Terminal ID and Merchant ID
        lines.add(createLabelValueLine(data.internalTerminalId, data.internalMerchantId))
        
        // Date and Time
        lines.add(createLabelValueLine(data.date, data.time))
        
        // EGP Batch and Batch Number
        lines.add(createLabelValueLine("EGP Batch", data.batchNumber))
        
        // SETTLEMENT REPORT (centered)
        lines.add(ReceiptLine.Text("SETTLEMENT REPORT", Alignment.CENTER, FontSize.NORMAL, bold = true))
        
        // GB BATCH (batchNumber) status (accepted/approved)
        val batchStatusLine = "GB BATCH (${data.batchNumber}) ${data.batchStatus}"
        lines.add(ReceiptLine.Text(batchStatusLine, Alignment.CENTER, FontSize.NORMAL))
        
        // VISA section (only if there are transactions)
        if (hasTransactions(data.visaTotals)) {
            addBrandSection(lines, data.visaTotals)
        }
        
        // MASTERCARD section (only if there are transactions)
        if (hasTransactions(data.mastercardTotals)) {
            addBrandSection(lines, data.mastercardTotals)
        }
        
        // MEEZA section (only if there are transactions)
        if (hasTransactions(data.meezaTotals)) {
            addBrandSection(lines, data.meezaTotals)
        }
        
        // GENERAL TOTAL section
        lines.add(ReceiptLine.Text("GENERAL TOTAL", Alignment.CENTER, FontSize.NORMAL, bold = true))
        addBrandTotals(lines, data.generalTotals)
        
        // Footer
        lines.add(ReceiptLine.Text("BATCH ENDED SUCCESSFULLY", Alignment.CENTER, FontSize.NORMAL, bold = true))
        lines.add(ReceiptLine.Text("================================", Alignment.CENTER, FontSize.NORMAL))
        lines.add(ReceiptLine.Empty)
        
        return lines
    }
    
    private fun hasTransactions(totals: BrandTotals): Boolean {
        return totals.saleCount > 0 || totals.voidSaleCount > 0 || totals.refundCount > 0 ||
               totals.saleAmount > BigDecimal.ZERO || totals.voidSaleAmount > BigDecimal.ZERO || totals.refundAmount > BigDecimal.ZERO
    }
    
    private fun addBrandSection(lines: MutableList<ReceiptLine>, totals: BrandTotals) {
        lines.add(ReceiptLine.Text(totals.brandName, Alignment.CENTER, FontSize.NORMAL, bold = true))
        addBrandTotals(lines, totals)
    }
    
    private fun addBrandTotals(lines: MutableList<ReceiptLine>, totals: BrandTotals) {
        val formatter = DecimalFormat("#,##0.00")
        
        // Sale
            val saleLine = formatAmountLine("sale", totals.saleCount, totals.saleAmount, formatter)
            lines.add(ReceiptLine.Text(saleLine, Alignment.LEFT, FontSize.NORMAL))
        
        // V.Sale (void sale)
            val voidSaleLine = formatAmountLine("v.sale", totals.voidSaleCount, totals.voidSaleAmount, formatter)
            lines.add(ReceiptLine.Text(voidSaleLine, Alignment.LEFT, FontSize.NORMAL))
        
        // Refund - always show, even if 0
        val refundLine = formatAmountLine("refund", totals.refundCount, totals.refundAmount, formatter)
        lines.add(ReceiptLine.Text(refundLine, Alignment.LEFT, FontSize.NORMAL))
        
        // Separator
        lines.add(ReceiptLine.Text("-------------------------------", Alignment.CENTER, FontSize.NORMAL))
        
        // Total
        val totalLine = formatAmountLine("total", totals.totalCount, totals.totalAmount, formatter)
        lines.add(ReceiptLine.Text(totalLine, Alignment.LEFT, FontSize.NORMAL))
        
        // Double separator
        lines.add(ReceiptLine.Text("================================", Alignment.CENTER, FontSize.NORMAL))
    }
    
    private fun formatAmountLine(label: String, count: Int, amount: BigDecimal, formatter: DecimalFormat): String {
        val amountStr = formatter.format(amount)
        // Format: "label.                (count).                   EGP(amount)"
        // Equal spacing between transaction type, count, and amount
        val labelPart = "$label"
        val countFormatted = String.format("%03d", count) // Format as 3 digits with leading zeros
        val countPart = "($countFormatted)"
        val amountPart = "EGP$amountStr"
        
        // Calculate spacing - ensure everything fits on one line (max 32 characters)
        val labelWidth = labelPart.length
        val countWidth = countPart.length // Always 6: "(000)"
        val amountWidth = amountPart.length
        
        // Calculate total spacing needed
        val totalSpacing = RECEIPT_WIDTH - labelWidth - countWidth - amountWidth
        
        // Ensure we have at least 1 space between each part
        if (totalSpacing < 2) {
            // If not enough space, use minimal spacing (1 space each)
            return "$labelPart $countPart $amountPart"
        }
        
        // Equal spacing: divide total spacing equally between the two gaps
        // Gap 1: between label and count
        // Gap 2: between count and amount
        val spacesPerGap = totalSpacing / 2
        val extraSpace = totalSpacing % 2 // If odd, add extra space to second gap
        
        // Build line with equal spacing
        val builder = StringBuilder()
        builder.append(labelPart)
        repeat(spacesPerGap) { builder.append(' ') }
        builder.append(countPart)
        repeat(spacesPerGap + extraSpace) { builder.append(' ') } // Add extra space to second gap if odd
        builder.append(amountPart)
        
        // Ensure total length doesn't exceed RECEIPT_WIDTH
        val result = builder.toString()
        return if (result.length <= RECEIPT_WIDTH) {
            result
        } else {
            // Fallback: minimal spacing if it still doesn't fit
            "$labelPart $countPart $amountPart"
        }
    }
    
    private fun createLabelValueLine(leftValue: String, rightValue: String): ReceiptLine {
        return ReceiptLineUtils.createLeftRightLine(leftValue, rightValue, RECEIPT_WIDTH)
    }
}

