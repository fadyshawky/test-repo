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
fun CardDetectScreen(
    onCardRead: (requiresPin: Boolean, cardNo: String?) -> Unit,
    onCancel: () -> Unit
) {
    val emvBridge: EmvBridge = remember { EmvBridgeImpl() }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    LaunchedEffect(Unit) {
        emvBridge.detectCard(
            onResult = { requiresPin, cardNo ->
                // Pass card number to parent
                onCardRead(requiresPin, cardNo)
            },
            onError = { error ->
                errorMessage = error.message
            }
        )
    }
    
    DisposableEffect(Unit) {
        onDispose {
            (emvBridge as? EmvBridgeImpl)?.cancelCardDetection()
        }
    }
    
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text("Tap / Insert / Swipe card")
            errorMessage?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(32.dp))
            OutlinedButton(onClick = onCancel) { Text("Cancel") }
        }
    }
}

