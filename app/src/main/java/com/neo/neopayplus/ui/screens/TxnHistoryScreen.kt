package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal

data class TxnListItem(val rrn: String?, val amount: String?, val responseCode: String?)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxnHistoryScreen(
    onSelect: (rrn: String) -> Unit,
    onBack: () -> Unit
) {
    val items by remember {
        mutableStateOf(
            TransactionJournal.getAllTransactions().take(50).map { tx ->
                TxnListItem(tx.rrn, tx.amount, tx.responseCode)
            }
        )
    }
    
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Transactions") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("←")
                }
            }
        )
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
            items(items) { tx ->
                Card(
                    Modifier.fillMaxWidth().padding(vertical = 6.dp).clickable { 
                        onSelect(tx.rrn ?: "") 
                    }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("RRN: ${tx.rrn ?: "N/A"}")
                        Text("Amount: ${tx.amount ?: "0.00"}")
                        Text(if ("00".equals(tx.responseCode)) "✓ Approved" else "✗ Declined")
                    }
                }
            }
        }
    }
}

