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
        val isBankDecline = intent.getBooleanExtra("isBankDecline", false)
        val tvr = intent.getStringExtra("tvr")
        val tsi = intent.getStringExtra("tsi")
        val maskedExpiryDate = intent.getStringExtra("maskedExpiryDate")
        val cardholderName = intent.getStringExtra("cardholderName")
        val isReprint = intent.getBooleanExtra("isReprint", false)
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")
        
        // Build ReceiptData using ReceiptDataMapper (centralized mapping logic)
        // Use date/time from intent if available (for reprints from history), otherwise use current time
        val receiptDate = date ?: java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date())
        val receiptTime = time ?: java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date())
        
        val receiptData = ReceiptDataMapper.fromIntentExtras(
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
            bankMerchantId = bankMerchantId,
            isBankDecline = isBankDecline,
            tvr = tvr,
            tsi = tsi,
            maskedExpiryDate = maskedExpiryDate,
            cardholderName = cardholderName,
            date = receiptDate,
            time = receiptTime
        )
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    // Auto-print merchant copy for approved transactions when screen loads (skip for reprints)
                    LaunchedEffect(approved, isReprint) {
                        if (approved && !isReprint) {
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
                        isReprint = isReprint,
                        onPrintCustomerCopy = {
                            if (isReprint) {
                                android.util.Log.d("ReceiptActivity", "Printing reprint...")
                                receiptPrinterService.printReprint(
                                    receiptData,
                                    object : ReceiptPrinterService.PrintCallback {
                                        override fun onSuccess() {
                                            android.util.Log.d("ReceiptActivity", "✓ Reprint printed successfully")
                                        }
                                        override fun onError(message: String) {
                                            android.util.Log.e("ReceiptActivity", "✗ Reprint print error: $message")
                                        }
                                    }
                                )
                            } else {
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
    
}

