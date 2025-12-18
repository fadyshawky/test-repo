package com.neo.neopayplus.data.payment.datasource

import android.content.Context
import com.neo.neopayplus.db.TxnDb
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of TransactionLocalDataSource that wraps TxnDb.
 * 
 * This class handles local database operations for transaction storage.
 */
@Singleton
class TransactionLocalDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TransactionLocalDataSource {
    
    private val db: TxnDb
        get() = TxnDb(context)
    
    override suspend fun getNextStan(): Int = withContext(Dispatchers.IO) {
        db.nextStan()
    }
    
    override suspend fun getTransactionByRrn(rrn: String): Transaction? = withContext(Dispatchers.IO) {
        // TxnDb doesn't have a direct getByRrn method, so we'll need to query
        // For now, return null - this can be implemented when needed
        null
    }
    
    override suspend fun saveTransaction(transaction: Transaction): Unit = withContext(Dispatchers.IO) {
        // Convert domain Transaction to TxnDb format
        val amountMinor = transaction.amount.multiply(BigDecimal("100")).toInt()
        val entryMode = when (transaction.entryMode) {
            EntryMode.ICC -> "ICC"
            EntryMode.NFC -> "NFC"
            EntryMode.MAGNETIC -> "MSR"
        }
        
        val dateFormat = SimpleDateFormat("yyMMddHHmmss", Locale.US)
        val dateTime = dateFormat.format(transaction.timestamp)
        
        // Save to journal using TxnDb.insertJournal
        val journalMap = mutableMapOf<String, Any>(
            "stan" to (transaction.stan ?: 0),
            "rrn" to (transaction.rrn ?: ""),
            "amount_minor" to amountMinor,
            "currency" to transaction.currency,
            "pan_masked" to (transaction.cardPan?.let { maskPan(it) } ?: ""),
            "entry_mode" to entryMode,
            "resp_code" to (transaction.responseCode ?: ""),
            "auth_code" to (transaction.authCode ?: ""),
            "datetime" to dateTime
        )
        
        db.insertJournal(journalMap)
    }
    
    override suspend fun getTransactionHistory(limit: Int): List<Transaction> = withContext(Dispatchers.IO) {
        // TxnDb doesn't have a direct history method, so we'll need to query
        // For now, return empty list - this can be implemented when needed
        emptyList()
    }
    
    private fun maskPan(pan: String): String {
        if (pan.length < 8) return "****"
        return "${pan.take(6)}****${pan.takeLast(4)}"
    }
    
    private fun buildJsonPayload(transaction: Transaction): String {
        // Build JSON payload for storage
        return """
        {
            "type": "${transaction.type}",
            "status": "${transaction.status}",
            "entryMode": "${transaction.entryMode}",
            "timestamp": "${transaction.timestamp.time}"
        }
        """.trimIndent()
    }
}

