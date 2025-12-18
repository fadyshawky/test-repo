package com.neo.neopayplus.domain.payment.usecase

import com.neo.neopayplus.domain.payment.model.EntryMode
import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.model.TransactionType
import com.neo.neopayplus.domain.payment.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Use case for processing payment transactions.
 * 
 * This encapsulates the business logic for payment processing,
 * following Clean Architecture principles.
 * 
 * Use cases should:
 * - Be independent of Android framework
 * - Contain only business logic
 * - Be easily testable
 */
class ProcessPaymentUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    /**
     * Execute payment processing.
     * 
     * @param amount Transaction amount
     * @param transactionType Type of transaction
     * @param entryMode Card entry mode
     * @return Flow of transaction result
     */
    suspend operator fun invoke(
        amount: BigDecimal,
        transactionType: TransactionType,
        entryMode: EntryMode
    ): Flow<Transaction> {
        // Business logic validation
        require(amount > BigDecimal.ZERO) { "Amount must be greater than zero" }
        require(amount <= MAX_AMOUNT) { "Amount exceeds maximum allowed" }
        
        return paymentRepository.processPayment(amount, transactionType, entryMode)
    }
    
    companion object {
        private val MAX_AMOUNT = BigDecimal("999999.99")
    }
}

