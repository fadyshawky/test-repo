package com.neo.neopayplus.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.receipt.ReceiptDataMapper
import com.neo.neopayplus.ui.activities.ReceiptActivity
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun HistoryScreen() {
    val transactions = remember { TransactionJournal.getAllTransactions() }
    val context = LocalContext.current
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Transactions History",
            style = MaterialTheme.typography.titleLarge,
            color = IndigoBlue
        )
        
        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    "No transactions found",
                    modifier = Modifier.padding(20.dp),
                    color = MutedLavender
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions) { tx ->
                    TransactionCard(
                        transaction = tx,
                        onReprint = { 
                            // Navigate to ReceiptActivity with transaction data for reprint
                            navigateToReceiptForReprint(context, tx)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: TransactionJournal.TransactionRecord,
    onReprint: () -> Unit
) {
    val statusColor = when (transaction.status) {
        "APPROVED" -> IndigoBlue
        "DECLINED" -> MaterialTheme.colorScheme.error
        else -> MutedLavender
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    transaction.status ?: "UNKNOWN",
                    style = MaterialTheme.typography.titleMedium,
                    color = statusColor
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    // Show transaction type badge
                    val transactionTypeBadge = when (transaction.transactionType) {
                        "20" -> "REFUND"
                        "40" -> "VOID"
                        else -> null
                    }
                    if (transactionTypeBadge != null) {
                        Surface(
                            color = MutedLavender.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                transactionTypeBadge,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedLavender
                            )
                        }
                    }
                    if (transaction.isReversal) {
                        Surface(
                            color = MutedLavender.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                "REVERSAL",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedLavender
                            )
                        }
                    }
                }
            }
            
            // Amount
            val amount = try {
                val amountMinor = transaction.amount?.toLongOrNull() ?: 0L
                BigDecimal(amountMinor).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
            } catch (e: Exception) {
                BigDecimal.ZERO
            }
            Text(
                "EGP ${amount}",
                style = MaterialTheme.typography.headlineSmall,
                color = IndigoBlue
            )
            
            // Details
            if (!transaction.rrn.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("RRN:", color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                    Text(transaction.rrn, color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            // Date/Time
            val dateTime = if (!transaction.date.isNullOrEmpty() && !transaction.time.isNullOrEmpty()) {
                "${transaction.date} ${transaction.time}"
            } else {
                "N/A"
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Date:", color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                Text(dateTime, color = MutedLavender, style = MaterialTheme.typography.bodySmall)
            }
            
            // Response code
            if (!transaction.responseCode.isNullOrEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Response:", color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                    Text(transaction.responseCode, color = MutedLavender, style = MaterialTheme.typography.bodySmall)
                }
            }
            
            // Reprint button (only for approved transactions)
            if ("APPROVED" == transaction.status && "00" == transaction.responseCode) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onReprint,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IndigoBlue
                    )
                ) {
                    Text("Reprint Receipt")
                }
            }
        }
    }
}

/**
 * Navigate to ReceiptActivity with transaction data for reprint
 */
private fun navigateToReceiptForReprint(context: Context, transaction: TransactionJournal.TransactionRecord) {
    val amount = try {
        transaction.amount?.toLongOrNull()?.let { 
            BigDecimal(it).movePointLeft(2).toDouble() 
        } ?: 0.0
    } catch (e: Exception) {
        0.0
    }
    
    // Map transaction type
    val transactionType = when (transaction.transactionType) {
        "20" -> "REFUND"
        "40" -> "VOID"
        else -> "SALE"
    }
    
    // Use saved entry mode from TransactionJournal, default to IC if not available
    val entryMode = transaction.entryMode ?: "IC"
    
    val intent = Intent(context, ReceiptActivity::class.java).apply {
        putExtra("approved", "APPROVED" == transaction.status)
        putExtra("rrn", transaction.rrn)
        putExtra("amount", amount)
        putExtra("currency", transaction.currencyCode ?: "EGP")
        putExtra("cardPan", transaction.pan)
        putExtra("aid", transaction.aid) // Use saved AID from TransactionJournal
        putExtra("applicationPreferredName", null as String?)
        putExtra("entryMode", entryMode)
        putExtra("transactionType", transactionType)
        putExtra("authCode", transaction.authCode)
        putExtra("responseCode", transaction.responseCode)
        putExtra("responseMessage", if ("00" == transaction.responseCode) "APPROVED" else "DECLINED")
        putExtra("stan", 0) // TransactionJournal doesn't store STAN
        putExtra("cvmMethod", "NO_PIN") // Default, TransactionJournal doesn't store CVM
        putExtra("isReprint", true) // Flag to indicate this is a reprint
        // Convert date/time format: YYMMDD -> yyyy-MM-dd, HHMMSS -> HH:mm:ss
        val formattedDate = transaction.date?.let { dateStr ->
            if (dateStr.length == 6) {
                val year = "20${dateStr.substring(0, 2)}"
                val month = dateStr.substring(2, 4)
                val day = dateStr.substring(4, 6)
                "$year-$month-$day"
            } else null
        }
        val formattedTime = transaction.time?.let { timeStr ->
            if (timeStr.length == 6) {
                "${timeStr.substring(0, 2)}:${timeStr.substring(2, 4)}:${timeStr.substring(4, 6)}"
            } else null
        }
        putExtra("cardholderName", transaction.cardholderName)
        putExtra("date", formattedDate ?: "")
        putExtra("time", formattedTime ?: "")
    }
    
    context.startActivity(intent)
}

