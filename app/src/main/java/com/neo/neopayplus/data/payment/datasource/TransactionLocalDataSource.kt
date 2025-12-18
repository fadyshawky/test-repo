package com.neo.neopayplus.data.payment.datasource

import com.neo.neopayplus.domain.payment.model.Transaction

/**
 * Data source interface for local transaction storage.
 * 
 * This abstracts database operations, allowing for easy testing
 * and potential database replacements.
 */
interface TransactionLocalDataSource {
    
    /**
     * Get next System Trace Audit Number (STAN).
     * 
     * @return Next STAN value
     */
    suspend fun getNextStan(): Int
    
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
    suspend fun getTransactionHistory(limit: Int): List<Transaction>
}

