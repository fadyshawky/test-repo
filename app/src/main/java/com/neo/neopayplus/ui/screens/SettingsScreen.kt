package com.neo.neopayplus.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Settings", style = MaterialTheme.typography.titleLarge, color = IndigoBlue)
        
        // Device info, Printer test, Versions, Network
        OutlinedButton(onClick = { /* test print */ }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Text("Printer Test")
        }
        
        // Debug: ISO8583 Logs
        OutlinedButton(
            onClick = {
                val intent = Intent(context, com.neo.neopayplus.debug.DebugActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("View ISO8583 Logs")
        }
    }
}

