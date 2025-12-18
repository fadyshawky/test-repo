package com.neo.neopayplus.data.payment.datasource

import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.emv.TransactionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

/**
 * Data source interface for EMV hardware operations.
 * 
 * This abstracts the Sunmi Pay SDK interactions,
 * allowing for easier testing and potential SDK replacements.
 */
interface EmvDataSource {
    
    /**
     * Result of EMV transaction processing.
     */
    data class EmvResult(
        val requiresOnlineAuth: Boolean,
        val emvData: Map<String, String>, // Field 55 data
        val cardPan: String?,
        val cardExpiry: String?,
        val cardholderName: String?,
        val offlineApproved: Boolean = false
    )
    
    /**
     * Process EMV transaction with hardware.
     * 
     * @param amount Transaction amount
     * @param transactionType Type of transaction
     * @param entryMode Card entry mode
     * @param stan System Trace Audit Number
     * @return Flow of EMV processing results
     */
    suspend fun processEmvTransaction(
        amount: BigDecimal,
        transactionType: TransactionType,
        entryMode: EntryMode,
        stan: Int
    ): Flow<EmvResult>
    
    /**
     * Get the transaction state flow for observing real-time transaction updates.
     * This allows the presentation layer to observe intermediate states (Processing, PinRequired, etc.)
     * without directly accessing EmvManager.
     * 
     * @return StateFlow of transaction state
     */
    fun getTransactionStateFlow(): StateFlow<TransactionState>
    
    /**
     * Start EMV transaction directly (for immediate card detection scenarios).
     * This is used when card is already detected and we need to start transaction immediately.
     * 
     * @param amountMinor12 Amount in minor units (12 digits)
     * @param stan System Trace Audit Number
     * @param cardType Card type (IC/NFC/Magnetic)
     */
    fun startTransaction(amountMinor12: String, stan: Int, cardType: Int)
    
    /**
     * Process issuer scripts received from backend.
     * 
     * @param issuerScripts Scripts to process
     * @return Success or failure
     */
    suspend fun processIssuerScripts(issuerScripts: List<String>): Boolean
}

