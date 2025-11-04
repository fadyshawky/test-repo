package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProcessingScreen(
    amountCents: Long,
    onDone: (approved: Boolean) -> Unit
) {
    // TODO: Connect to ProcessEmvTransactionUseCase
    LaunchedEffect(Unit) {
        // TODO: Perform full EMV transaction flow
        // For now, simulate processing
        kotlinx.coroutines.delay(2000)
        onDone(true) // or false
    }
    
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Authorizing…")
        }
    }
}

