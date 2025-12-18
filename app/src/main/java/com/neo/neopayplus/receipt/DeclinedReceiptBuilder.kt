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
        
        // Header with merchant logo only (no bank logo for declined transactions)
        val merchantLogo = data.merchantLogoAssetPath ?: "images/receipt_logo.webp"
        lines.add(ReceiptLine.Logo(merchantLogo, Alignment.CENTER))
        lines.add(ReceiptLine.Empty)
        
        // Order ID
        lines.add(createLabelValueLine("Order ID", data.orderId ?: "ORD123456789"))

        // Transaction ID
        lines.add(createLabelValueLine("Transaction ID", data.transactionId ?: "TXN987654321"))
        
        // Internal Terminal ID and Merchant ID
        lines.add(createLabelValueLine("Terminal ID", data.internalTerminalId))
        lines.add(createLabelValueLine("Merchant ID", data.internalMerchantId))

        lines.add(ReceiptLine.Empty)
        
        // Mastercard Requirement #2: Transaction type (middle title bold)
        val transactionTypeText = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        val brandTypeText = "${data.cardBrand ?: ""} $transactionTypeText"
        val panLine = data.maskedPan ?: ""
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
        
        // RRN (if available)
        if (data.rrn != null) {
            lines.add(createLabelValueLine("RRN", data.rrn ?: ""))
        }
        
        // Mastercard Requirement #8: For Chip Transaction - AID
        if (data.aid != null && data.aid.isNotEmpty()) {
            lines.add(createLabelValueLine("APP ID", data.aid))
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
