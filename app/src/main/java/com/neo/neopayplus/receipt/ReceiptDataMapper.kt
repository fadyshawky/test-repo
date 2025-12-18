package com.neo.neopayplus.receipt

import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.emv.config.EmvBrandConfig
import com.neo.neopayplus.utils.SystemDateTime
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 * Maps transaction data to ReceiptData for printing
 */
object ReceiptDataMapper {
    
    /**
     * Convert Transaction domain model to ReceiptData
     */
    fun toReceiptData(
        transaction: Transaction,
        aid: String? = null,
        cvmMethod: com.neo.neopayplus.receipt.ReceiptCvmMethod = com.neo.neopayplus.receipt.ReceiptCvmMethod.NO_PIN,
        signatureBitmap: android.graphics.Bitmap? = null,
        orderId: String? = null,
        transactionId: String? = null,
        batchNumber: String? = null,
        receiptNumber: String? = null,
        bankTerminalId: String? = null,
        bankMerchantId: String? = null,
        authCode: String? = null
    ): ReceiptData {
        // Detect card brand from AID
        val cardBrand = if (aid != null) {
            val brandProfile = EmvBrandConfig.getBrandForAid(aid)
            brandProfile?.name?.uppercase() ?: "UNKNOWN"
        } else {
            null
        }
        
        // Mask PAN
        val maskedPan = transaction.cardPan?.let { maskPan(it) }
        
        // Convert transaction type
        val receiptTransactionType = when (transaction.type) {
            TransactionType.PURCHASE -> com.neo.neopayplus.receipt.ReceiptTransactionType.SALE
            TransactionType.REFUND -> com.neo.neopayplus.receipt.ReceiptTransactionType.REFUND
            TransactionType.REVERSAL -> com.neo.neopayplus.receipt.ReceiptTransactionType.VOID
            TransactionType.VOID -> com.neo.neopayplus.receipt.ReceiptTransactionType.VOID
        }
        
        // Convert entry mode
        val receiptEntryMode = when (transaction.entryMode) {
            EntryMode.ICC -> com.neo.neopayplus.receipt.ReceiptEntryMode.IC
            EntryMode.NFC -> com.neo.neopayplus.receipt.ReceiptEntryMode.CONTACTLESS
            EntryMode.MAGNETIC -> com.neo.neopayplus.receipt.ReceiptEntryMode.MAGNETIC
        }
        
        // Format date and time
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(transaction.timestamp)
        val dateParts = dateTime.split(" ")
        val date = dateParts[0]
        val time = dateParts[1]
        
        return ReceiptData(
            orderId = orderId,
            transactionId = transactionId ?: transaction.id,
            rrn = transaction.rrn,
            stan = transaction.stan,
            internalTerminalId = PaymentConfig.getTerminalId(),
            internalMerchantId = PaymentConfig.getMerchantId(),
            bankTerminalId = bankTerminalId,
            bankMerchantId = bankMerchantId,
            merchantName = PaymentConfig.MERCHANT_NAME,
            merchantCity = null, // TODO: Get from terminal config
            merchantStateProvince = null, // TODO: Get from terminal config
            merchantCountry = "EG", // TODO: Get from terminal config
            cardBrand = cardBrand,
            maskedPan = maskedPan,
            transactionType = receiptTransactionType,
            entryMode = receiptEntryMode,
            amount = transaction.amount,
            currency = transaction.currency,
            aid = aid,
            applicationPreferredName = null, // TODO: Read from EMV tag 50
            authCode = authCode ?: transaction.authCode,
            lineItems = emptyList(), // TODO: Support line items if available
            approved = transaction.isApproved,
            responseCode = transaction.responseCode,
            responseMessage = transaction.responseMessage,
            cvmMethod = cvmMethod,
            signatureBitmap = signatureBitmap,
            batchNumber = batchNumber,
            receiptNumber = receiptNumber,
            date = date,
            time = time,
            merchantLogoAssetPath = "images/receipt_logo.webp",
            bankLogoAssetPath = "images/banque_misr_logo.png" // Bank logo from assets
        )
    }
    
    /**
     * Mask PAN (Primary Account Number) for display
     * Format: ****1234 (last 4 digits visible)
     */
    private fun maskPan(pan: String): String {
        if (pan.length <= 4) {
            return "****"
        }
        val last4 = pan.takeLast(4)
        return "****$last4"
    }
}
