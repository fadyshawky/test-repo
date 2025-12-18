package com.neo.neopayplus.data.payment.datasource

import com.neo.neopayplus.api.PaymentApiFactory
import com.neo.neopayplus.api.PaymentApiService
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.utils.TimeUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PaymentApiDataSource that wraps PaymentApiService.
 * 
 * This class handles all backend API communication for payment authorization.
 */
@Singleton
class PaymentApiDataSourceImpl @Inject constructor() : PaymentApiDataSource {
    
    private val apiService: PaymentApiService
        get() = PaymentApiFactory.getInstance()
    
    override suspend fun authorizeTransaction(
        amount: BigDecimal,
        transactionType: TransactionType,
        emvData: Map<String, String>,
        stan: Int
    ): PaymentApiDataSource.AuthorizationResult = withContext(Dispatchers.IO) {
        val request = PaymentApiService.AuthorizationRequest().apply {
            this.amount = amount.multiply(BigDecimal("100")).toLong().toString()
            this.currencyCode = "818" // EGP
            this.transactionType = when (transactionType) {
                TransactionType.PURCHASE -> "00"
                TransactionType.REFUND -> "20"
                TransactionType.REVERSAL -> "40"
                TransactionType.VOID -> "40"
            }
            this.field55 = emvData["field55"] ?: ""
            
            // Get current date/time
            val dateFormat = SimpleDateFormat("yyMMddHHmmss", Locale.US)
            val dateTime = dateFormat.format(Date())
            this.date = dateTime.substring(0, 6) // YYMMDD
            this.time = dateTime.substring(6, 12) // HHMMSS
        }
        
        // Make API call using callback (convert to suspend function)
        suspendCancellableCoroutine { continuation ->
            apiService.authorizeTransaction(request, object : PaymentApiService.AuthorizationCallback {
                override fun onAuthorizationComplete(response: PaymentApiService.AuthorizationResponse) {
                    val result = PaymentApiDataSource.AuthorizationResult(
                        approved = response.approved,
                        rrn = response.rrn,
                        authCode = response.authCode,
                        responseCode = response.responseCode ?: "XX",
                        responseMessage = response.message ?: "Unknown",
                        issuerScripts = response.responseTags?.zip(response.responseValues ?: emptyArray())
                            ?.map { (tag, value) -> "$tag$value" }
                            ?: emptyList()
                    )
                    continuation.resume(result) {}
                }
                
                override fun onAuthorizationError(error: Throwable) {
                    val result = PaymentApiDataSource.AuthorizationResult(
                        approved = false,
                        rrn = null,
                        authCode = null,
                        responseCode = "XX",
                        responseMessage = error.message ?: "Authorization failed",
                        issuerScripts = emptyList()
                    )
                    continuation.resume(result) {}
                }
            })
        }
    }
    
    override suspend fun reverseTransaction(
        rrn: String,
        originalAmount: BigDecimal,
        originalStan: Int
    ): PaymentApiDataSource.ReversalResult = withContext(Dispatchers.IO) {
        val request = PaymentApiService.ReversalRequest().apply {
            this.rrn = rrn
            this.amount = originalAmount.multiply(BigDecimal("100")).toLong().toString()
            this.currencyCode = "818" // EGP
            this.reversalReason = "USER_REQUEST"
            // terminalId and merchantId should be set from config, but for now leave as default
        }
        
        suspendCancellableCoroutine { continuation ->
            apiService.reverseTransaction(request, object : PaymentApiService.ReversalCallback {
                override fun onReversalComplete(response: PaymentApiService.ReversalResponse) {
                    val result = PaymentApiDataSource.ReversalResult(
                        success = response.approved,
                        rrn = rrn, // Use the rrn from request since ReversalResponse doesn't have it
                        responseCode = response.responseCode ?: "XX",
                        responseMessage = response.responseMessage ?: "Reversal completed"
                    )
                    continuation.resume(result) {}
                }
                
                override fun onReversalError(error: Throwable) {
                    val result = PaymentApiDataSource.ReversalResult(
                        success = false,
                        rrn = null,
                        responseCode = "XX",
                        responseMessage = error.message ?: "Reversal failed"
                    )
                    continuation.resume(result) {}
                }
            })
        }
    }
}
