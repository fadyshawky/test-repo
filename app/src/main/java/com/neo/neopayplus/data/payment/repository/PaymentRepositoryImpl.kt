package com.neo.neopayplus.data.payment.repository

import com.neo.neopayplus.data.payment.datasource.EmvDataSource
import com.neo.neopayplus.data.payment.datasource.PaymentApiDataSource
import com.neo.neopayplus.data.payment.datasource.TransactionLocalDataSource
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionStatus
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.domain.payment.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PaymentRepository in the Data layer.
 * 
 * This class handles:
 * - Communication with EMV hardware (Sunmi Pay SDK)
 * - Backend API calls for authorization
 * - Local database operations
 * - Data transformation between domain and data models
 * 
 * This is where Android-specific code and SDK interactions happen,
 * keeping the Domain layer clean and testable.
 */
@Singleton
class PaymentRepositoryImpl @Inject constructor(
    private val emvDataSource: EmvDataSource,
    private val apiDataSource: PaymentApiDataSource,
    private val localDataSource: TransactionLocalDataSource
) : PaymentRepository {
    
    override suspend fun processPayment(
        amount: BigDecimal,
        transactionType: TransactionType,
        entryMode: EntryMode
    ): Flow<Transaction> = flow {
        // Step 1: Initialize EMV transaction
        val stan = localDataSource.getNextStan()
        
        // Step 2: Process EMV transaction with hardware
        emvDataSource.processEmvTransaction(
            amount = amount,
            transactionType = transactionType,
            entryMode = entryMode,
            stan = stan
        ).collect { emvResult ->
            // Step 3: If online authorization required, call backend
            if (emvResult.requiresOnlineAuth) {
                val authResult = apiDataSource.authorizeTransaction(
                    amount = amount,
                    transactionType = transactionType,
                    emvData = emvResult.emvData,
                    stan = stan
                )
                
                // Step 4: Process issuer scripts if provided
                if (authResult.issuerScripts.isNotEmpty()) {
                    emvDataSource.processIssuerScripts(authResult.issuerScripts)
                }
                
                // Step 5: Build final transaction result
                val transaction = Transaction(
                    amount = amount,
                    entryMode = entryMode,
                    type = transactionType,
                    status = if (authResult.approved) TransactionStatus.APPROVED else TransactionStatus.DECLINED,
                    rrn = authResult.rrn,
                    stan = stan,
                    authCode = authResult.authCode,
                    responseCode = authResult.responseCode,
                    responseMessage = authResult.responseMessage,
                    cardPan = emvResult.cardPan,
                    cardExpiry = emvResult.cardExpiry,
                    cardholderName = emvResult.cardholderName
                )
                
                // Step 6: Save to local database
                localDataSource.saveTransaction(transaction)
                
                emit(transaction)
            } else {
                // Offline approved transaction
                val transaction = Transaction(
                    amount = amount,
                    entryMode = entryMode,
                    type = transactionType,
                    status = TransactionStatus.APPROVED,
                    stan = stan,
                    cardPan = emvResult.cardPan,
                    cardExpiry = emvResult.cardExpiry,
                    cardholderName = emvResult.cardholderName
                )
                
                localDataSource.saveTransaction(transaction)
                emit(transaction)
            }
        }
    }
    
    override suspend fun reverseTransaction(rrn: String): Flow<Transaction> = flow {
        // Get original transaction
        val originalTransaction = localDataSource.getTransactionByRrn(rrn)
            ?: throw IllegalArgumentException("Transaction not found: $rrn")
        
        // Call backend for reversal
        val reversalResult = apiDataSource.reverseTransaction(
            rrn = rrn,
            originalAmount = originalTransaction.amount,
            originalStan = originalTransaction.stan ?: 0
        )
        
        val reversalTransaction = Transaction(
            amount = originalTransaction.amount,
            entryMode = originalTransaction.entryMode,
            type = TransactionType.REVERSAL,
            status = if (reversalResult.success) TransactionStatus.APPROVED else TransactionStatus.DECLINED,
            rrn = reversalResult.rrn,
            responseCode = reversalResult.responseCode,
            responseMessage = reversalResult.responseMessage
        )
        
        localDataSource.saveTransaction(reversalTransaction)
        emit(reversalTransaction)
    }
    
    override suspend fun getTransactionByRrn(rrn: String): Transaction? {
        return localDataSource.getTransactionByRrn(rrn)
    }
    
    override suspend fun saveTransaction(transaction: Transaction) {
        localDataSource.saveTransaction(transaction)
    }
    
    override suspend fun getTransactionHistory(limit: Int): List<Transaction> {
        return localDataSource.getTransactionHistory(limit)
    }
}

