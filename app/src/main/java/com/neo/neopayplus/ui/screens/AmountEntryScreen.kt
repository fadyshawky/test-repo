package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.ui.components.NumPad

@Composable
fun AmountEntryScreen(
    title: String,
    onConfirm: (Long) -> Unit,
    onCancel: () -> Unit,
) {
    var input by remember { mutableStateOf("0.00") }
    
    fun push(key: String) {
        val raw = input.replace(".", "").padStart(3, '0')
        val digits = raw.filter { it.isDigit() }
        val next = when (key) {
            "." -> digits // ignore, fixed cents
            else -> (digits + key).takeLast(12)
        }
        input = (next.toLongOrNull() ?: 0L).let { cents ->
            val s = cents.toString().padStart(3, '0')
            val dollars = s.dropLast(2)
            val centsStr = s.takeLast(2)
            "$dollars.$centsStr"
        }
    }
    
    fun clear() { input = "0.00" }
    
    Scaffold { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = input,
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                )
            }
            NumPad(
                modifier = Modifier.fillMaxWidth(),
                onPress = { push(it) },
                onClear = { clear() }
            )
            Row(
                modifier = Modifier.fillMaxWidth(), 
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f).height(56.dp), 
                    onClick = onCancel
                ) { 
                    Text("Cancel") 
                }
                Button(
                    modifier = Modifier.weight(1f).height(56.dp), 
                    onClick = {
                        val cents = (input.replace(".", "").toLongOrNull() ?: 0L)
                        onConfirm(cents)
                    }
                ) { 
                    Text("Confirm") 
                }
            }
        }
    }
}

