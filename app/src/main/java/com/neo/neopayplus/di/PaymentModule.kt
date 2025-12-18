package com.neo.neopayplus.di

import com.neo.neopayplus.data.payment.datasource.*
import com.neo.neopayplus.data.payment.repository.PaymentRepositoryImpl
import com.neo.neopayplus.domain.payment.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for payment-related dependencies.
 * 
 * This module provides bindings for:
 * - Repository implementations
 * - Data source implementations
 * 
 * Use cases are auto-provided by Hilt via their @Inject constructor.
 * 
 * Following Clean Architecture, dependencies flow:
 * Presentation -> Domain -> Data
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentModule {
    
    /**
     * Bind PaymentRepository interface to implementation.
     */
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository
    
    /**
     * Bind EmvDataSource interface to implementation.
     * 
     * Uses EMVHandler.java with the simplified EMVListenerV2.Stub approach.
     */
    @Binds
    @Singleton
    abstract fun bindEmvDataSource(
        emvDataSourceImpl: EmvDataSourceImpl
    ): EmvDataSource
    
    /**
     * Bind PaymentApiDataSource interface to implementation.
     */
    @Binds
    @Singleton
    abstract fun bindPaymentApiDataSource(
        paymentApiDataSourceImpl: PaymentApiDataSourceImpl
    ): PaymentApiDataSource
    
    /**
     * Bind TransactionLocalDataSource interface to implementation.
     */
    @Binds
    @Singleton
    abstract fun bindTransactionLocalDataSource(
        transactionLocalDataSourceImpl: TransactionLocalDataSourceImpl
    ): TransactionLocalDataSource
    
    // Use cases with @Inject constructor don't need @Provides
    // Hilt automatically creates them from their @Inject constructor
}

