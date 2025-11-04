package com.neo.neopayplus.emv

/**
 * Thin interface to your existing PayLib integration. Implement in your current service layer
 * and call from the Composables/VMs. This keeps UI module independent from Sunmi SDK.
 */
interface EmvBridge {
    fun detectCard(onResult: (requiresPin: Boolean, cardNo: String?) -> Unit, onError: (Throwable) -> Unit)
    fun requestPin(cardNo: String, pinType: Int, onDone: () -> Unit, onCancel: () -> Unit)
    fun authorize(amountCents: Long, onResult: (approved: Boolean) -> Unit)
    fun printReceipt(copy: String, onDone: () -> Unit)
}

