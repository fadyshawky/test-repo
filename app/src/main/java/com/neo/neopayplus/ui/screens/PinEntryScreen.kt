package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.emv.EmvBridge
import com.neo.neopayplus.emv.EmvBridgeImpl

@Composable
fun PinEntryScreen(
    cardNo: String,
    pinType: Int,
    onPinEntered: () -> Unit,
    onCancel: () -> Unit,
) {
    val emvBridge: EmvBridge = remember { EmvBridgeImpl() }
    
    LaunchedEffect(Unit) {
        emvBridge.requestPin(
            cardNo = cardNo,
            pinType = pinType,
            onDone = { onPinEntered() },
            onCancel = { onCancel() }
        )
    }
    
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Enter PIN on the keypad")
            Spacer(Modifier.height(16.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel) { Text("Cancel") }
                // Note: "Continue" button is for testing - actual PIN entry is via hardware keypad
            }
        }
    }
}

