package com.neo.neopayplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.PaymentState
import com.neo.neopayplus.domain.payment.PaymentViewModel
import com.neo.neopayplus.nav.Route
import com.neo.neopayplus.ui.screens.*
import com.neo.neopayplus.ui.screens.PaymentScreen
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint
import com.neo.neopayplus.emv.EmvProvisioningManager

/**
 * Legacy MainActivity - Redirects to HomeActivity for backward compatibility.
 * 
 * This Activity is kept for apps/scripts that may reference MainActivity.
 * All new code should use HomeActivity directly.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Redirect to HomeActivity
        val intent = android.content.Intent(this, com.neo.neopayplus.ui.activities.HomeActivity::class.java).apply {
            // Forward any intent extras
            intent.getStringExtra("nav_to")?.let { putExtra("nav_to", it) }
            intent.getStringExtra("amount")?.let { putExtra("amount", it) }
            intent.getStringExtra("amountDisplay")?.let { putExtra("amountDisplay", it) }
            // Copy all other extras
            intent.extras?.let { putExtras(it) }
        }
        startActivity(intent)
        finish()
    }
}

@Composable
fun NeoAppContent(
    initialDestination: String? = null,
    initialAmount: String? = null,
    initialAmountDisplay: String? = null
) {
    // Store initial values in remember for use in composable
    val savedInitialAmount = remember { initialAmount }
    val savedInitialAmountDisplay = remember { initialAmountDisplay }
    NeoTheme {
        androidx.compose.material3.Surface(
            modifier = Modifier.fillMaxSize(),
            color = com.neo.neopayplus.ui.theme.Background
        ) {
            val nav = rememberNavController()
            val context = LocalContext.current
            val provisionScope = rememberCoroutineScope()

            fun launchAfterProvision(action: () -> Unit) {
                provisionScope.launch {
                    val ok = EmvProvisioningManager.ensureProvisioned()
                    if (ok) {
                        action()
                    } else {
                        Toast.makeText(
                            context,
                            "Unable to load EMV configuration. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            
            // Handle initial navigation from intent extras
            LaunchedEffect(initialDestination) {
                when (initialDestination) {
                    "reversal" -> nav.navigate(Route.Reversal.path)
                    "payment" -> {
                        val provisioned = EmvProvisioningManager.ensureProvisioned()
                        if (!provisioned) {
                            Toast.makeText(
                                context,
                                "Unable to load EMV configuration. Please try again.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@LaunchedEffect
                        }
                        if (savedInitialAmount != null && savedInitialAmountDisplay != null) {
                            nav.navigate("${Route.Card.path}?amount=${savedInitialAmount}") {
                                popUpTo(Route.Home.path) { inclusive = false }
                            }
                        } else {
                            nav.navigate(Route.Amount.path("purchase"))
                        }
                    }
                    else -> { /* Stay on home */ }
                }
            }
            
            NavHost(navController = nav, startDestination = Route.Home.path) {
            composable(Route.Home.path) {
                HomeScreen(
                    onCardPayment = { 
                        // Clear transaction state and turn off LEDs before starting new transaction
                        try {
                            // OLD: emvManager.clearStateAndTurnOffLeds() - removed with old EMV code
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to clear state: ${e.message}")
                        }
                        launchAfterProvision { nav.navigate(Route.Amount.path("purchase")) } 
                    },
                    onRefund = { launchAfterProvision { nav.navigate(Route.Amount.path("refund")) } },
                    onVoid = { nav.navigate(Route.Reversal.path) },
                    onSettlement = { nav.navigate(Route.Settlement.path) },
                    onHistory = { nav.navigate(Route.History.path) },
                    onSettings = { nav.navigate(Route.Settings.path) }
                )
            }
            
            composable(
                route = Route.Amount.path,
                arguments = listOf(navArgument("type") { type = NavType.StringType; defaultValue = "purchase" })
            ) { backStackEntry ->
                val vm: PaymentViewModel = hiltViewModel()
                AmountScreen(
                    type = backStackEntry.arguments?.getString("type") ?: "purchase",
                    state = vm.uiState.collectAsState().value,
                    onAmountConfirm = { amount ->
                        vm.onAmountEntered(amount)
                        android.util.Log.e("MainActivity", "Amount entered: $amount")
                        // Navigate to unified PaymentScreen (no more separate Card/Processing screens)
                        nav.navigate(Route.Payment.path(amount.toPlainString()))
                    },
                    onBack = { nav.popBackStack() }
                )
            }
            
            // Unified Payment Screen - handles card detection AND processing in one place
            // This follows the SDK demo flow exactly
            composable(
                route = Route.Payment.path,
                arguments = listOf(navArgument("amount") { type = NavType.StringType; defaultValue = "0" })
            ) { backStackEntry ->
                val amountStr = backStackEntry.arguments?.getString("amount") ?: "0"
                val amount = try {
                    java.math.BigDecimal(amountStr)
                } catch (e: Exception) {
                    java.math.BigDecimal.ZERO
                }
                
                android.util.Log.e("MainActivity", "PaymentScreen: amount=$amount")
                
                PaymentScreen(
                    amount = amount,
                    onCancel = { 
                        nav.popBackStack(Route.Home.path, false) 
                    },
                    onComplete = { approved, rc, msg, rrn ->
                        android.util.Log.e("MainActivity", "Payment complete: approved=$approved, rc=$rc, msg=$msg")
                        nav.navigate(Route.Result.path(approved, rc, msg)) {
                            popUpTo(Route.Home.path) { inclusive = false }
                        }
                    }
                )
            }
            
            // NOTE: CardScreen was removed - the app now uses EMVPaymentActivity with EMVHandler directly
            // for the payment flow. This composable route is kept for backward compatibility
            // but redirects to the Payment screen.
            composable(
                route = "${Route.Card.path}?amount={amount}",
                arguments = listOf(navArgument("amount") { type = NavType.StringType; defaultValue = "" }),
                deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = Route.Card.path })
            ) { backStackEntry ->
                val amountArg = backStackEntry.arguments?.getString("amount") ?: "0"
                // Redirect to unified Payment screen
                LaunchedEffect(Unit) {
                    nav.navigate(Route.Payment.path(amountArg)) {
                        popUpTo(Route.Card.path) { inclusive = true }
                            }
                        }
            }
            
            composable(
                route = "${Route.Processing.path}?amount={amount}&entryMode={entryMode}",
                arguments = listOf(
                    navArgument("amount") { type = NavType.StringType; defaultValue = "" },
                    navArgument("entryMode") { type = NavType.StringType; defaultValue = "" }
                ),
                deepLinks = listOf(androidx.navigation.navDeepLink { uriPattern = Route.Processing.path })
            ) { backStackEntry ->
                val vm: PaymentViewModel = hiltViewModel()
                val amountStr = backStackEntry.arguments?.getString("amount") ?: ""
                val entryModeStr = backStackEntry.arguments?.getString("entryMode") ?: ""
                
                // Restore state from navigation arguments
                // ALWAYS restore entryMode since ViewModel might be recreated
                LaunchedEffect(amountStr, entryModeStr) {
                    val currentState = vm.uiState.value
                    android.util.Log.e("MainActivity", "ProcessingScreen: Restoring state from nav args - amountStr=$amountStr, entryModeStr=$entryModeStr, currentState=$currentState")
                    
                    // Always restore entryMode (critical for EMV to work)
                    if (entryModeStr.isNotEmpty() && vm.getEntryMode() == null) {
                        try {
                            val entryMode = EntryMode.valueOf(entryModeStr)
                            android.util.Log.e("MainActivity", "ProcessingScreen: Restoring entryMode=$entryMode")
                            // Set entry mode without changing state to Processing again
                            vm.setEntryModeOnly(entryMode)
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to restore entryMode: ${e.message}")
                        }
                    }
                    
                    // Only restore amount/state if not already Processing
                    if (currentState !is PaymentState.Processing) {
                        if (amountStr.isNotEmpty()) {
                            try {
                                val amount = java.math.BigDecimal(amountStr)
                                android.util.Log.e("MainActivity", "ProcessingScreen: Restoring amount=$amount")
                                vm.onAmountEntered(amount)
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Failed to restore amount: ${e.message}")
                            }
                        }
                        if (entryModeStr.isNotEmpty()) {
                            try {
                                val entryMode = EntryMode.valueOf(entryModeStr)
                                android.util.Log.e("MainActivity", "ProcessingScreen: Calling onCardPresented with entryMode=$entryMode")
                                vm.onCardPresented(entryMode)
                            } catch (e: Exception) {
                                android.util.Log.e("MainActivity", "Failed to restore entryMode: ${e.message}")
                            }
                        }
                    } else {
                        android.util.Log.e("MainActivity", "ProcessingScreen: State already Processing, entryMode=${vm.getEntryMode()}")
                    }
                }
                
                ProcessingScreen(
                    viewModel = vm,
                    state = vm.uiState.collectAsState().value,
                    onDone = { approved, rc, msg, rrn ->
                        nav.navigate(Route.Result.path(approved, rc, msg)) {
                            popUpTo(Route.Card.path) { inclusive = true }
                        }
                    }
                )
            }
            
            composable(
                route = Route.Result.path,
                arguments = listOf(
                    navArgument("approved") { type = NavType.BoolType },
                    navArgument("rc") { type = NavType.StringType; defaultValue = "" },
                    navArgument("msg") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val approved = backStackEntry.arguments?.getBoolean("approved") ?: false
                ResultScreen(
                    approved = approved,
                    rc = backStackEntry.arguments?.getString("rc") ?: "",
                    msg = backStackEntry.arguments?.getString("msg") ?: "",
                    onPrint = { rrn -> nav.navigate(Route.Receipt.path(rrn)) },
                    onDone = { 
                        // Clear transaction state and turn off LEDs when Done is clicked
                        // Especially important when error happens
                        try {
                            // OLD: emvManager.clearStateAndTurnOffLeds() - removed with old EMV code
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to clear state: ${e.message}")
                        }
                        nav.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } } 
                    }
                )
            }
            
            composable(
                route = Route.Receipt.path,
                arguments = listOf(navArgument("rrn") { type = NavType.StringType; defaultValue = "" })
            ) { backStackEntry ->
                // Create placeholder receipt data for navigation-based receipts
                val placeholderReceiptData = com.neo.neopayplus.receipt.ReceiptData(
                    orderId = "ORD123456789",
                    transactionId = "TXN987654321",
                    rrn = backStackEntry.arguments?.getString("rrn") ?: "123456789012",
                    internalTerminalId = com.neo.neopayplus.config.PaymentConfig.getTerminalId(),
                    internalMerchantId = com.neo.neopayplus.config.PaymentConfig.getMerchantId(),
                    bankTerminalId = "00000001",
                    bankMerchantId = "00000001",
                    merchantName = com.neo.neopayplus.config.PaymentConfig.MERCHANT_NAME,
                    transactionType = com.neo.neopayplus.receipt.ReceiptTransactionType.SALE,
                    entryMode = com.neo.neopayplus.receipt.ReceiptEntryMode.IC,
                    amount = java.math.BigDecimal("100.00"),
                    currency = "EGP",
                    maskedPan = "123456****1234",
                    aid = "A0000000041010",
                    applicationPreferredName = "VISA",
                    authCode = "AUTH123",
                    batchNumber = "001",
                    receiptNumber = "000001",
                    date = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                    time = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.US).format(java.util.Date()),
                    approved = false,
                    cvmMethod = com.neo.neopayplus.receipt.ReceiptCvmMethod.NO_PIN,
                    merchantLogoAssetPath = "images/receipt_logo.webp",
                    bankLogoAssetPath = "images/banque_misr_logo.png"
                )
                ReceiptScreen(
                    receiptData = placeholderReceiptData,
                    approved = false, // Default to false for navigation-based receipts
                    onPrintCustomerCopy = { 
                        // Clear transaction state and turn off LEDs after printing
                        try {
                            // OLD: emvManager.clearStateAndTurnOffLeds() - removed with old EMV code
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to clear state: ${e.message}")
                        }
                        /* call printer */ 
                    },
                    onCancelCustomerCopy = { /* cancelled */ },
                    onShare = { /* export pdf */ },
                    onDone = { 
                        // Clear transaction state and turn off LEDs when Done is clicked
                        try {
                            // OLD: emvManager.clearStateAndTurnOffLeds() - removed with old EMV code
                        } catch (e: Exception) {
                            android.util.Log.e("MainActivity", "Failed to clear state: ${e.message}")
                        }
                        nav.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } } 
                    }
                )
            }
            
            composable(Route.Reversal.path) {
                val vm: PaymentViewModel = hiltViewModel()
                ReversalScreen(
                    onCancel = { nav.popBackStack() },
                    onRrnEntered = { rrn ->
                        vm.onReversalRequested(rrn)
                        nav.navigate(Route.Processing.path)
                    },
                    onSelectFromHistory = { nav.navigate(Route.History.path) }
                )
            }
            
            composable(Route.Settlement.path) { SettlementScreen() }
            composable(Route.History.path) { HistoryScreen() }
            composable(Route.Settings.path) { SettingsScreen() }
        }
        }
    }
}
