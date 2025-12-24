package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.neo.neopayplus.emv.EmvProvisioningManager
import com.neo.neopayplus.ui.screens.HomeScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Home Activity - Main entry point of the application.
 * Displays the home screen with action buttons for different flows.
 */
@AndroidEntryPoint
class HomeActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure PaySDK is connected before showing UI
        // CRITICAL: Do NOT call bindPaySDKService() here - it's handled by TransactionManager in Application.onCreate()
        // If service is not ready, wait for it (TransactionManager.init() was called in Application.onCreate())
        if (!com.neo.neopayplus.emv.TransactionManager.isReady()) {
            android.util.Log.w("HomeActivity", "PaySDK not ready yet - waiting for TransactionManager initialization")
            // Service should be initializing in background - wait a bit
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                val ready = com.neo.neopayplus.emv.TransactionManager.waitForReady(5000)
                if (!ready) {
                    android.util.Log.e("HomeActivity", "PaySDK failed to initialize within timeout")
                }
            }
        }
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    HomeScreen(
                        onCardPayment = {
                            launchAfterProvision {
                                startActivity(Intent(this@HomeActivity, EMVPaymentActivity::class.java).apply {
                                    putExtra("type", "purchase")
                                })
                            }
                        },
                        onRefund = {
                            launchAfterProvision {
                                startActivity(Intent(this@HomeActivity, RefundActivity::class.java))
                            }
                        },
                        onVoid = {
                            launchAfterProvision {
                                startActivity(Intent(this@HomeActivity, VoidActivity::class.java))
                            }
                        },
                        onSettlement = {
                            startActivity(Intent(this@HomeActivity, SettlementActivity::class.java))
                        },
                        onHistory = {
                            startActivity(Intent(this@HomeActivity, HistoryActivity::class.java))
                        },
                        onSettings = {
                            startActivity(Intent(this@HomeActivity, SettingsActivity::class.java))
                        }
                    )
                }
            }
        }
    }
    
    private fun launchAfterProvision(action: () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            val ok = EmvProvisioningManager.ensureProvisioned()
            if (ok) {
                action()
            } else {
                Toast.makeText(
                    this@HomeActivity,
                    "Unable to load EMV configuration. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

