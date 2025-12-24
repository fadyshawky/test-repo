package com.neo.neopayplus.receipt

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.Locale

/**
 * Builder for approved transaction receipts
 */
class ApprovedReceiptBuilder(private val data: ReceiptData) {
    
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

        // Status and Transaction Type combined (e.g., "SALE APPROVED", "REFUND APPROVED")
        val transactionTypeText = when (data.transactionType) {
            ReceiptTransactionType.SALE -> "SALE"
            ReceiptTransactionType.REFUND -> "REFUND"
            ReceiptTransactionType.VOID -> "VOID"
        }
        lines.add(ReceiptLine.Text("$transactionTypeText APPROVED", Alignment.CENTER, FontSize.LARGE, bold = true))

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
            lines.add(ReceiptLine.Empty)
        }

        if(!isMerchantCopy) {
            // Cardholder name (above authorization text)
            if (data.cardholderName != null && data.cardholderName.isNotEmpty()) {
                lines.add(ReceiptLine.Text(data.cardholderName, Alignment.CENTER, FontSize.NORMAL, bold = true))
                lines.add(ReceiptLine.Empty)
            }
            
            // Authorization text (body middle)
            val authText = "I authorize to debit the above amount from my account, " +
                    "i confirm receipt of merchandise inside the shop and in a good condition, " +
                    "all sales final. I acknowledge and accept the time of transaction"
            lines.add(ReceiptLine.Text(authText, Alignment.CENTER, FontSize.SMALL))
            lines.add(ReceiptLine.Empty)
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
    
    private fun buildCvmText(): String {
        return when (data.cvmMethod) {
            ReceiptCvmMethod.NO_PIN -> "No PIN Entered"
            ReceiptCvmMethod.OFFLINE_PIN -> "Offline PIN"
            ReceiptCvmMethod.ONLINE_PIN -> "Online PIN"
            ReceiptCvmMethod.SIGNATURE -> "Signature"
        }
    }
    
    private fun createLabelValueLine(label: String, value: String): ReceiptLine {
        return ReceiptLineUtils.createLabelValueLine(label, value)
    }
}
