package com.neo.neopayplus.navigation

sealed class Route(val path: String) {
    data object Home : Route("home")
    // Payment
    data object Amount : Route("amount")
    data object CardDetect : Route("card_detect")
    data object Pin : Route("pin")
    data object Processing : Route("processing")
    data object Result : Route("result/{approved}") {
        fun build(approved: Boolean) = "result/$approved"
    }
    // Refund reuses payment steps
    data object RefundAmount : Route("refund_amount")
    // Void
    data object TxnHistory : Route("txn_history")
    data object TxnDetails : Route("txn_details/{rrn}") {
        fun build(rrn: String) = "txn_details/$rrn"
    }
    data object VoidConfirm : Route("void_confirm/{rrn}") {
        fun build(rrn: String) = "void_confirm/$rrn"
    }
    // Settlement
    data object SettlementConfirm : Route("settlement_confirm")
    data object SettlementProcessing : Route("settlement_processing")
    data object SettlementResult : Route("settlement_result")
}

