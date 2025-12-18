package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.BuildConfig
import com.neo.neopayplus.MyApplication
import com.neo.neopayplus.db.TxnDb
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.emv.TransactionState
import com.neo.neopayplus.app.di.ServiceLocator
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.sunmi.pay.hardware.aidl.AidlConstants
import com.sunmi.payservice.AidlConstantsV2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

/**
 * Unified Payment Screen - Handles entire EMV flow in one screen
 * 
 * This follows the Sunmi SDK demo pattern where:
 * 1. initEmvProcess() + setTlvList() happen immediately
 * 2. checkCard() waits for card
 * 3. findRFCard/findICCard callback -> IMMEDIATELY calls transactProcess()
 * 4. EMV callbacks (onAppFinalSelect, onConfirmCardNo, etc.) are handled
 * 5. Transaction completes
 * 
 * NO navigation between card detection and transaction processing!
 */
@Composable
fun PaymentScreen(
    amount: BigDecimal,
    onCancel: () -> Unit,
    onComplete: (approved: Boolean, rc: String, msg: String, rrn: String?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    
    // State machine for the payment flow
    var paymentPhase by remember { mutableStateOf(PaymentPhase.INITIALIZING) }
    var statusMessage by remember { mutableStateOf("Initializing...") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var detectedEntryMode by remember { mutableStateOf<EntryMode?>(null) }
    var isApproved by remember { mutableStateOf(false) } // Track approval status
    
    // Observe transaction state - TODO: Get from PaymentViewModel
    val transactionState = remember { mutableStateOf<TransactionState>(TransactionState.Idle) }
    
    // Handle transaction state changes - simple and clean
    // TODO: Update to observe PaymentViewModel.transactionStateFlow
    LaunchedEffect(transactionState.value) {
        when (val state = transactionState.value) {
            is TransactionState.Idle -> {
                // Initial state - do nothing
            }
            is TransactionState.Processing -> {
                paymentPhase = PaymentPhase.PROCESSING
                statusMessage = state.message
            }
            is TransactionState.PinRequired -> {
                paymentPhase = PaymentPhase.PIN_ENTRY
                statusMessage = if (state.isOnline) "Enter PIN" else "Enter Offline PIN"
            }
            is TransactionState.Completed -> {
                paymentPhase = PaymentPhase.COMPLETED
                isApproved = state.approved
                statusMessage = if (state.approved) "Transaction Approved" else "Transaction Declined"
                // Navigate immediately - UI will show result screen
                onComplete(state.approved, state.rc, state.msg, state.rrn)
            }
            is TransactionState.Error -> {
                paymentPhase = PaymentPhase.ERROR
                isApproved = false
                errorMessage = state.message
            }
        }
    }
    
    // Main EMV flow - simple and clean
    LaunchedEffect(Unit) {
        // Validate services
        if (MyApplication.app.emvOptV2 == null) {
            paymentPhase = PaymentPhase.ERROR
            errorMessage = "Payment service not available"
            return@LaunchedEffect
        }
        
        // Get STAN and prepare amount
        val stan = withContext(Dispatchers.IO) {
            TxnDb(MyApplication.app).nextStan()
        }
        val amountPiasters = amount.multiply(BigDecimal("100")).toLong().toString().padStart(12, '0')
        
        // Reset approval status for new transaction
        isApproved = false
        
        // Update UI
        paymentPhase = PaymentPhase.WAITING_FOR_CARD
        statusMessage = "Tap, insert, or swipe card"
        
        // TODO: Implement PaymentViewModel.startEmv(stan) integration
        // The new architecture uses EmvDataSource via PaymentViewModel
    }
    
    // Cleanup on cancel
    DisposableEffect(Unit) {
        onDispose {
            // Only cancel if still waiting for card
            // OLD: emvManager?.cancelTransaction() removed
            // TODO: Cancel via PaymentViewModel or EmvDataSource
            // if (paymentPhase == PaymentPhase.WAITING_FOR_CARD) {
            //     emvManager?.cancelTransaction()
            // }
        }
    }
    
    // UI
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column {
            Text(
                text = when (paymentPhase) {
                    PaymentPhase.INITIALIZING -> "Initializing"
                    PaymentPhase.WAITING_FOR_CARD
                     -> "Present Card"
                    PaymentPhase.PROCESSING -> "Processing"
                    PaymentPhase.PIN_ENTRY -> "PIN Entry"
                    PaymentPhase.COMPLETED -> "Complete"
                    PaymentPhase.ERROR -> "Error"
                },
                style = MaterialTheme.typography.titleLarge,
                color = IndigoBlue
            )
            
            Spacer(Modifier.height(8.dp))
            
            // Amount display
            Text(
                text = "EGP ${amount.setScale(2)}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = IndigoBlue
            )
        }
        
        // Center content
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon based on phase and detected entry mode
            val icon = when {
                paymentPhase == PaymentPhase.COMPLETED && isApproved -> Icons.Outlined.CheckCircle
                paymentPhase == PaymentPhase.COMPLETED && !isApproved -> Icons.Outlined.Close
                paymentPhase == PaymentPhase.ERROR -> Icons.Outlined.Close
                // Keep the detected entry mode icon throughout the transaction
                detectedEntryMode == EntryMode.ICC -> Icons.Outlined.CreditCard
                detectedEntryMode == EntryMode.NFC -> Icons.Outlined.Nfc
                // Default to NFC icon when waiting
                else -> Icons.Outlined.Nfc
            }
            
            val iconTint = when {
                paymentPhase == PaymentPhase.COMPLETED && isApproved -> Color(0xFF4CAF50) // Green for approved
                paymentPhase == PaymentPhase.COMPLETED && !isApproved -> Color(0xFFF44336) // Red for declined
                paymentPhase == PaymentPhase.ERROR -> Color(0xFFF44336) // Red for error
                else -> IndigoBlue
            }
            
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = iconTint
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Status message
            Text(statusMessage, color = MutedLavender)
            
            // Entry mode indicator
            detectedEntryMode?.let { mode ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when (mode) {
                        EntryMode.NFC -> "Contactless"
                        EntryMode.ICC -> "Chip Card"
                        EntryMode.MAGNETIC -> "Swipe"
                    },
                    color = MutedLavender,
                    fontSize = 14.sp
                )
            }
            
            // Progress indicator for active phases only (not when completed or error)
            if (paymentPhase in listOf(
                PaymentPhase.INITIALIZING,
                PaymentPhase.WAITING_FOR_CARD,
                PaymentPhase.PROCESSING,
                PaymentPhase.PIN_ENTRY
            )) {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator(color = IndigoBlue)
            }
            
            // Error message
            errorMessage?.let {
                Spacer(Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
        
        // Cancel button (only show when appropriate)
        if (paymentPhase in listOf(PaymentPhase.WAITING_FOR_CARD, PaymentPhase.ERROR)) {
            OutlinedButton(
                onClick = {
                    // TODO: Cancel via PaymentViewModel or EmvDataSource if needed
                    onCancel()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (paymentPhase == PaymentPhase.ERROR) "Back" else "Cancel")
            }
        } else {
            // Spacer to maintain layout
            Spacer(Modifier.height(52.dp))
        }
    }
}

/**
 * Payment flow phases
 */
enum class PaymentPhase {
    INITIALIZING,
    WAITING_FOR_CARD,
    PROCESSING,
    PIN_ENTRY,
    COMPLETED,
    ERROR
}

