package com.neo.neopayplus.di

import android.content.Context
import com.neo.neopayplus.MyApplication
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for EMV SDK dependencies.
 * 
 * Provides SDK service instances from TransactionManager.
 * These are initialized when PaySDK connects.
 */
@Module
@InstallIn(SingletonComponent::class)
object EmvModule {
    
    /**
     * Provides ReadCardOptV2 service.
     * 
     * @return ReadCardOptV2 instance from TransactionManager, or null if not connected
     */
    @Provides
    @Singleton
    fun provideReadCardOptV2(@ApplicationContext context: Context): ReadCardOptV2? {
        return MyApplication.app.readCardOptV2
    }
    
    /**
     * Provides EMVOptV2 service.
     * 
     * @return EMVOptV2 instance from TransactionManager, or null if not connected
     */
    @Provides
    @Singleton
    fun provideEmvOptV2(@ApplicationContext context: Context): EMVOptV2? {
        return MyApplication.app.emvOptV2
    }
    
    /**
     * Provides PinPadOptV2 service.
     * 
     * @return PinPadOptV2 instance from TransactionManager, or null if not connected
     */
    @Provides
    @Singleton
    fun providePinPadOptV2(@ApplicationContext context: Context): PinPadOptV2? {
        return MyApplication.app.pinPadOptV2
    }
}

