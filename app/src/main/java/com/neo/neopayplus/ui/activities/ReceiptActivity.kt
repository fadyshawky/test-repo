package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.neo.neopayplus.receipt.ReceiptData
import com.neo.neopayplus.receipt.ReceiptDataMapper
import com.neo.neopayplus.receipt.ReceiptPrinterService
import com.neo.neopayplus.ui.screens.ReceiptScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Receipt Activity - Displays and allows printing/sharing of transaction receipt.
 */
@AndroidEntryPoint
class ReceiptActivity : ComponentActivity() {
    
    @Inject
    lateinit var receiptPrinterService: ReceiptPrinterService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Get transaction data from intent
        val approved = intent.getBooleanExtra("approved", false)
        val rrn = intent.getStringExtra("rrn")
        val amount = intent.getDoubleExtra("amount", 0.0)
        val currency = intent.getStringExtra("currency") ?: "EGP"
        val cardPan = intent.getStringExtra("cardPan")
        val aid = intent.getStringExtra("aid")
        val applicationPreferredName = intent.getStringExtra("applicationPreferredName")
        val entryMode = intent.getStringExtra("entryMode") // "CONTACTLESS" or "IC"
        val transactionType = intent.getStringExtra("transactionType") // "SALE", "REFUND", "VOID"
        val authCode = intent.getStringExtra("authCode")
        
        // Log received values for debugging
        android.util.Log.d("ReceiptActivity", "Received data - amount: $amount, rrn: $rrn, authCode: $authCode, aid: $aid")
        val responseCode = intent.getStringExtra("responseCode")
        val responseMessage = intent.getStringExtra("responseMessage")
        val stan = intent.getIntExtra("stan", 0)
        val cvmMethod = intent.getStringExtra("cvmMethod") ?: "NO_PIN" // "NO_PIN", "OFFLINE_PIN", "ONLINE_PIN", "SIGNATURE"
        val orderId = intent.getStringExtra("orderId")
        val transactionId = intent.getStringExtra("transactionId")
        val batchNumber = intent.getStringExtra("batchNumber")
        val receiptNumber = intent.getStringExtra("receiptNumber")
        val bankTerminalId = intent.getStringExtra("bankTerminalId")
        val bankMerchantId = intent.getStringExtra("bankMerchantId")
        
        // Build ReceiptData
        val receiptData = buildReceiptData(
            approved = approved,
            rrn = rrn,
            amount = amount,
            currency = currency,
            cardPan = cardPan,
            aid = aid,
            applicationPreferredName = applicationPreferredName,
            entryMode = entryMode,
            transactionType = transactionType,
            authCode = authCode,
            responseCode = responseCode,
            responseMessage = responseMessage,
            stan = stan,
            cvmMethod = cvmMethod,
            orderId = orderId,
            transactionId = transactionId,
            batchNumber = batchNumber,
            receiptNumber = receiptNumber,
            bankTerminalId = bankTerminalId,
            bankMerchantId = bankMerchantId
        )
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    // Auto-print merchant copy for approved transactions when screen loads
                    LaunchedEffect(approved) {
                        if (approved) {
                            android.util.Log.d("ReceiptActivity", "Auto-printing merchant copy...")
                            receiptPrinterService.printMerchantCopy(
                                receiptData,
                                object : ReceiptPrinterService.PrintCallback {
                                    override fun onSuccess() {
                                        android.util.Log.d("ReceiptActivity", "✓ Merchant copy printed successfully")
                                    }
                                    override fun onError(message: String) {
                                        android.util.Log.e("ReceiptActivity", "✗ Merchant copy print error: $message")
                                    }
                                }
                            )
                        }
                    }
                    
