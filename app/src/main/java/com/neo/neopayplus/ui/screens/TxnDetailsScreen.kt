package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TxnDetailsScreen(
    rrn: String,
    onVoid: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(topBar = { 
        TopAppBar(
            title = { Text("Txn Details") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Text("←")
                }
            }
        ) 
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(16.dp), 
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("RRN: $rrn")
            Text("Amount: 100.00")
            Text("PAN: **** **** **** 1234")
            Text("Date/Time: 2025-11-03 18:00")
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f), 
                    onClick = onBack
                ) { 
                    Text("Back") 
                }
                Button(
                    modifier = Modifier.weight(1f), 
                    onClick = onVoid
                ) { 
                    Text("Void") 
                }
            }
        }
    }
}

