package com.neo.neopayplus.data.payment.datasource

import android.content.Context
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.emv.TransactionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Stub implementation of EmvDataSource.
 * 
 * NOTE: The main payment flow now uses EMVPaymentActivity with EMVHandler directly.
 * This stub exists to satisfy Hilt dependency injection for legacy code paths.
 * 
 * For actual EMV operations, use EMVHandler directly in your Activity/Fragment.
 */
@Singleton
class EmvDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : EmvDataSource {
    
    private val _transactionState = MutableStateFlow<TransactionState>(TransactionState.Idle)
    
    override fun getTransactionStateFlow(): StateFlow<TransactionState> {
        return _transactionState.asStateFlow()
    }
    
    override fun startTransaction(amountMinor12: String, stan: Int, cardType: Int) {
        // Stub - EMVPaymentActivity uses EMVHandler directly
        android.util.Log.w("EmvDataSourceImpl", 
            "startTransaction called on stub implementation. Use EMVPaymentActivity with EMVHandler instead.")
        _transactionState.value = TransactionState.Error(
            "Use EMVPaymentActivity for payment processing"
        )
    }
    
    override suspend fun processEmvTransaction(
        amount: BigDecimal,
        transactionType: TransactionType,
        entryMode: EntryMode,
        stan: Int
    ): Flow<EmvDataSource.EmvResult> = flow {
        // Stub - EMVPaymentActivity uses EMVHandler directly
        android.util.Log.w("EmvDataSourceImpl", 
            "processEmvTransaction called on stub implementation. Use EMVPaymentActivity with EMVHandler instead.")
        throw UnsupportedOperationException(
            "Use EMVPaymentActivity with EMVHandler for payment processing"
        )
    }
    
    override suspend fun processIssuerScripts(issuerScripts: List<String>): Boolean {
        // Stub - not implemented
        android.util.Log.w("EmvDataSourceImpl", 
            "processIssuerScripts called on stub implementation.")
        return false
    }
}
