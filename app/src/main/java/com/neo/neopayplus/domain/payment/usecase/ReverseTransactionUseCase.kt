package com.neo.neopayplus.domain.payment.usecase

import com.neo.neopayplus.domain.payment.model.Transaction
import com.neo.neopayplus.domain.payment.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for reversing transactions.
 * 
 * Encapsulates business logic for transaction reversal operations.
 */
class ReverseTransactionUseCase @Inject constructor(
    private val paymentRepository: PaymentRepository
) {
    
    /**
     * Execute transaction reversal.
     * 
     * @param rrn Retrieval Reference Number of transaction to reverse
     * @return Flow of reversal transaction result
     */
    suspend operator fun invoke(rrn: String): Flow<Transaction> {
        // Business logic validation
        require(rrn.isNotBlank()) { "RRN cannot be empty" }
        require(rrn.length == 12) { "RRN must be 12 digits" }
        require(rrn.all { it.isDigit() }) { "RRN must contain only digits" }
        
        return paymentRepository.reverseTransaction(rrn)
    }
}

