package com.neo.neopayplus.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumPad(
    modifier: Modifier = Modifier,
    onPress: (String) -> Unit,
    onClear: () -> Unit,
) {
    Column(modifier) {
        listOf(
            listOf("1","2","3"),
            listOf("4","5","6"),
            listOf("7","8","9"),
            listOf("C","0",".")
        ).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    Button(
                        modifier = Modifier.weight(1f).height(64.dp),
                        onClick = {
                            when (key) {
                                "C" -> onClear()
                                else -> onPress(key)
                            }
                        }
                    ) { Text(key, fontSize = 20.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

