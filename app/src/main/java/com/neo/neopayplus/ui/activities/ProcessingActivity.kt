package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.PaymentViewModel
import com.neo.neopayplus.ui.screens.ProcessingScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Processing Activity - Shows transaction processing status.
 * Used for both payment and reversal processing.
 */
@AndroidEntryPoint
class ProcessingActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val amountStr = intent.getStringExtra("amount") ?: ""
        val entryModeStr = intent.getStringExtra("entryMode") ?: ""
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    val vm: PaymentViewModel = hiltViewModel()
                    
                    // Restore state from intent extras
                    LaunchedEffect(amountStr, entryModeStr) {
                        if (entryModeStr.isNotEmpty()) {
                            try {
                                val entryMode = EntryMode.valueOf(entryModeStr)
                                vm.setEntryModeOnly(entryMode)
                            } catch (e: Exception) {
                                android.util.Log.e("ProcessingActivity", "Failed to restore entryMode: ${e.message}")
                            }
                        }
                        if (amountStr.isNotEmpty()) {
                            try {
                                val amount = java.math.BigDecimal(amountStr)
                                vm.onAmountEntered(amount)
                            } catch (e: Exception) {
                                android.util.Log.e("ProcessingActivity", "Failed to restore amount: ${e.message}")
                            }
                        }
                    }
                    
                    ProcessingScreen(
                        viewModel = vm,
                        state = vm.uiState.collectAsState().value,
                        onDone = { approved, rc, msg, rrn ->
                            startActivity(Intent(this@ProcessingActivity, ResultActivity::class.java).apply {
                                putExtra("approved", approved)
                                putExtra("rc", rc)
                                putExtra("msg", msg)
                                putExtra("rrn", rrn)
                            })
                            finish()
                        }
                    )
                }
            }
        }
    }
}

