package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.neo.neopayplus.ui.screens.ResultScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

/**
 * Result Activity - Displays transaction result (approved/declined).
 * Shows status for 2 seconds, then automatically navigates to ReceiptActivity.
 */
@AndroidEntryPoint
class ResultActivity : ComponentActivity() {
    
    private fun navigateToReceipt() {
        val approved = intent.getBooleanExtra("approved", false)
        val rc = intent.getStringExtra("rc") ?: ""
        val msg = intent.getStringExtra("msg") ?: ""
        val rrn = intent.getStringExtra("rrn")
        
        // Pass transaction data to ReceiptActivity
        val receiptIntent = Intent(this, ReceiptActivity::class.java).apply {
            putExtra("approved", approved)
            putExtra("rrn", rrn)
            putExtra("rc", rc)
            putExtra("msg", msg)
            // Add other transaction data from intent if available
            if (intent.hasExtra("amount")) putExtra("amount", intent.getDoubleExtra("amount", 0.0))
            if (intent.hasExtra("currency")) putExtra("currency", intent.getStringExtra("currency"))
            if (intent.hasExtra("cardPan")) putExtra("cardPan", intent.getStringExtra("cardPan"))
            if (intent.hasExtra("aid")) putExtra("aid", intent.getStringExtra("aid"))
            if (intent.hasExtra("applicationPreferredName")) putExtra("applicationPreferredName", intent.getStringExtra("applicationPreferredName"))
            if (intent.hasExtra("entryMode")) putExtra("entryMode", intent.getStringExtra("entryMode"))
            if (intent.hasExtra("transactionType")) putExtra("transactionType", intent.getStringExtra("transactionType"))
            if (intent.hasExtra("authCode")) putExtra("authCode", intent.getStringExtra("authCode"))
            if (intent.hasExtra("responseCode")) putExtra("responseCode", intent.getStringExtra("responseCode"))
            if (intent.hasExtra("responseMessage")) putExtra("responseMessage", intent.getStringExtra("responseMessage"))
            if (intent.hasExtra("stan")) putExtra("stan", intent.getIntExtra("stan", 0))
            if (intent.hasExtra("cvmMethod")) putExtra("cvmMethod", intent.getStringExtra("cvmMethod"))
            if (intent.hasExtra("orderId")) putExtra("orderId", intent.getStringExtra("orderId"))
            if (intent.hasExtra("transactionId")) putExtra("transactionId", intent.getStringExtra("transactionId"))
            if (intent.hasExtra("batchNumber")) putExtra("batchNumber", intent.getStringExtra("batchNumber"))
            if (intent.hasExtra("receiptNumber")) putExtra("receiptNumber", intent.getStringExtra("receiptNumber"))
            if (intent.hasExtra("bankTerminalId")) putExtra("bankTerminalId", intent.getStringExtra("bankTerminalId"))
            if (intent.hasExtra("bankMerchantId")) putExtra("bankMerchantId", intent.getStringExtra("bankMerchantId"))
            if (intent.hasExtra("isBankDecline")) putExtra("isBankDecline", intent.getBooleanExtra("isBankDecline", false))
            if (intent.hasExtra("tvr")) putExtra("tvr", intent.getStringExtra("tvr"))
            if (intent.hasExtra("tsi")) putExtra("tsi", intent.getStringExtra("tsi"))
            if (intent.hasExtra("maskedExpiryDate")) putExtra("maskedExpiryDate", intent.getStringExtra("maskedExpiryDate"))
            if (intent.hasExtra("cardholderName")) putExtra("cardholderName", intent.getStringExtra("cardholderName"))
        }
        startActivity(receiptIntent)
        finish() // Finish ResultActivity after navigating
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val approved = intent.getBooleanExtra("approved", false)
        val rc = intent.getStringExtra("rc") ?: ""
        val msg = intent.getStringExtra("msg") ?: ""
        val rrn = intent.getStringExtra("rrn")
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    ResultScreen(
                        approved = approved,
                        rc = rc,
                        msg = msg,
                        onPrint = { rrn ->
                            navigateToReceipt()
                        },
                        onDone = {
                            // Clear transaction state and turn off LEDs
                            try {
                                // OLD: emvManager.clearStateAndTurnOffLeds() - removed with old EMV code
                            } catch (e: Exception) {
                                android.util.Log.e("ResultActivity", "Failed to clear state: ${e.message}")
                            }
                            finish()
                        }
                    )
                    
                    // Auto-navigate to ReceiptActivity after 1 second
                    LaunchedEffect(Unit) {
                        delay(1000) // 1 second delay
                        navigateToReceipt()
                    }
                }
            }
        }
    }
}

