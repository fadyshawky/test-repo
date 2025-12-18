package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.neo.neopayplus.domain.payment.PaymentViewModel
import com.neo.neopayplus.ui.screens.ReversalScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Reversal Activity - Handles transaction reversals/voids.
 */
@AndroidEntryPoint
class ReversalActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    val vm: PaymentViewModel = hiltViewModel()
                    ReversalScreen(
                        onCancel = {
                            finish()
                        },
                        onRrnEntered = { rrn ->
                            vm.onReversalRequested(rrn)
                            startActivity(Intent(this@ReversalActivity, ProcessingActivity::class.java))
                        },
                        onSelectFromHistory = {
                            startActivity(Intent(this@ReversalActivity, HistoryActivity::class.java))
                        }
                    )
                }
            }
        }
    }
}

