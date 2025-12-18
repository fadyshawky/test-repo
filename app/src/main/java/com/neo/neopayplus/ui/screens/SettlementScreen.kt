package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun SettlementScreen() {
    val transactions = remember { TransactionJournal.getAllTransactions() }
    
    // Calculate batch totals
    val approvedCount = transactions.count { it.status == "APPROVED" && !it.isReversal }
    val declinedCount = transactions.count { it.status == "DECLINED" }
    val reversalCount = transactions.count { it.isReversal }
    
    val totalAmount = transactions
        .filter { it.status == "APPROVED" && !it.isReversal }
        .sumOf { tx ->
            try {
                tx.amount?.toLongOrNull() ?: 0L
            } catch (e: Exception) {
                0L
            }
        }
    
    val totalAmountDecimal = BigDecimal(totalAmount).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Settlement", style = MaterialTheme.typography.titleLarge, color = IndigoBlue)
        
        // Batch info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Batch Summary",
                    style = MaterialTheme.typography.titleMedium,
                    color = IndigoBlue
                )
                
                Divider()
                
                // Total amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount:", color = MutedLavender)
                    Text(
                        "EGP $totalAmountDecimal",
                        style = MaterialTheme.typography.headlineSmall,
                        color = IndigoBlue
                    )
                }
                
                // Transaction counts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Approved:", color = MutedLavender)
                    Text("$approvedCount", color = IndigoBlue)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Declined:", color = MutedLavender)
                    Text("$declinedCount", color = MaterialTheme.colorScheme.error)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Reversals:", color = MutedLavender)
                    Text("$reversalCount", color = MutedLavender)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Transactions:", color = MutedLavender)
                    Text("${transactions.size}", color = IndigoBlue)
                }
                
                // Terminal info
                Divider()
                Text(
                    "Terminal: ${com.neo.neopayplus.config.PaymentConfig.getTerminalId()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedLavender
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { /* trigger settlement */ },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = approvedCount > 0
        ) {
            Text("Settle Now", color = White)
        }
    }
}

