package com.neo.neopayplus.domain.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject
import com.neo.neopayplus.data.payment.datasource.EmvDataSource
import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.usecase.ProcessPaymentUseCase
import com.neo.neopayplus.domain.payment.usecase.ReverseTransactionUseCase
import com.neo.neopayplus.emv.TransactionState

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val reverseTransactionUseCase: ReverseTransactionUseCase,
    private val emvDataSource: EmvDataSource
) : ViewModel() {
    
    // Expose transaction state flow for UI observation
    // This provides direct access to TransactionState for screens that need it
    val transactionStateFlow: kotlinx.coroutines.flow.StateFlow<TransactionState>
        get() = emvDataSource.getTransactionStateFlow()
    
    val reversalService = com.neo.neopayplus.emv.ReversalService()
    
    // Map TransactionState to PaymentState for UI
    private val _state = MutableStateFlow<PaymentState>(PaymentState.Ready)
    val uiState: StateFlow<PaymentState> = _state.asStateFlow()

    private val _effect = Channel<PaymentEffect>(Channel.BUFFERED)
    val effects = _effect.receiveAsFlow()

    private var amount: BigDecimal = BigDecimal.ZERO
    private var currentEntryMode: EntryMode? = null
    private var isReversalMode: Boolean = false
    private var reversalRrn: String? = null

    init {
        // Observe transaction state through EmvDataSource (Clean Architecture)
        // This provides real-time updates (Processing, PinRequired, etc.)
        viewModelScope.launch {
            emvDataSource.getTransactionStateFlow().collect { emvState ->
                when (emvState) {
                    is TransactionState.Idle -> {
                        // Keep current state, don't reset to Ready
                    }
                    is TransactionState.Processing -> {
                        _state.value = PaymentState.Processing(amount)
                    }
                    is TransactionState.PinRequired -> {
                        _state.value = PaymentState.PinEntry(amount)
                    }
                    is TransactionState.Completed -> {
                        _state.value = PaymentState.Done(
                            emvState.approved,
                            emvState.rc,
                            emvState.msg,
                            emvState.rrn
                        )
                    }
                    is TransactionState.Error -> {
                        _state.value = PaymentState.Done(
                            approved = false,
                            rc = "XX",
                            msg = emvState.message,
                            rrn = null
                        )
                    }
                }
            }
        }
    }

    fun onAmountEntered(value: BigDecimal) {
        amount = value
        _state.value = PaymentState.WaitingForCard(amount)
    }
    
    fun onCardPresented(entryMode: EntryMode) {
        android.util.Log.e("PaymentViewModel", "onCardPresented called: entryMode=$entryMode, amount=$amount")
        currentEntryMode = entryMode
        isReversalMode = false
        _state.value = PaymentState.Processing(amount)
        android.util.Log.e("PaymentViewModel", "State updated to Processing, current state: ${_state.value}")
    }
    
    /**
     * Set entry mode without changing state - used for restoration after navigation
     */
    fun setEntryModeOnly(entryMode: EntryMode) {
        android.util.Log.e("PaymentViewModel", "setEntryModeOnly called: entryMode=$entryMode")
        currentEntryMode = entryMode
    }
    
    /**
     * Start EMV transaction using use case (Clean Architecture).
     * This initiates the payment flow through the domain layer.
     */
    fun startEmv(stan: Int) {
        val entryMode = currentEntryMode ?: throw IllegalStateException("EntryMode not set")
        val amountPiasters = amount.multiply(BigDecimal("100")).toLong().toString().padStart(12, '0')
        val cardType = when (entryMode) {
            EntryMode.ICC -> com.sunmi.pay.hardware.aidl.AidlConstants.CardType.IC.getValue()
            EntryMode.NFC -> com.sunmi.pay.hardware.aidl.AidlConstants.CardType.NFC.getValue()
            EntryMode.MAGNETIC -> com.sunmi.pay.hardware.aidl.AidlConstants.CardType.MAGNETIC.getValue()
        }
        
        // Use EmvDataSource to start transaction (for immediate scenarios)
        // The use case will handle the full flow, but we need immediate start for card detection
        emvDataSource.startTransaction(amountPiasters, stan, cardType)
    }
    
    /**
     * Start EMV transaction with explicit card type - used when starting immediately
     * after card detection (SDK demo flow) to prevent TLV loss during UI navigation.
     * 
     * CRITICAL: This must be called IMMEDIATELY in the card detection callback,
     * before any UI navigation, to ensure TLVs set during initEmvProcess() are
     * still active when transactProcessEx() is called.
     * 
     * Uses EmvDataSource instead of direct EmvManager access (Clean Architecture).
     */
    fun startEmvWithCardType(stan: Int, cardType: Int) {
        val amountPiasters = amount.multiply(BigDecimal("100")).toLong().toString().padStart(12, '0')
        android.util.Log.e("PaymentViewModel", "startEmvWithCardType: stan=$stan, cardType=$cardType, amount=$amountPiasters")
        
        // Update entry mode based on card type
        currentEntryMode = when (cardType) {
            com.sunmi.pay.hardware.aidl.AidlConstants.CardType.IC.getValue() -> EntryMode.ICC
            com.sunmi.pay.hardware.aidl.AidlConstants.CardType.NFC.getValue() -> EntryMode.NFC
            com.sunmi.pay.hardware.aidl.AidlConstants.CardType.MAGNETIC.getValue() -> EntryMode.MAGNETIC
            else -> EntryMode.NFC
        }
        
        _state.value = PaymentState.Processing(amount)
        
        // Use EmvDataSource instead of direct EmvManager access
        emvDataSource.startTransaction(amountPiasters, stan, cardType)
    }
    
    /**
     * Request transaction reversal using use case (Clean Architecture).
     */
    fun onReversalRequested(rrn: String) {
        isReversalMode = true
        reversalRrn = rrn
        _state.value = PaymentState.Processing(BigDecimal.ZERO)
        
        // Use use case for reversal
        viewModelScope.launch {
            try {
                reverseTransactionUseCase(rrn).collect { transaction ->
                    // Handle reversal result
                    _state.value = PaymentState.Done(
                        approved = transaction.isApproved,
                        rc = transaction.responseCode ?: "00",
                        msg = transaction.responseMessage ?: "Reversal completed",
                        rrn = transaction.rrn
                    )
                }
            } catch (e: Exception) {
                _state.value = PaymentState.Done(
                    approved = false,
                    rc = "XX",
                    msg = "Reversal failed: ${e.message}",
                    rrn = null
                )
            }
        }
    }
    
    fun onTransactionCompleted(approved: Boolean, rc: String, msg: String, rrn: String?) {
        _state.value = PaymentState.Done(approved, rc, msg, rrn)
    }
    
    fun onTransactionError(message: String) {
        _state.value = PaymentState.Done(approved = false, rc = "XX", msg = message, rrn = null)
    }
    
    fun getAmount(): BigDecimal = amount
    fun getEntryMode(): EntryMode? = currentEntryMode
    fun isReversal(): Boolean = isReversalMode
    fun getReversalRrn(): String? = reversalRrn
}

