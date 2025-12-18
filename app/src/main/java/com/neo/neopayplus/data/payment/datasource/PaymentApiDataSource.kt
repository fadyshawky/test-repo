package com.neo.neopayplus.data.payment.datasource

import com.neo.neopayplus.domain.payment.model.TransactionType
import java.math.BigDecimal

/**
 * Data source interface for payment API operations.
 * 
 * This abstracts backend API calls for authorization and reversal.
 * All network communication happens here, keeping it separate from business logic.
 */
interface PaymentApiDataSource {
    
    /**
     * Result of authorization request.
     */
    data class AuthorizationResult(
        val approved: Boolean,
        val rrn: String?,
        val authCode: String?,
        val responseCode: String,
        val responseMessage: String,
        val issuerScripts: List<String> = emptyList()
    )
    
    /**
     * Result of reversal request.
     */
    data class ReversalResult(
        val success: Boolean,
        val rrn: String?,
        val responseCode: String,
        val responseMessage: String
    )
    
    /**
     * Authorize transaction with backend.
     * 
     * @param amount Transaction amount
     * @param transactionType Type of transaction
     * @param emvData EMV data (Field 55)
     * @param stan System Trace Audit Number
     * @return Authorization result
     */
    suspend fun authorizeTransaction(
        amount: BigDecimal,
        transactionType: TransactionType,
        emvData: Map<String, String>,
        stan: Int
    ): AuthorizationResult
    
    /**
     * Reverse transaction with backend.
     * 
     * @param rrn Retrieval Reference Number
     * @param originalAmount Original transaction amount
     * @param originalStan Original transaction STAN
     * @return Reversal result
     */
    suspend fun reverseTransaction(
        rrn: String,
        originalAmount: BigDecimal,
        originalStan: Int
    ): ReversalResult
}

