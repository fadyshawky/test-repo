package com.neo.neopayplus.nav

sealed class Route(val path: String) {
    data object Home : Route("home")
    
    // Payment flow
    data object Amount : Route("amount?type={type}") {
        fun path(type: String) = "amount?type=$type" // type: purchase/refund/void
    }
    
    // Unified payment screen (card detection + processing in one screen)
    data object Payment : Route("payment?amount={amount}") {
        fun path(amount: String) = "payment?amount=$amount"
    }
    
    // Legacy routes (kept for backward compatibility)
    data object Card : Route("card")
    data object Processing : Route("processing")
    
    data object Result : Route("result?approved={approved}&rc={rc}&msg={msg}") {
        fun path(approved: Boolean, rc: String, msg: String) =
            "result?approved=$approved&rc=$rc&msg=$msg"
    }
    
    data object Receipt : Route("receipt?rrn={rrn}") {
        fun path(rrn: String) = "receipt?rrn=$rrn"
    }
    
    // Service screens
    data object Reversal : Route("reversal")
    data object Settlement : Route("settlement")
    data object History : Route("history")
    data object Settings : Route("settings")
}

