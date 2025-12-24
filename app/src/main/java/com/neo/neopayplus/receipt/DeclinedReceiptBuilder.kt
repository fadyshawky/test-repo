package com.neo.neopayplus.receipt

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

/**
 * Builder for declined transaction receipts (SALE, REFUND, VOID)
 * Matches the structure of ApprovedReceiptBuilder with both logos for bank declines, DECLINED status
 */
class DeclinedReceiptBuilder(private val data: ReceiptData) {
    
    private val amountFormatter = DecimalFormat("#,##0.00")
    
    fun build(isMerchantCopy: Boolean, isReprint: Boolean = false): List<ReceiptLine> {
        val lines = mutableListOf<ReceiptLine>()
        
        // Header with logos - merchant logo (left) and bank logo (right) on same line
        val merchantLogo = data.merchantLogoAssetPath ?: "images/receipt_logo.webp"
        val bankLogo = data.bankLogoAssetPath ?: "images/banque_misr_logo.png"
        lines.add(ReceiptLine.DualLogo(merchantLogo, bankLogo))
        lines.add(ReceiptLine.Empty)
        
        // Merchant name
        if (data.merchantName.isNotEmpty()) {
            lines.add(ReceiptLine.Text(data.merchantName, Alignment.CENTER, FontSize.NORMAL, bold = true))
            lines.add(ReceiptLine.Empty)
        }
        
        lines.add(createLabelValueLine(data.internalTerminalId, data.bankTerminalId ?: "00000001"))

        // Order ID
        lines.add(createLabelValueLine(data.internalMerchantId, data.bankMerchantId ?: "00000001"))
        // Bank Terminal ID and Bank Merchant ID
        lines.add(createLabelValueLine(data.transactionId ?: "TXN987654321", data.orderId ?: "ORD123456789"))

        lines.add(ReceiptLine.Empty)
        
        // Card brand with card type (e.g., "MASTERCARD DEBIT", "VISA CREDIT")
        val brandWithType = if (data.cardBrand != null && data.cardType != null) {
            "${data.cardBrand} ${data.cardType}"
        } else {
            data.cardBrand ?: ""
        }
        lines.add(ReceiptLine.Text(brandWithType.uppercase(), Alignment.CENTER, FontSize.LARGE, bold = true))

        // PAN on separate line
        val panLine = data.maskedPan ?: "" // Use empty string if null
        lines.add(ReceiptLine.Text(panLine, Alignment.CENTER, FontSize.LARGE, bold = true))

        // Transaction type (contactless/IC) - middle title bold
        val entryModeText = when (data.entryMode) {
            ReceiptEntryMode.CONTACTLESS -> "CONTACTLESS"
            ReceiptEntryMode.IC -> "IC CARD"
            ReceiptEntryMode.MAGNETIC -> "MAGNETIC STRIPE"
        }
        lines.add(ReceiptLine.Text(entryModeText, Alignment.CENTER, FontSize.NORMAL, bold = true))

        // Batch number and Receipt
        lines.add(createLabelValueLine("BATCH:${data.batchNumber ?: "001"}", "RECEIPT:${data.receiptNumber ?: "000001"}"))
        

        // Mastercard Requirement #6: Transaction date
        lines.add(createLabelValueLine("DATE:${data.date}", "TIME:${data.time}"))
        

        // RRN - truncate to 12 digits if longer
        val rrnFormatted = data.rrn?.take(12) ?: "123456789012"
        val authFormatted = data.authCode?.take(6) ?: "AUTH123"
        lines.add(createLabelValueLine("RRN:$rrnFormatted","AUTH:$authFormatted"))
        

        // Mastercard Requirement #8: For Chip Transaction - AID
        lines.add(createLabelValueLine("AID: ${data.aid ?: ""}", "EXP: ${data.maskedExpiryDate}"))
        
        // TVR (Terminal Verification Results) - tag 95
        if (data.tvr != null && data.tvr.isNotEmpty()) {
            lines.add(createLabelValueLine("TVR", data.tvr))
        }
        
        // TSI (Transaction Status Information) - tag 9B
        if (data.tsi != null && data.tsi.isNotEmpty()) {
            lines.add(createLabelValueLine("TSI", data.tsi))
        }
        
        // Mastercard Requirement #5: Total Transaction amount and currency (align left label => value => align right)
        val formattedAmount = amountFormatter.format(data.amount) + " ${data.currency}"
        lines.add(createLabelValueLine("Amount", formattedAmount))

        // Status and Transaction Type combined (e.g., "SALE DECLINED", "REFUND DECLINED")
        val transactionTypeText = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        lines.add(ReceiptLine.Text("$transactionTypeText DECLINED", Alignment.CENTER, FontSize.LARGE, bold = true))

        // Mastercard Requirement: Response or failure reason for unsuccessful transaction
        if (data.responseCode != null) {
            lines.add(ReceiptLine.Text("Response Code: ${data.responseCode}", Alignment.CENTER, FontSize.NORMAL))
        }
        if (data.responseMessage != null) {
            lines.add(ReceiptLine.Text(data.responseMessage, Alignment.CENTER, FontSize.SMALL))
        }

        // Copy type (body middle) - "CUSTOMER COPY / MERCHANT COPY" or "REPRINT"
        val copyType = if (isReprint) {
            "REPRINT"
        } else {
            if (isMerchantCopy) "MERCHANT COPY" else "CUSTOMER COPY"
        }
        lines.add(ReceiptLine.Text(copyType, Alignment.CENTER, FontSize.NORMAL, bold = true))
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
