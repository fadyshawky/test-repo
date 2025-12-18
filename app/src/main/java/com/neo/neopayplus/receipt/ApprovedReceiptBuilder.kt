package com.neo.neopayplus.receipt

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

/**
 * Builder for approved transaction receipts
 */
class ApprovedReceiptBuilder(private val data: ReceiptData) {
    
    private val amountFormatter = DecimalFormat("#,##0.00")
    
    fun build(isMerchantCopy: Boolean): List<ReceiptLine> {
        val lines = mutableListOf<ReceiptLine>()
        
        // Header with logos
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
        
        // Bank logo
        val bankLogo = data.bankLogoAssetPath ?: "images/banque_misr_logo.png"
        lines.add(ReceiptLine.Logo(bankLogo, Alignment.CENTER))
        lines.add(ReceiptLine.Empty)
        // Bank Terminal ID and Bank Merchant ID
        lines.add(createLabelValueLine("Terminal ID", data.bankTerminalId ?: "00000001"))
        lines.add(createLabelValueLine("Merchant ID", data.bankMerchantId ?: "00000001"))

        lines.add(ReceiptLine.Empty)
        
        // Mastercard Requirement #2: Transaction type (middle title bold)
        val transactionTypeText = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        val brandTypeText = "${data.cardBrand ?: ""} $transactionTypeText"
        val panLine = data.maskedPan
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
        

        // RRN
        lines.add(createLabelValueLine("RRN:${data.rrn ?: "123456789012"}","AUTH:${data.authCode ?: "AUTH123"}" ))
        

        // Mastercard Requirement #8: For Chip Transaction - AID
        lines.add(createLabelValueLine("APP ID", data.aid ?: ""))
        
        // Mastercard Requirement #5: Total Transaction amount and currency (align left label => value => align right)
        val formattedAmount = amountFormatter.format(data.amount) + " ${data.currency}"
        lines.add(createLabelValueLine("Amount", formattedAmount))
        lines.add(ReceiptLine.Empty)
        // Status (middle title bold)
        lines.add(ReceiptLine.Text("APPROVED", Alignment.CENTER, FontSize.LARGE, bold = true))
        lines.add(ReceiptLine.Empty)
        
        // Mastercard Requirement #9: CVM (body middle)
        val cvmText = buildCvmText()
        lines.add(ReceiptLine.Text(cvmText, Alignment.CENTER, FontSize.NORMAL))
        
        // Mastercard Requirement #9: Signature space (only for signature-based transactions)
        // Omit signature space if PIN or CDCVM was used
        if (data.cvmMethod == ReceiptCvmMethod.SIGNATURE) {
            lines.add(ReceiptLine.Empty)
            if (data.signatureBitmap != null) {
                // Print signature bitmap (would need to convert to ReceiptLine)
                lines.add(ReceiptLine.Text("[Signature Image]", Alignment.CENTER, FontSize.NORMAL))
            }
            lines.add(ReceiptLine.SignatureLine)
        }
        
        lines.add(ReceiptLine.Empty)
        
        // Authorization text (body middle)
        val authText = "I authorize to debit the above amount from my account, " +
                "i confirm receipt of merchandise inside the shop and in a good condition, " +
                "all sales final. I acknowledge and accept the time of transaction"
        lines.add(ReceiptLine.Text(authText, Alignment.CENTER, FontSize.SMALL))
        lines.add(ReceiptLine.Empty)
        
        // Copy type (body middle) - "CUSTOMER COPY / MERCHANT COPY"
        // First print is for merchant, second is for customer
        val copyType = if (isMerchantCopy) "MERCHANT COPY" else "CUSTOMER COPY"
        lines.add(ReceiptLine.Text(copyType, Alignment.CENTER, FontSize.NORMAL, bold = true))
        lines.add(ReceiptLine.Empty)
        
        return lines
    }
    
    private fun buildBrandTypeText(): String {
        val brand = data.cardBrand ?: ""
        val type = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        val pan = data.maskedPan ?: ""
        return "$brand $type\n$pan"
    }
    
    private fun buildBatchReceiptLine(): String {
        val batch = data.batchNumber ?: "N/A"
        val receipt = data.receiptNumber ?: "N/A"
        return "$batch / $receipt"
    }
    
    private fun buildRrnAuthLine(): String {
        val rrn = data.rrn ?: "N/A"
        val auth = data.authCode ?: "N/A"
        return "RRN $rrn / AUTH $auth"
    }
    
    private fun buildCvmText(): String {
        return when (data.cvmMethod) {
            ReceiptCvmMethod.NO_PIN -> "No PIN Entered"
            ReceiptCvmMethod.OFFLINE_PIN -> "Offline PIN"
            ReceiptCvmMethod.ONLINE_PIN -> "Online PIN"
            ReceiptCvmMethod.SIGNATURE -> "Signature"
        }
    }
    
    private fun createLabelValueLine(label: String, value: String): ReceiptLine {
        // Format: Label Value (no colon, matching user's changes)
        // Receipt width is typically 32-48 characters, use 32 for safety
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
