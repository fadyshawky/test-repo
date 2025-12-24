package com.neo.neopayplus.emv

import android.os.Handler
import android.os.Looper
import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.ErrorHandler
import com.neo.neopayplus.utils.LogUtil
import com.neo.neopayplus.processing.repository.TransactionRepository
import com.neo.neopayplus.data.TransactionJournal
import com.neo.neopayplus.config.PaymentConfig
import com.neo.neopayplus.api.PaymentApiService
import com.neo.neopayplus.db.TxnDb
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

/**
 * Service for handling reversal transactions from Compose UI
 */
class ReversalService {
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private val transactionRepository = TransactionRepository()
    
    private val _reversalState = MutableStateFlow<ReversalState>(ReversalState.Idle)
    val reversalState: StateFlow<ReversalState> = _reversalState.asStateFlow()
    
    /**
     * Process reversal transaction by transaction ID
     */
    fun processReversal(transactionId: String) {
        _reversalState.value = ReversalState.Processing("Looking up transaction...")
        
        try {
            // Find original transaction by transaction ID
            val originalTx = TransactionJournal.findTransactionById(transactionId)
            
            if (originalTx == null) {
                _reversalState.value = ReversalState.Error("Transaction not found for ID: $transactionId")
                LogUtil.e(Constant.TAG, "❌ No transaction found with ID: $transactionId")
                return
            }
            
            LogUtil.e(Constant.TAG, "=== PROCESSING REVERSAL ===")
            LogUtil.e(Constant.TAG, "  Transaction ID: $transactionId")
            LogUtil.e(Constant.TAG, "  RRN: ${originalTx.rrn}")
            LogUtil.e(Constant.TAG, "  Original Amount: ${originalTx.amount}")
            
            // Build reversal request
            val request = PaymentApiService.ReversalRequest().apply {
                terminalId = PaymentConfig.getTerminalId()
                merchantId = PaymentConfig.getMerchantId()
                this.transactionId = transactionId
                this.rrn = originalTx.rrn // Include RRN for backward compatibility
                amount = originalTx.amount
                currencyCode = originalTx.currencyCode ?: PaymentConfig.getCurrencyCode()
                reversalReason = "USER_REQUEST"
            }
            
            _reversalState.value = ReversalState.Processing("Sending reversal to backend...")
            
            // Send reversal using TransactionRepository
            transactionRepository.reverseTransaction(request, object : PaymentApiService.ReversalCallback {
                override fun onReversalComplete(response: PaymentApiService.ReversalResponse) {
                    mainHandler.post {
                        if (response.approved) {
                            LogUtil.e(Constant.TAG, "✓ Reversal Approved ✅")
                            
                            // Update original transaction status to REFUNDED
                            if (originalTx.transactionId != null) {
                                TransactionJournal.updateTransactionStatus(originalTx.transactionId, "REFUNDED")
                                LogUtil.e(Constant.TAG, "✓ Original transaction marked as REFUNDED: ${originalTx.transactionId}")
                            }
                            
                            // Save reversal transaction to journal
                            val reversal = TransactionJournal.TransactionRecord().apply {
                                this.transactionId = null // Will be auto-generated
                                this.rrn = request.rrn
                                amount = request.amount
                                currencyCode = request.currencyCode
                                transactionType = "20" // Reversal
                                this.responseCode = response.responseCode
                                status = "APPROVED"
                                isReversal = true
                                originalRrn = originalTx.rrn
                                
                                // Use original transaction's batch number for reversals
                                this.batchNumber = originalTx.batchNumber
                                
                                // Use current date/time
                                val dateFormat = SimpleDateFormat("yyMMdd", Locale.US)
                                val timeFormat = SimpleDateFormat("HHmmss", Locale.US)
                                val now = Date()
                                date = dateFormat.format(now)
                                time = timeFormat.format(now)
                            }
                            
                            TransactionJournal.saveTransaction(reversal)
                            
                            _reversalState.value = ReversalState.Completed(
                                approved = true,
                                rc = response.responseCode ?: "00",
                                msg = response.responseMessage ?: "Reversal Approved",
                                rrn = originalTx.rrn
                            )
                        } else {
                            LogUtil.e(Constant.TAG, "❌ Reversal Declined: ${response.responseCode}")
                            _reversalState.value = ReversalState.Completed(
                                approved = false,
                                rc = response.responseCode ?: "XX",
                                msg = response.responseMessage ?: "Reversal Declined",
                                rrn = null
                            )
                        }
                    }
                }
                
                override fun onReversalError(error: Throwable) {
                    mainHandler.post {
                        LogUtil.e(Constant.TAG, "❌ Reversal failed: ${error.message}")
                        
                        // Host down - queue reversal offline
                        queueReversalOffline(request)
                        
                        _reversalState.value = ReversalState.Error(
                            "Host unavailable - reversal queued offline"
                        )
                    }
                }
            })
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "processReversal", e)
            _reversalState.value = ReversalState.Error(e.message ?: "Failed to process reversal")
        }
    }
    
    /**
     * Queue reversal offline (when host is down)
     */
    private fun queueReversalOffline(request: PaymentApiService.ReversalRequest) {
        try {
            val db = TxnDb(com.neo.neopayplus.MyApplication.app)
            val reversalRecord = mutableMapOf<String, Any>().apply {
                put("rrn", request.rrn)
                put("amount_minor", request.amount.toLongOrNull() ?: 0L)
                put("currency", request.currencyCode)
                put("reason", request.reversalReason ?: "HOST_UNAVAILABLE")
                put("created_at", com.neo.neopayplus.utils.TimeSync.nowIso())
            }
            
            db.enqueueReversal(reversalRecord)
            LogUtil.e(Constant.TAG, "✓ Reversal queued offline ⏳")
            
        } catch (e: Exception) {
            ErrorHandler.logError(Constant.TAG, "queueReversalOffline", e)
        }
    }
    
    sealed class ReversalState {
        object Idle : ReversalState()
        data class Processing(val message: String) : ReversalState()
        data class Completed(val approved: Boolean, val rc: String, val msg: String, val rrn: String?) : ReversalState()
        data class Error(val message: String) : ReversalState()
    }
}

