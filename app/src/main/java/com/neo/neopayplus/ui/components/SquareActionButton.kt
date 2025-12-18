package com.neo.neopayplus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.ui.theme.Card
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.MutedLavender30

@Composable
fun SquareActionButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: @Composable () -> Unit = {
        Icon(Icons.Outlined.CreditCard, contentDescription = null, tint = IndigoBlue, modifier = Modifier.size(36.dp))
    },
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .shadow(6.dp, RoundedCornerShape(12.dp), clip = false)
            .background(Card, RoundedCornerShape(12.dp))
            .border(1.dp, MutedLavender30, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon()
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MutedLavender,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

