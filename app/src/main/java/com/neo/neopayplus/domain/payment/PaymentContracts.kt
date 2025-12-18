package com.neo.neopayplus.domain.payment

import com.neo.neopayplus.domain.mvi.UiIntent
import com.neo.neopayplus.domain.payment.model.EntryMode
import java.math.BigDecimal

sealed class PaymentIntent : UiIntent {
    data class EnterAmount(val amount: BigDecimal) : PaymentIntent()
    data class CardPresented(val entryMode: EntryMode) : PaymentIntent()
    data object StartOnline : PaymentIntent()
    data class OnlineResult(val approved: Boolean, val rc: String, val msg: String, val rrn: String?) : PaymentIntent()
}

sealed class PaymentState {
    data object Ready : PaymentState()
    data class Amount(val current: BigDecimal?) : PaymentState()
    data class WaitingForCard(val amount: BigDecimal) : PaymentState()
    data class PinEntry(val amount: BigDecimal) : PaymentState()
    data class Processing(val amount: BigDecimal) : PaymentState()
    data class Done(val approved: Boolean, val rc: String, val msg: String, val rrn: String?) : PaymentState()
}

sealed class PaymentEffect {
    data class Print(val rrn: String) : PaymentEffect()
}

