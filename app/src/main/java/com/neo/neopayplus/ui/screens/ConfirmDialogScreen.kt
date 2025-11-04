package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmDialogScreen(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
) {
    Scaffold { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            AlertDialog(
                onDismissRequest = onCancel,
                title = { Text(title) },
                text = { Text(message) },
                confirmButton = { Button(onClick = onConfirm) { Text(confirmText) } },
                dismissButton = { OutlinedButton(onClick = onCancel) { Text("Cancel") } }
            )
        }
    }
}

