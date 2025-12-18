package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.neo.neopayplus.ui.components.NeoTopBar
import com.neo.neopayplus.ui.components.SquareActionButton
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue

@Composable
fun HomeScreen(
    onCardPayment: () -> Unit,
    onRefund: () -> Unit,
    onVoid: () -> Unit,
    onSettlement: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        NeoTopBar("NeoPay POS")
        
        // 3xN grid
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                SquareActionButton(modifier = Modifier.weight(1f), title = "Card Payment", icon = {
                    Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = IndigoBlue)
                }, onClick = onCardPayment)
                SquareActionButton(modifier = Modifier.weight(1f), title = "Refund", icon = {
                    Icon(Icons.Outlined.Undo, contentDescription = null, tint = IndigoBlue)
                }, onClick = onRefund)
                SquareActionButton(modifier = Modifier.weight(1f), title = "Void", icon = {
                    Icon(Icons.Outlined.Payments, contentDescription = null, tint = IndigoBlue)
                }, onClick = onVoid)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                SquareActionButton(modifier = Modifier.weight(1f), title = "Settlement", icon = {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = IndigoBlue)
                }, onClick = onSettlement)
                SquareActionButton(modifier = Modifier.weight(1f), title = "History", icon = {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = IndigoBlue)
                }, onClick = onHistory)
                SquareActionButton(modifier = Modifier.weight(1f), title = "Settings", icon = {
                    Icon(Icons.Outlined.Settings, contentDescription = null, tint = IndigoBlue)
                }, onClick = onSettings)
            }
        }
    }
}
