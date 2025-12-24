package com.neo.neopayplus.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.api.SettlementApiFactory
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var showClearDialog by remember { mutableStateOf(false) }
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge, color = IndigoBlue)
        
        // Device info, Printer test, Versions, Network
        OutlinedButton(onClick = { /* test print */ }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Printer Test")
        }
        
        // Debug: ISO8583 Logs
        OutlinedButton(
            onClick = {
                val intent = Intent(context, com.neo.neopayplus.debug.DebugActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("View ISO8583 Logs")
        }
        
        // Clear All Transactions
        OutlinedButton(
            onClick = { showClearDialog = true },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Clear All Transactions")
        }
    }
    
    // Confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Transactions?") },
            text = { 
                Text("This will permanently delete all transaction history from both POS and backend. " +
                      "This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Clear all transactions from POS
                        TransactionJournal.clearJournal()
                        android.util.Log.d("SettingsScreen", "✓ Cleared POS transactions")
                        
                        // Clear all transactions from backend
                        val settlementService = SettlementApiFactory.getInstance()
                        if (settlementService is com.neo.neopayplus.api.SettlementApiServiceImpl) {
                            settlementService.clearBackendTransactions(
                                object : com.neo.neopayplus.api.SettlementApiServiceImpl.ClearTransactionsCallback {
                                    override fun onClearComplete(success: Boolean, message: String) {
                                        android.util.Log.d("SettingsScreen", if (success) "✓ $message" else "✗ $message")
                                    }
                                }
                            )
                        }
                        
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

