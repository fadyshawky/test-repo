package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun HistoryScreen() {
    val transactions = remember { TransactionJournal.getAllTransactions() }
    
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
                    TransactionCard(transaction = tx)
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: TransactionJournal.TransactionRecord) {
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
        }
    }
}

