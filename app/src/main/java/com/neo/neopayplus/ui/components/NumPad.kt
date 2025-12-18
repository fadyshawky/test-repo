package com.neo.neopayplus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neo.neopayplus.ui.theme.Card
import com.neo.neopayplus.ui.theme.IndigoBlue
import com.neo.neopayplus.ui.theme.MutedLavender
import com.neo.neopayplus.ui.theme.White

@Composable
fun NumPad(
    onPress: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onOk: () -> Unit
) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    Button(
                        onClick = {
                            when (key) {
                                "C" -> onClear()
                                "⌫" -> onDelete()
                                else -> onPress(key)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (key) {
                                "C" -> MutedLavender.copy(alpha = 0.3f)
                                "⌫" -> MutedLavender.copy(alpha = 0.3f)
                                else -> Card
                            }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(64.dp)
                    ) {
                        Text(
                            text = key,
                            color = IndigoBlue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Continue button
        Button(
            onClick = onOk,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                text = "Continue",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
