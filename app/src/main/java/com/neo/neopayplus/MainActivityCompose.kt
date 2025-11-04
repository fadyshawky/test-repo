package com.neo.neopayplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.neo.neopayplus.navigation.AppNavHost
import com.neo.neopayplus.ui.theme.NeoTheme

/**
 * Main Activity using Jetpack Compose
 * This provides a modern UI layer while using existing Java services
 */
class MainActivityCompose : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure PaySDK is connected before showing UI
        if (!MyApplication.app.isConnectPaySDK()) {
            MyApplication.app.bindPaySDKService()
        }
        
        setContent {
            NeoApp()
        }
    }
}

@Composable
fun NeoApp() {
    NeoTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AppNavHost()
        }
    }
}

