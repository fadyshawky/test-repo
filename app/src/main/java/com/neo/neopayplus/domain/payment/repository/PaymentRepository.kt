package com.neo.neopayplus.domain.payment.repository

import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for payment operations.
 * This is part of the Domain layer and defines the contract for data operations.
 * 
 * Implementation should be in the Data layer to follow Clean Architecture.
 */
interface PaymentRepository {
    
    /**
     * Process a payment transaction.
     * 
     * @param amount Transaction amount
     * @param transactionType Type of transaction (purchase/refund)
     * @param entryMode Card entry mode (ICC/NFC/Magnetic)
     * @return Flow of transaction result
     */
    suspend fun processPayment(
        amount: java.math.BigDecimal,
        transactionType: TransactionType,
        entryMode: EntryMode
    ): Flow<Transaction>
    
    /**
     * Reverse a transaction by RRN.
     * 
     * @param rrn Retrieval Reference Number
     * @return Flow of transaction result
     */
    suspend fun reverseTransaction(rrn: String): Flow<Transaction>
    
    /**
     * Get transaction by RRN.
     * 
     * @param rrn Retrieval Reference Number
     * @return Transaction if found, null otherwise
     */
    suspend fun getTransactionByRrn(rrn: String): Transaction?
    
    /**
     * Save transaction to local database.
     * 
     * @param transaction Transaction to save
     */
    suspend fun saveTransaction(transaction: Transaction)
    
    /**
     * Get transaction history.
     * 
     * @param limit Maximum number of transactions to return
     * @return List of transactions
     */
    suspend fun getTransactionHistory(limit: Int = 100): List<Transaction>
}

