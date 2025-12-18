package com.neo.neopayplus.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.neo.neopayplus.ui.screens.HistoryScreen
import com.neo.neopayplus.ui.theme.Background
import com.neo.neopayplus.ui.theme.NeoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * History Activity - Displays transaction history.
 */
@AndroidEntryPoint
class HistoryActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            NeoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    HistoryScreen()
                }
            }
        }
    }
}

