package com.neo.neopayplus.receipt

import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.emv.config.EmvBrandConfig
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
        authCode: String? = null,
        isBankDecline: Boolean = false,
        tvr: String? = null,
        tsi: String? = null,
        maskedExpiryDate: String? = null
    ): ReceiptData {
        // Detect card brand from AID
        val cardBrand = if (aid != null) {
            val brandProfile = EmvBrandConfig.getBrandForAid(aid)
            brandProfile?.name?.uppercase() ?: "UNKNOWN"
        } else {
            null
        }
        
        // Detect card type (DEBIT or CREDIT) from PAN
        val cardType = CardTypeDetector.detectCardType(transaction.cardPan, cardBrand)
        
        // Mask PAN - always show first 6 digits (BIN), then mask, then last 4
        val maskedPan = transaction.cardPan?.let { pan ->
            when {
                pan.length >= 10 -> {
                    // Always use first 6 digits and last 4 digits
                    val first6 = pan.take(6)
                    val last4 = pan.takeLast(4)
                    "$first6****$last4"
                }
                else -> "****"
            }
        }
        
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
            cardType = cardType,
            maskedPan = maskedPan,
            maskedExpiryDate = maskedExpiryDate,
            transactionType = receiptTransactionType,
            entryMode = receiptEntryMode,
            amount = transaction.amount,
            currency = transaction.currency,
            aid = aid,
            applicationPreferredName = null, // TODO: Read from EMV tag 50
            authCode = authCode ?: transaction.authCode,
            tvr = tvr,
            tsi = tsi,
            lineItems = emptyList(), // TODO: Support line items if available
            approved = transaction.isApproved,
            responseCode = transaction.responseCode,
            responseMessage = transaction.responseMessage,
            isBankDecline = isBankDecline,
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
     * Build ReceiptData from intent extras (for EMVPaymentActivity flow)
     * This is used when we don't have a Transaction domain model yet
     */
    fun fromIntentExtras(
        approved: Boolean,
        rrn: String?,
        amount: Double,
        currency: String,
        cardPan: String?,
        aid: String?,
        applicationPreferredName: String?,
        entryMode: String?,
        transactionType: String?,
        authCode: String?,
        responseCode: String?,
        responseMessage: String?,
        stan: Int,
        cvmMethod: String,
        orderId: String?,
        transactionId: String?,
        batchNumber: String?,
        receiptNumber: String?,
        bankTerminalId: String?,
        bankMerchantId: String?,
        isBankDecline: Boolean = false,
        tvr: String? = null,
        tsi: String? = null,
        maskedExpiryDate: String? = null
    ): ReceiptData {
        // Convert CVM method string to enum
        val cvm = when (cvmMethod.uppercase()) {
            "OFFLINE_PIN" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.OFFLINE_PIN
            "ONLINE_PIN" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.ONLINE_PIN
            "SIGNATURE" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.SIGNATURE
            else -> com.neo.neopayplus.receipt.ReceiptCvmMethod.NO_PIN
        }
        
        // Convert entry mode
        val receiptEntryMode = when (entryMode?.uppercase()) {
            "CONTACTLESS", "NFC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.CONTACTLESS
            "IC", "ICC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.IC
            "MAGNETIC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.MAGNETIC
            else -> com.neo.neopayplus.receipt.ReceiptEntryMode.IC
        }
        
        // Convert transaction type
        val receiptTransactionType = when (transactionType?.uppercase()) {
            "REFUND" -> com.neo.neopayplus.receipt.ReceiptTransactionType.REFUND
            "VOID", "REVERSAL" -> com.neo.neopayplus.receipt.ReceiptTransactionType.VOID
            else -> com.neo.neopayplus.receipt.ReceiptTransactionType.SALE
        }
        
        // Get date and time
        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val dateParts = dateTime.split(" ")
        val date = dateParts[0]
        val time = dateParts[1]
        
        // Mask PAN - always show first 6 digits (BIN), then mask, then last 4
        val maskedPan = cardPan?.let { pan ->
            when {
                pan.length >= 10 -> {
                    // Always use first 6 digits and last 4 digits
                    val first6 = pan.take(6)
                    val last4 = pan.takeLast(4)
                    "$first6****$last4"
                }
                else -> "****"
            }
        } ?: "" // Return empty string if cardPan is null
        
        // Detect card brand from AID
        val cardBrand = aid?.let {
            val brandProfile = EmvBrandConfig.getBrandForAid(it)
            brandProfile?.name?.uppercase() ?: "UNKNOWN"
        }
        
        // Detect card type (DEBIT or CREDIT) from PAN
        val cardType = CardTypeDetector.detectCardType(cardPan, cardBrand)
        
        return ReceiptData(
            orderId = orderId,
            transactionId = transactionId,
            rrn = rrn,
            stan = if (stan > 0) stan else null,
            internalTerminalId = PaymentConfig.getTerminalId(),
            internalMerchantId = PaymentConfig.getMerchantId(),
            bankTerminalId = bankTerminalId,
            bankMerchantId = bankMerchantId,
            merchantName = PaymentConfig.MERCHANT_NAME,
            merchantCity = null, // TODO: Get from terminal config
            merchantStateProvince = null, // TODO: Get from terminal config
            merchantCountry = "EG", // TODO: Get from terminal config
            cardBrand = cardBrand,
            cardType = cardType,
            maskedPan = maskedPan,
            maskedExpiryDate = maskedExpiryDate,
            transactionType = receiptTransactionType,
            entryMode = receiptEntryMode,
            amount = BigDecimal.valueOf(amount),
            currency = currency,
            aid = aid,
            applicationPreferredName = applicationPreferredName,
            authCode = authCode,
            tvr = tvr,
            tsi = tsi,
            lineItems = emptyList(), // TODO: Support line items if available
            approved = approved,
            responseCode = responseCode,
            responseMessage = responseMessage,
            isBankDecline = isBankDecline,
            cvmMethod = cvm,
            signatureBitmap = null, // TODO: Get signature bitmap if available
            batchNumber = batchNumber,
            receiptNumber = receiptNumber,
            date = date,
            time = time,
            merchantLogoAssetPath = "images/receipt_logo.webp",
            bankLogoAssetPath = "images/banque_misr_logo.png" // Bank logo from assets
        )
    }
}
