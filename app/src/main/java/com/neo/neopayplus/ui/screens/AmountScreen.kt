package com.neo.neopayplus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.domain.payment.PaymentState
import com.neo.neopayplus.ui.components.NeoTopBar
import com.neo.neopayplus.ui.components.NumPad
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun AmountScreen(
    type: String,
    state: PaymentState,
    onAmountConfirm: (BigDecimal) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Top section with header and amount display
        Column(
            Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // NeoTopBar("${type.replaceFirstChar { it.uppercase() }} — Amount")

            Spacer(Modifier.weight(1f))

            // Amount display centered
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EGP",
                    style = MaterialTheme.typography.titleMedium,
                    color = MutedLavender
                )
                Text(
                    text = if (text.isBlank()) "0.00" else {
                        val cents = text.toBigDecimalOrNull() ?: BigDecimal.ZERO
                        cents.movePointLeft(2).setScale(2, RoundingMode.HALF_UP).toString()
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 56.sp
                    ),
                    color = IndigoBlue,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.weight(1f))
        }

        // NumPad attached to bottom
        Surface(
            Modifier.fillMaxWidth(),
            color = Background,
            tonalElevation = 2.dp
        ) {
            Column(
                Modifier.padding(16.dp)
            ) {
                NumPad(
                    onPress = { k ->
                        val digits = (text + k).take(9)
                        text = digits
                    },
                    onDelete = { text = text.dropLast(1) },
                    onClear = { text = "" },
                    onOk = {
                        val amount = if (text.isBlank()) BigDecimal.ZERO
                        else {
                            try {
                                BigDecimal(text).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
                            } catch (e: Exception) {
                                BigDecimal.ZERO
                            }
                        }
                        onAmountConfirm(amount)
                    }
                )
            }
        }
    }
}
