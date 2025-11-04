@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.ui.theme.NeoColors

data class HomeAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    onCardPayment: () -> Unit,
    onRefund: () -> Unit,
    onVoid: () -> Unit,
    onSettlement: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = LocalHapticFeedback.current
    
    val actions = listOf(
        HomeAction(
            id = "pay",
            title = "Card Payment",
            icon = Icons.Filled.CreditCard,
            onClick = onCardPayment
        ),
        HomeAction(
            id = "refund",
            title = "Refund",
            icon = Icons.Filled.Refresh,
            onClick = onRefund
        ),
        HomeAction(
            id = "void",
            title = "Void",
            icon = Icons.Filled.Cancel,
            onClick = onVoid
        ),
        HomeAction(
            id = "settlement",
            title = "Settlement",
            icon = Icons.Filled.Receipt,
            onClick = onSettlement
        ),
        HomeAction(
            id = "history",
            title = "Transactions",
            icon = Icons.Filled.History,
            onClick = onHistory
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "NeoPay+", 
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = NeoColors.TextPrimary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NeoColors.Background
                )
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 20.dp, 
                end = 20.dp,
                top = padding.calculateTopPadding() + 20.dp,
                bottom = padding.calculateBottomPadding() + 20.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = modifier
                .fillMaxSize()
                .background(NeoColors.Background)
        ) {
            items(actions) { action ->
                NeoMenuButton(
                    icon = action.icon,
                    label = action.title,
                    onClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        action.onClick()
                    }
                )
            }
        }
    }
}

@Composable
private fun NeoMenuButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Perfect square
            .clip(shape)
            .background(NeoColors.CardSurface)
            .border(
                width = 1.dp,
                color = NeoColors.MutedLavender30,
                shape = shape
            )
            .clickable(onClick = onClick)
            .semantics { contentDescription = label }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeoColors.MutedLavender,
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = label,
                color = NeoColors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
