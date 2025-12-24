package com.neo.neopayplus.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.NeoTheme
import java.math.BigDecimal

/**
 * Void Activity - Starts with transaction ID input, then shows transaction details and starts EMV void
 */
class VoidActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NeoTheme {
                VoidScreen(
                    onTransactionFound = { transaction ->
                        // Start EMV void with transaction details
                        val amount = try {
                            transaction.amount?.toLongOrNull()?.let {
                                BigDecimal(it).movePointLeft(2).toDouble()
                            } ?: 0.0
                        } catch (e: Exception) {
                            0.0
                        }
                        
                        startActivity(Intent(this@VoidActivity, EMVPaymentActivity::class.java).apply {
                            putExtra("type", "void")
                            putExtra("amount", amount)
                            putExtra("transactionId", transaction.transactionId)
                            putExtra("originalRrn", transaction.rrn)
                            putExtra("originalPan", transaction.pan)
                            putExtra("originalAuthCode", transaction.authCode)
                        })
                        finish()
                    },
                    onBack = {
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun VoidScreen(
    onTransactionFound: (TransactionJournal.TransactionRecord) -> Unit,
    onBack: () -> Unit
) {
    var transactionIdInput by remember { mutableStateOf("") }
    var searchedTransaction by remember { mutableStateOf<TransactionJournal.TransactionRecord?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Void Transaction",
            style = MaterialTheme.typography.headlineMedium,
            color = IndigoBlue
        )
        
        // Transaction ID Input (user enters just the number, TXN is auto-prefixed)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TXN",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 8.dp)
            )
            OutlinedTextField(
                value = transactionIdInput,
                onValueChange = { 
                    // Only allow digits (the number part after TXN)
                    val digitsOnly = it.filter { char -> char.isDigit() }
                    transactionIdInput = digitsOnly
                    searchedTransaction = null
                    errorMessage = null
                },
                label = { Text("Transaction ID Number") },
                placeholder = { Text("Enter number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }
        
        // Search Button
        Button(
            onClick = {
                if (transactionIdInput.isBlank()) {
                    errorMessage = "Please enter a transaction ID number"
                    return@Button
                }
                
                // Debug: Log all transactions to see what we have
                val allTransactions = TransactionJournal.getAllTransactions()
                android.util.Log.e("VoidActivity", "=== DEBUG: All transactions in journal ===")
                allTransactions.take(10).forEachIndexed { index, tx ->
                    android.util.Log.e("VoidActivity", "TX[$index]: ID=${tx.transactionId}, RRN=${tx.rrn}, Status=${tx.status}, Settled=${tx.isSettled}")
                }
                android.util.Log.e("VoidActivity", "Searching for: ${transactionIdInput.trim()}")
                
                // Search for voidable transaction (approved, not refunded, not voided, not settled)
                val transaction = TransactionJournal.findVoidableTransactionById(transactionIdInput.trim())
                if (transaction != null) {
                    searchedTransaction = transaction
                    errorMessage = null
                } else {
                    searchedTransaction = null
                    // Check if transaction exists but is not voidable
                    val existingTransaction = TransactionJournal.findTransactionById(transactionIdInput.trim())
                    if (existingTransaction != null) {
                        when {
                            "VOID".equals(existingTransaction.status) -> {
                                errorMessage = "This transaction has already been voided."
                            }
                            "REFUNDED".equals(existingTransaction.status) -> {
                                errorMessage = "This transaction has already been refunded and cannot be voided."
                            }
                            existingTransaction.isSettled -> {
                                errorMessage = "This transaction cannot be voided. Transaction has been settled. Only unsettled transactions can be voided."
                            }
                            !"APPROVED".equals(existingTransaction.status) -> {
                                errorMessage = "This transaction cannot be voided. Only approved transactions can be voided."
                            }
                            existingTransaction.isReversal -> {
                                errorMessage = "This transaction is a reversal and cannot be voided."
                            }
                            else -> {
                                errorMessage = "This transaction cannot be voided."
                            }
                        }
                    } else {
                        errorMessage = "Transaction not found. Please check the Transaction ID and try again."
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search Transaction")
        }
        
        // Error Message
        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // Transaction Details
        searchedTransaction?.let { transaction ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Transaction Found",
                        style = MaterialTheme.typography.titleMedium,
                        color = IndigoBlue
                    )
                    
                    TransactionDetailRow("Transaction ID:", transaction.transactionId ?: "N/A")
                    TransactionDetailRow("RRN:", transaction.rrn ?: "N/A")
                    TransactionDetailRow("Amount:", formatAmount(transaction.amount))
                    TransactionDetailRow("Status:", transaction.status ?: "N/A")
                    TransactionDetailRow("Date:", formatDate(transaction.date, transaction.time))
                    TransactionDetailRow("Card:", transaction.pan ?: "N/A")
                    
                    if (transaction.cardBrand != null) {
                        TransactionDetailRow("Brand:", transaction.cardBrand)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Proceed Button
                    Button(
                        onClick = { onTransactionFound(transaction) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Proceed with Void")
                    }
                }
            }
        }
        
        // Back Button
        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

