package com.neo.neopayplus.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.PaymentState
import com.neo.neopayplus.domain.payment.PaymentViewModel
import com.neo.neopayplus.emv.TransactionState
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.MutedLavender
import com.sunmi.payservice.AidlConstantsV2
import java.math.BigDecimal

@Composable
fun ProcessingScreen(
    viewModel: PaymentViewModel,
    state: PaymentState,
    onDone: (approved: Boolean, rc: String, msg: String, rrn: String?) -> Unit
) {
    // Use ViewModel's state flows (Clean Architecture)
    val reversalService = viewModel.reversalService
    
    // Use snapshotFlow to observe state changes efficiently
    val isReversal = remember { viewModel.isReversal() }
    
    // Collect states efficiently - only recompose when values actually change
    // Use initial value to prevent initial jump
    val transactionState by viewModel.transactionStateFlow.collectAsState(
        initial = TransactionState.Idle
    )
    val reversalState by reversalService.reversalState.collectAsState(
        initial = com.neo.neopayplus.emv.ReversalService.ReversalState.Idle
    )
    val currentState by viewModel.uiState.collectAsState()
    
    // Compute active state without creating new state objects
    // Use derivedStateOf for stable state computation
    val activeState = remember(isReversal, transactionState, reversalState) {
        if (isReversal) reversalState else transactionState
    }
    
    // Track if transaction has been started to avoid duplicate calls
    var transactionStarted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    // Start transaction/reversal when screen appears
    // NOTE: For the SDK demo flow, the EMV transaction is already started in CardScreen
    // before navigation. We only start here as a fallback for the legacy flow.
    LaunchedEffect(Unit) {
        android.util.Log.e("ProcessingScreen", "ProcessingScreen initialized")
        android.util.Log.e("ProcessingScreen", "  isReversal=$isReversal, transactionStarted=$transactionStarted")
        android.util.Log.e("ProcessingScreen", "  transactionState=$transactionState")
        
        // Check if transaction is already running (SDK demo flow - started in CardScreen)
        val isAlreadyProcessing = transactionState is TransactionState.Processing ||
                                  transactionState is TransactionState.PinRequired
        
        if (!viewModel.isReversal() && !transactionStarted && !isAlreadyProcessing) {
            // Legacy flow fallback - transaction not yet started
            android.util.Log.e("ProcessingScreen", "⚠️ Legacy flow: Starting EMV transaction here (may cause TLV issues)")
            transactionStarted = true
            coroutineScope.launch {
                try {
                    val stan = withContext(Dispatchers.IO) {
                        com.neo.neopayplus.db.TxnDb(com.neo.neopayplus.MyApplication.app).nextStan()
                    }
                    withContext(Dispatchers.Main) {
                        android.util.Log.e("ProcessingScreen", "Starting EMV transaction with STAN: $stan")
                        viewModel.startEmv(stan)
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ProcessingScreen", "ERROR starting EMV transaction: ${e.message}", e)
                    viewModel.onTransactionError("Failed to start transaction: ${e.message}")
                }
            }
        } else if (isAlreadyProcessing) {
            // SDK demo flow - transaction already started in CardScreen
            android.util.Log.e("ProcessingScreen", "✓ SDK demo flow: Transaction already started, just observing")
            transactionStarted = true
        } else if (isReversal && !transactionStarted) {
            transactionStarted = true
            // Reversal is already started by onReversalRequested() in ViewModel
            android.util.Log.e("ProcessingScreen", "Reversal already started by ViewModel")
        }
    }
    
    // Handle transaction/reversal state changes
    LaunchedEffect(activeState) {
        if (isReversal) {
            when (val currentState = activeState) {
                is com.neo.neopayplus.emv.ReversalService.ReversalState.Completed -> {
                    viewModel.onTransactionCompleted(
                        currentState.approved,
                        currentState.rc,
                        currentState.msg,
                        currentState.rrn
                    )
                    onDone(currentState.approved, currentState.rc, currentState.msg, currentState.rrn)
                }
                is com.neo.neopayplus.emv.ReversalService.ReversalState.Error -> {
                    viewModel.onTransactionError(currentState.message)
                }
                else -> { /* Processing - continue */ }
            }
        } else {
            when (val currentState = activeState) {
                is TransactionState.Completed -> {
                    viewModel.onTransactionCompleted(
                        currentState.approved,
                        currentState.rc,
                        currentState.msg,
                        currentState.rrn
                    )
                    onDone(currentState.approved, currentState.rc, currentState.msg, currentState.rrn)
                }
                is TransactionState.Error -> {
                    viewModel.onTransactionError(currentState.message)
                }
                else -> { /* Processing or PinRequired - continue */ }
            }
        }
    }
    
    // Display appropriate UI based on transaction/reversal state
    // Use fixed layout structure to prevent jumping when state changes
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Fixed height container to prevent layout shifts
        Box(
            modifier = Modifier
                .height(120.dp) // Fixed height to prevent jumping
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Use Crossfade for smooth transitions between states
            Crossfade(
                targetState = activeState,
                modifier = Modifier.fillMaxWidth(),
                label = "processing_state_transition"
            ) { state ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (isReversal) {
                        when (state) {
                            is com.neo.neopayplus.emv.ReversalService.ReversalState.Idle -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text("Initializing reversal...", color = MutedLavender)
                            }
                            is com.neo.neopayplus.emv.ReversalService.ReversalState.Processing -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(state.message, color = MutedLavender)
                            }
                            is com.neo.neopayplus.emv.ReversalService.ReversalState.Error -> {
                                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            }
                            is com.neo.neopayplus.emv.ReversalService.ReversalState.Completed -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text("Reversal completed", color = MutedLavender)
                            }
                        }
                    } else {
                        when (state) {
                            is TransactionState.Idle -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text("Initializing...", color = MutedLavender)
                            }
                            is TransactionState.Processing -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(state.message, color = MutedLavender)
                            }
                            is TransactionState.PinRequired -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    if (state.isOnline) "Enter PIN (Online)" else "Enter PIN (Offline)",
                                    color = MutedLavender
                                )
                            }
                            is TransactionState.Error -> {
                                Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                            }
                            is TransactionState.Completed -> {
                                CircularProgressIndicator()
                                Spacer(Modifier.height(16.dp))
                                Text("Transaction completed", color = MutedLavender)
                            }
                        }
                    }
                }
            }
        }
    }
}
