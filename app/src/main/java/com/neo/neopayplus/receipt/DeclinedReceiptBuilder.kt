package com.neo.neopayplus.receipt

import java.text.DecimalFormat

/**
 * Builder for declined transaction receipts (SALE, REFUND, VOID)
 * Matches the structure of ApprovedReceiptBuilder but without bank logo and with DECLINED status
 */
class DeclinedReceiptBuilder(private val data: ReceiptData) {
    
    private val amountFormatter = DecimalFormat("#,##0.00")
    
    fun build(): List<ReceiptLine> {
        val lines = mutableListOf<ReceiptLine>()
        
        // Header: Show bank logo if this is a bank decline, otherwise merchant logo only
        if (data.isBankDecline && data.bankLogoAssetPath != null) {
            // Bank decline: Show bank logo
            lines.add(ReceiptLine.Logo(data.bankLogoAssetPath, Alignment.CENTER))
        } else {
            // Internal decline: Show merchant logo only
            val merchantLogo = data.merchantLogoAssetPath ?: "images/receipt_logo.webp"
            lines.add(ReceiptLine.Logo(merchantLogo, Alignment.CENTER))
        }
        lines.add(ReceiptLine.Empty)
        
        // Order ID
        lines.add(createLabelValueLine("Order ID", data.orderId ?: "ORD123456789"))

        // Transaction ID
        lines.add(createLabelValueLine("Transaction ID", data.transactionId ?: "TXN987654321"))
        
        // Terminal ID and Merchant ID: Show bank TID/MID for bank declines, internal for internal declines
        if (data.isBankDecline && data.bankTerminalId != null && data.bankMerchantId != null) {
            // Bank decline: Show bank Terminal ID and Merchant ID
            lines.add(createLabelValueLine("Terminal ID", data.bankTerminalId))
            lines.add(createLabelValueLine("Merchant ID", data.bankMerchantId))
        } else {
            // Internal decline: Show internal Terminal ID and Merchant ID
            lines.add(createLabelValueLine("Terminal ID", data.internalTerminalId))
            lines.add(createLabelValueLine("Merchant ID", data.internalMerchantId))
        }

        lines.add(ReceiptLine.Empty)
        
        // Mastercard Requirement #2: Transaction type (middle title bold)
        val transactionTypeText = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        // Format brand with card type (e.g., "VISA DEBIT", "MASTERCARD CREDIT")
        val brandWithType = if (data.cardBrand != null && data.cardType != null) {
            "${data.cardBrand} ${data.cardType}"
        } else {
            data.cardBrand ?: ""
        }
        val brandTypeText = "$brandWithType $transactionTypeText"
        val panLine = data.maskedPan ?: "" // Use empty string if null, matching approved receipt format
        lines.add(ReceiptLine.Text("$brandTypeText\n$panLine", Alignment.CENTER, FontSize.LARGE, bold = true))
        lines.add(ReceiptLine.Empty)
        
        // Transaction type (contactless/IC) - middle title bold
        val entryModeText = when (data.entryMode) {
            ReceiptEntryMode.CONTACTLESS -> "CONTACTLESS"
            ReceiptEntryMode.IC -> "IC CARD"
            ReceiptEntryMode.MAGNETIC -> "MAGNETIC STRIPE"
        }
        lines.add(ReceiptLine.Text(entryModeText, Alignment.CENTER, FontSize.NORMAL, bold = true))
        lines.add(ReceiptLine.Empty)
        
        // Batch number and Receipt
        lines.add(createLabelValueLine("BATCH:${data.batchNumber ?: "001"}", "RECEIPT:${data.receiptNumber ?: "000001"}"))
        
        // Mastercard Requirement #6: Transaction date
        lines.add(createLabelValueLine("DATE:${data.date}", "TIME:${data.time}"))
        
        // RRN (always show, with placeholder if not available)
        lines.add(createLabelValueLine("RRN", data.rrn ?: ""))
        
        // Mastercard Requirement #8: For Chip Transaction - AID (always show, matching approved receipt)
        lines.add(createLabelValueLine("AID", data.aid ?: ""))
        
        // Expiry date (masked for display)
        if (data.maskedExpiryDate != null && data.maskedExpiryDate.isNotEmpty()) {
            lines.add(createLabelValueLine("EXP", data.maskedExpiryDate))
        }
        
        // TVR (Terminal Verification Results) - tag 95
        if (data.tvr != null && data.tvr.isNotEmpty()) {
            lines.add(createLabelValueLine("TVR", data.tvr))
        }
        
        // TSI (Transaction Status Information) - tag 9B
        if (data.tsi != null && data.tsi.isNotEmpty()) {
            lines.add(createLabelValueLine("TSI", data.tsi))
        }
        
        // Mastercard Requirement #5: Total Transaction amount and currency
        val formattedAmount = amountFormatter.format(data.amount) + " ${data.currency}"
        lines.add(createLabelValueLine("Amount", formattedAmount))
        lines.add(ReceiptLine.Empty)
        
        // Status (middle title bold) - DECLINED
        lines.add(ReceiptLine.Text("DECLINED", Alignment.CENTER, FontSize.LARGE, bold = true))
        lines.add(ReceiptLine.Empty)
        
        // Mastercard Requirement: Response or failure reason for unsuccessful transaction
        if (data.responseCode != null) {
            lines.add(ReceiptLine.Text("Response Code: ${data.responseCode}", Alignment.CENTER, FontSize.NORMAL))
        }
        if (data.responseMessage != null) {
            lines.add(ReceiptLine.Text(data.responseMessage, Alignment.CENTER, FontSize.SMALL))
        }
        
        lines.add(ReceiptLine.Empty)
        lines.add(ReceiptLine.Text("CUSTOMER COPY", Alignment.CENTER, FontSize.NORMAL, bold = true))
        lines.add(ReceiptLine.Empty)
        
        return lines
    }
    
    private fun createLabelValueLine(label: String, value: String): ReceiptLine {
        // Format: Label Value (no colon, matching approved receipt format)
        val receiptWidth = 32 // Typical thermal receipt width in characters
        val labelLength = label.length
        val valueLength = value.length
        val availableSpace = receiptWidth - labelLength - valueLength
        
        if (availableSpace > 0) {
            val spacing = " ".repeat(availableSpace)
            val line = "$label$spacing$value"
            return ReceiptLine.Text(line, Alignment.LEFT, FontSize.NORMAL)
        } else {
            // If too long, just concatenate
            return ReceiptLine.Text("$label $value", Alignment.LEFT, FontSize.NORMAL)
        }
    }
    
    private fun createLabelValueLine(leftValue: String, rightValue: String, receiptWidth: Int = 32): ReceiptLine {
        // Format: LeftValue (left aligned) RightValue (right aligned)
        val leftLength = leftValue.length
        val rightLength = rightValue.length
        val availableSpace = receiptWidth - leftLength - rightLength
        
        if (availableSpace > 0) {
            val spacing = " ".repeat(availableSpace)
            val line = "$leftValue$spacing$rightValue"
            return ReceiptLine.Text(line, Alignment.LEFT, FontSize.NORMAL)
        } else {
            // If too long, just concatenate with single space
            return ReceiptLine.Text("$leftValue $rightValue", Alignment.LEFT, FontSize.NORMAL)
        }
    }
}
