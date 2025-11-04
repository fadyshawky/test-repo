package com.neo.neopayplus.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.neo.neopayplus.ui.screens.*
import kotlinx.coroutines.delay

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    var amountCents by remember { mutableStateOf(0L) }
    var cardNo by remember { mutableStateOf<String?>(null) }
    var requiresPin by remember { mutableStateOf(false) }
    
    NavHost(navController, startDestination = Route.Home.path) {
        composable(Route.Home.path) {
            HomeScreen(
                onCardPayment = { navController.navigate(Route.Amount.path) },
                onRefund = { navController.navigate(Route.RefundAmount.path) },
                onVoid = { navController.navigate(Route.TxnHistory.path) },
                onSettlement = { navController.navigate(Route.SettlementConfirm.path) },
                onHistory = { navController.navigate(Route.TxnHistory.path) }
            )
        }
        
        composable(Route.Amount.path) {
            AmountEntryScreen(
                title = "Card Payment",
                onConfirm = { cents ->
                    amountCents = cents
                    navController.navigate(Route.CardDetect.path)
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable(Route.RefundAmount.path) {
            AmountEntryScreen(
                title = "Refund",
                onConfirm = { cents ->
                    amountCents = cents
                    navController.navigate(Route.CardDetect.path + "?refund=true")
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable(Route.CardDetect.path) {
            CardDetectScreen(
                onCardRead = { needsPin, detectedCardNo ->
                    requiresPin = needsPin
                    // Use detected card number if available, otherwise will extract during PIN entry
                    if (detectedCardNo != null) {
                        cardNo = detectedCardNo
                    }
                    if (needsPin) {
                        navController.navigate(Route.Pin.path)
                    } else {
                        navController.navigate(Route.Processing.path)
                    }
                },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable(Route.Pin.path) {
            PinEntryScreen(
                cardNo = cardNo ?: "",
                pinType = 1, // Online PIN
                onPinEntered = { navController.navigate(Route.Processing.path) },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable(Route.Processing.path) {
            ProcessingScreen(
                amountCents = amountCents,
                onDone = { approved ->
                    navController.navigate(Route.Result.build(approved))
                }
            )
        }
        
        composable(
            Route.Result.path,
            arguments = listOf(navArgument("approved") { 
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry ->
            val approved = backStackEntry.arguments?.getBoolean("approved") ?: false
            ResultScreen(
                approved = approved,
                onNewSale = {
                    navController.popBackStack(Route.Home.path, false)
                },
                onHome = {
                    navController.popBackStack(Route.Home.path, false)
                }
            )
        }
        
        composable(Route.TxnHistory.path) {
            TxnHistoryScreen(
                onSelect = { rrn -> navController.navigate(Route.TxnDetails.build(rrn)) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            Route.TxnDetails.path,
            arguments = listOf(navArgument("rrn") { type = NavType.StringType })
        ) { backStackEntry ->
            val rrn = backStackEntry.arguments?.getString("rrn").orEmpty()
            TxnDetailsScreen(
                rrn = rrn,
                onVoid = { navController.navigate(Route.VoidConfirm.build(rrn)) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(
            Route.VoidConfirm.path,
            arguments = listOf(navArgument("rrn") { type = NavType.StringType })
        ) { backStackEntry ->
            val rrn = backStackEntry.arguments?.getString("rrn").orEmpty()
            ConfirmDialogScreen(
                title = "Void transaction?",
                message = "RRN: $rrn",
                confirmText = "Void",
                onConfirm = { navController.navigate(Route.Processing.path) },
                onCancel = { navController.popBackStack() }
            )
        }
        
        composable(Route.SettlementConfirm.path) {
            SettlementConfirmScreen(
                onStart = { navController.navigate(Route.SettlementProcessing.path) },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Route.SettlementProcessing.path) {
            SettlementProcessingScreen(
                onDone = { ok -> navController.navigate(Route.SettlementResult.path + "?ok=$ok") }
            )
        }
        
        composable(Route.SettlementResult.path) {
            SettlementResultScreen(
                onHome = { navController.popBackStack(Route.Home.path, false) }
            )
        }
    }
}

