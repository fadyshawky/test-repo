package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.ui.components.NeoTopBar
import com.neo.neopayplus.ui.components.NumPad
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White

@Composable
fun ReversalScreen(
    onCancel: () -> Unit,
    onRrnEntered: (String) -> Unit,
    onSelectFromHistory: () -> Unit
) {
    var rrn by remember { mutableStateOf("") }
    val lastRrn = remember { TransactionJournal.getLastRrn() }
    
    // Auto-fill last RRN if available
    LaunchedEffect(Unit) {
        if (lastRrn != null && lastRrn.isNotEmpty()) {
            rrn = lastRrn
        }
    }
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NeoTopBar("Reverse Transaction")
        
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Enter RRN (Retrieval Reference Number)",
                style = MaterialTheme.typography.titleMedium,
                color = IndigoBlue
            )
            
            // RRN Display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    rrn.ifEmpty { "Enter RRN" },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (rrn.isEmpty()) MutedLavender else IndigoBlue
                )
            }
            
            // NumPad for RRN input
            NumPad(
                onPress = { k ->
                    val digits = (rrn + k).take(12) // RRN is typically 12 digits
                    rrn = digits
                },
                onDelete = { if (rrn.isNotEmpty()) rrn = rrn.dropLast(1) },
                onClear = { rrn = "" },
                onOk = {
                    if (rrn.isNotEmpty()) {
                        onRrnEntered(rrn)
                    }
                }
            )
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onSelectFromHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("History", color = IndigoBlue)
                }
                
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = IndigoBlue)
                }
            }
        }
    }
}

