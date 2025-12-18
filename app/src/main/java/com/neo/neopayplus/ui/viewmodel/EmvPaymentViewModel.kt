package com.neo.neopayplus.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neo.neopayplus.db.TxnDb
import com.neo.neopayplus.emv.EmvHandlerFactory
import com.neo.neopayplus.emv.TransactionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject

/**
 * ViewModel for EMVPaymentActivity.
 * 
 * Follows MVVM pattern - separates business logic from UI.
 * Handles EMV transaction flow, state management, and business operations.
 */
@HiltViewModel
class EmvPaymentViewModel @Inject constructor(
    application: Application,
    private val emvHandlerFactory: EmvHandlerFactory
    ) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "EmvPaymentViewModel"
    }

    // UI State
    // Kotlin data class automatically generates getters for Java interop
    data class UiState(
        val isProcessing: Boolean = false,
        val statusMessage: String = "",
        val amount: BigDecimal? = null,
        val amountMinor12: String = "",
        val stan: Int = 0,
        val transactionType: String = "purchase",
        val cardType: Int = 0,
        val currentPan: String? = null,
        val isManualPinEntry: Boolean = false,
        val manualPinBlock: ByteArray? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    
    // LiveData for Java compatibility
    private val _uiStateLiveData = MutableLiveData<UiState>()
    val uiStateLiveData: LiveData<UiState> = _uiStateLiveData
    
    init {
        // Sync StateFlow to LiveData for Java compatibility
        viewModelScope.launch {
            _uiState.collect { state ->
                _uiStateLiveData.postValue(state)
            }
        }
    }

    // Transaction state
    private var currentPinType: Int = 0

    /**
     * Set transaction amount
     */
    fun setAmount(amount: BigDecimal) {
        updateState { it.copy(
            amount = amount,
            amountMinor12 = convertToMinorUnits(amount)
        ) }
    }
    
    /**
     * Helper to update state
     */
    private fun updateState(update: (UiState) -> UiState) {
        _uiState.value = update(_uiState.value)
    }

    /**
     * Set transaction type
     */
    fun setTransactionType(type: String) {
        updateState { it.copy(transactionType = type) }
    }

    /**
     * Start payment flow
     */
    fun startPayment() {
        viewModelScope.launch {
            try {
                // Get STAN
                val stan = withContext(Dispatchers.IO) {
                    TxnDb(getApplication()).nextStan()
                }

                val amount = _uiState.value.amount
                if (amount == null) {
                    Log.e(TAG, "Amount not set")
                    return@launch
                }

                val amountMinor12 = convertToMinorUnits(amount)

                updateState { it.copy(
                    isProcessing = true,
                    stan = stan,
                    amountMinor12 = amountMinor12,
                    statusMessage = "Initializing...",
                    manualPinBlock = null,
                    isManualPinEntry = false
                ) }

                Log.e(TAG, "=== Starting Payment ===")
                Log.e(TAG, "  Type: ${_uiState.value.transactionType}")
                Log.e(TAG, "  Amount: $amount")
                Log.e(TAG, "  STAN: $stan")
                Log.e(TAG, "  Amount (minor): $amountMinor12")

                startCardDetection()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting payment: ${e.message}", e)
                updateState { it.copy(
                    statusMessage = "Error: ${e.message}",
                    isProcessing = false
                ) }
            }
        }
    }

    /**
     * Start card detection
     */
    private fun startCardDetection() {
        viewModelScope.launch {
            // Ensure PaySDK is ready
            if (!TransactionManager.isReady()) {
                Log.e(TAG, "PaySDK not ready - waiting...")
                updateState { it.copy(
                    statusMessage = "Initializing card reader..."
                ) }

                val ready = withContext(Dispatchers.IO) {
                    TransactionManager.waitForReady(10000)
                }

                if (ready) {
                    Log.e(TAG, "PaySDK ready - starting card detection")
                    doStartCardDetection()
                } else {
                    Log.e(TAG, "PaySDK failed to initialize")
                    updateState { it.copy(
                        statusMessage = "Card reader not available.\nPlease restart the app.",
                        isProcessing = false
                    ) }
                }
            } else {
                doStartCardDetection()
            }
        }
    }

    /**
     * Start card detection (internal)
     * Note: EMVHandler is created by Activity, not ViewModel
     * This method is kept for state management
     */
    private fun doStartCardDetection() {
        Log.e(TAG, "doStartCardDetection: Setting status to 'Present Card...'")
        updateState { it.copy(
            statusMessage = "Present Card\nTap, insert, or swipe"
        ) }
        Log.e(TAG, "doStartCardDetection: State updated, Activity should observe and start detection")
    }

    /**
     * Cancel card detection
     */
    fun cancelCardDetection() {
        updateState { it.copy(isProcessing = false) }
    }

    /**
     * Update status message
     */
    fun updateStatus(message: String) {
        updateState { it.copy(statusMessage = message) }
    }

    /**
     * Update card type
     */
    fun updateCardType(cardType: Int) {
        updateState { it.copy(cardType = cardType) }
    }

    /**
     * Update PAN
     */
    fun updatePan(pan: String) {
        updateState { it.copy(currentPan = pan) }
    }

    /**
     * Set manual PIN block
     */
    fun setManualPinBlock(pinBlock: ByteArray) {
        updateState { it.copy(
            manualPinBlock = pinBlock,
            isManualPinEntry = true
        ) }
    }

    /**
     * Handle PIN entry state update
     */
    fun handlePinEntryState(pinType: Int, isManual: Boolean) {
        currentPinType = pinType
        updateState { it.copy(
            isManualPinEntry = isManual
        ) }
    }

    /**
     * Handle online process state update
     * Note: Actual online processing is handled by Activity
     * This method just updates the state
     */
    fun handleOnlineProcessState() {
        updateStatus("Processing online authorization...")
    }
    
    /**
     * Get EMV handler factory for Activity to create handler
     */
    fun getEmvHandlerFactory(): EmvHandlerFactory = emvHandlerFactory
    
    /**
     * Update processing state (for Activity use)
     */
    fun setProcessing(isProcessing: Boolean) {
        updateState { it.copy(isProcessing = isProcessing) }
    }
    
    /**
     * Get current amount minor units (for Activity use)
     */
    fun getAmountMinor12(): String {
        return _uiState.value.amountMinor12
    }
    
    /**
     * Get current STAN (for Activity use)
     */
    fun getStan(): Int {
        return _uiState.value.stan
    }

    /**
     * Convert amount to minor units (12 digits)
     */
    private fun convertToMinorUnits(amount: BigDecimal): String {
        val minor = amount.multiply(BigDecimal("100"))
            .toBigInteger()
            .toString()
        return minor.padStart(12, '0')
    }

    /**
     * Mask card number for display
     */
    private fun maskCardNo(cardNo: String): String {
        if (cardNo.length < 10) return "****"
        return "${cardNo.substring(0, 6)}****${cardNo.substring(cardNo.length - 4)}"
    }
}

