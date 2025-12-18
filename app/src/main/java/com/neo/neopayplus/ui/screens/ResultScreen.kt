package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White

@Composable
fun ResultScreen(
    approved: Boolean,
    rc: String,
    msg: String,
    onPrint: (rrn: String) -> Unit,
    onDone: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            if (approved) "Approved" else "Declined",
            style = MaterialTheme.typography.titleLarge,
            color = IndigoBlue
        )
        
        Column {
            if (approved)
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = IndigoBlue, modifier = Modifier.size(96.dp))
            else
                Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(96.dp))
            
            Spacer(Modifier.height(12.dp))
            Text(msg, color = MutedLavender)
            if (!approved) Text("RC: $rc", color = MaterialTheme.colorScheme.error)
        }
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (approved) Button(onClick = { onPrint("123456789012") }, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Print Receipt", color = White)
            }
            OutlinedButton(onClick = onDone, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Text("Done")
            }
        }
    }
}
