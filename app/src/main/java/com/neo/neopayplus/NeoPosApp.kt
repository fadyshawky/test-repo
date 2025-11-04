package com.neo.neopayplus

import android.app.Application

/**
 * Application class for Compose UI
 * Note: Existing MyApplication.java handles PaySDK initialization
 * This is kept minimal for Compose integration
 */
class NeoPosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // PaySDK initialization is handled by MyApplication.java
    }
}