                    ReceiptScreen(
                        receiptData = receiptData,
                        approved = approved,
                        onPrintCustomerCopy = {
                            android.util.Log.d("ReceiptActivity", "Printing customer copy...")
                            if (approved) {
                                receiptPrinterService.printCustomerCopy(
                                    receiptData,
                                    object : ReceiptPrinterService.PrintCallback {
                                        override fun onSuccess() {
                                            android.util.Log.d("ReceiptActivity", "✓ Customer copy printed successfully")
                                        }
                                        override fun onError(message: String) {
                                            android.util.Log.e("ReceiptActivity", "✗ Customer copy print error: $message")
                                        }
                                    }
                                )
                            } else {
                                receiptPrinterService.printDeclinedReceipt(
                                    receiptData,
                                    object : ReceiptPrinterService.PrintCallback {
                                        override fun onSuccess() {
                                            android.util.Log.d("ReceiptActivity", "✓ Receipt printed successfully")
                                        }
                                        override fun onError(message: String) {
                                            android.util.Log.e("ReceiptActivity", "✗ Print error: $message")
                                        }
                                    }
                                )
                            }
                        },
                        onCancelCustomerCopy = {
                            android.util.Log.d("ReceiptActivity", "Customer copy printing cancelled")
                        },
                        onShare = { /* export pdf */ },
                        onDone = {
                            finish()
                        }
                    )
                }
            }
        }
    }
    
    private fun buildReceiptData(
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
        bankMerchantId: String?
    ): ReceiptData {
        // Convert CVM method string to enum
        val cvm = when (cvmMethod.uppercase()) {
            "OFFLINE_PIN" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.OFFLINE_PIN
            "ONLINE_PIN" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.ONLINE_PIN
            "SIGNATURE" -> com.neo.neopayplus.receipt.ReceiptCvmMethod.SIGNATURE
            else -> com.neo.neopayplus.receipt.ReceiptCvmMethod.NO_PIN
        }
        
        // Convert entry mode
        val entry = when (entryMode?.uppercase()) {
            "CONTACTLESS", "NFC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.CONTACTLESS
            "IC", "ICC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.IC
            "MAGNETIC" -> com.neo.neopayplus.receipt.ReceiptEntryMode.MAGNETIC
            else -> com.neo.neopayplus.receipt.ReceiptEntryMode.IC
        }
        
        // Convert transaction type
        val type = when (transactionType?.uppercase()) {
            "REFUND" -> com.neo.neopayplus.receipt.ReceiptTransactionType.REFUND
            "VOID", "REVERSAL" -> com.neo.neopayplus.receipt.ReceiptTransactionType.VOID
            else -> com.neo.neopayplus.receipt.ReceiptTransactionType.SALE
        }
        
        // Get date and time
        val dateTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
            .format(java.util.Date())
        val dateParts = dateTime.split(" ")
        val date = dateParts[0]
        val time = dateParts[1]
        
        // Mask PAN - show first 6 digits (BIN), then mask, then last 4
        val maskedPan = cardPan?.let { 
            when {
                it.length >= 16 -> {
                    // Standard format: first 6 digits, then masked, then last 4
                    val first6 = it.take(6)
                    val last4 = it.takeLast(4)
                    "$first6****$last4"
                }
                it.length > 4 -> {
                    // If less than 16 digits, show first 4, mask, then last 4
                    val first4 = it.take(4)
                    val last4 = it.takeLast(4)
                    "$first4****$last4"
                }
                else -> "****"
            }
        }
        
        // Detect card brand from AID
        val cardBrand = aid?.let {
            val brandProfile = com.neo.neopayplus.emv.config.EmvBrandConfig.getBrandForAid(it)
            brandProfile?.name?.uppercase() ?: "UNKNOWN"
        }
        
        return ReceiptData(
            orderId = orderId,
            transactionId = transactionId,
            rrn = rrn,
            stan = if (stan > 0) stan else null,
            internalTerminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId(),
            internalMerchantId = com.neo.neopayplus.config.PaymentConfig.getMerchantId(),
            bankTerminalId = bankTerminalId,
            bankMerchantId = bankMerchantId,
            merchantName = com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME,
            merchantCity = null, // TODO: Get from terminal config
            merchantStateProvince = null, // TODO: Get from terminal config
            merchantCountry = "EG", // TODO: Get from terminal config
            cardBrand = cardBrand,
            maskedPan = maskedPan,
            transactionType = type,
            entryMode = entry,
            amount = java.math.BigDecimal.valueOf(amount),
            currency = currency,
            aid = aid,
            applicationPreferredName = applicationPreferredName,
            authCode = authCode,
            lineItems = emptyList(), // TODO: Support line items if available
            approved = approved,
            responseCode = responseCode,
            responseMessage = responseMessage,
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

