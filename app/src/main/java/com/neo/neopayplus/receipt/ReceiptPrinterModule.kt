package com.neo.neopayplus.receipt

import com.neo.neopayplus.MyApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing ReceiptPrinterService
 */
@Module
@InstallIn(SingletonComponent::class)
object ReceiptPrinterModule {
    
    @Provides
    @Singleton
    fun provideReceiptPrinterService(): ReceiptPrinterService {
        return ReceiptPrinterService(MyApplication.app.sunmiPrinterService)
    }
}
