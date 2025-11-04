package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettlementConfirmScreen(onStart: () -> Unit, onBack: () -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Settlement") }) }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp), 
            verticalArrangement = Arrangement.Center, 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Send batch for settlement?")
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onBack) { Text("Cancel") }
                Button(onClick = onStart) { Text("Start") }
            }
        }
    }
}

@Composable
fun SettlementProcessingScreen(onDone: (ok: Boolean) -> Unit) {
    LaunchedEffect(Unit) { 
        // TODO: Implement settlement upload
        kotlinx.coroutines.delay(2000)
        onDone(true) 
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Uploading batch…")
        }
    }
}

@Composable
fun SettlementResultScreen(onHome: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Settlement Complete", style = MaterialTheme.typography.headlineLarge)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onHome) { Text("Home") }
        }
    }
}

