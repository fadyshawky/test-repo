package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.PaymentViewModel
import com.neo.neopayplus.ui.screens.AmountScreen
import com.neo.neopayplus.ui.screens.PaymentScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

/**
 * Payment Activity - Handles the complete payment flow.
 * Supports both purchase and refund transactions.
 */
@AndroidEntryPoint
class PaymentActivity : ComponentActivity() {
    
    private var currentAmount: BigDecimal? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val transactionType = intent.getStringExtra("type") ?: "purchase"
        val amountStr = intent.getStringExtra("amount")
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    PaymentFlowContent(
                        transactionType = transactionType,
                        initialAmount = amountStr,
                        onComplete = { approved, rc, msg, rrn ->
                            // Navigate to result screen
                            startActivity(Intent(this@PaymentActivity, ResultActivity::class.java).apply {
                                putExtra("approved", approved)
                                putExtra("rc", rc)
                                putExtra("msg", msg)
                                putExtra("rrn", rrn)
                            })
                            finish()
                        },
                        onCancel = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentFlowContent(
    transactionType: String,
    initialAmount: String?,
    onComplete: (Boolean, String, String, String?) -> Unit,
    onCancel: () -> Unit
) {
    var showAmountScreen by remember { mutableStateOf(initialAmount == null) }
    var amount by remember { mutableStateOf(initialAmount?.let { BigDecimal(it) } ?: BigDecimal.ZERO) }
    val vm: PaymentViewModel = hiltViewModel()
    
    if (showAmountScreen) {
        AmountScreen(
            type = transactionType,
            state = vm.uiState.collectAsState().value,
            onAmountConfirm = { enteredAmount ->
                amount = enteredAmount
                vm.onAmountEntered(enteredAmount)
                showAmountScreen = false
            },
            onBack = onCancel
        )
    } else {
        PaymentScreen(
            amount = amount,
            onCancel = {
                showAmountScreen = true
            },
            onComplete = onComplete
        )
    }
}

