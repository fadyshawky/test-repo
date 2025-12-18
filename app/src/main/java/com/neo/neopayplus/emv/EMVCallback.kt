package com.neo.neopayplus.emv

/**
 * EMV Callback Interface
 * 
 * Single callback interface for all EMV events.
 * Implement this to handle EMV transaction flow in your Activity/Fragment.
 */
interface EMVCallback {

    /**
     * Called for all EMV events
     * 
     * @param step The current EMV step
     * @param data Optional data associated with the step (type varies by step)
     *             - EMV_APP_SELECT: List of EMVCandidateV2
     *             - EMV_FINAL_APP_SELECT: String (AID tag 9F06)
     *             - EMV_CONFIRM_CARD_NO: String (card number)
     *             - EMV_SHOW_PIN_PAD: Integer (pinType: 0=Online, 1=Offline)
     *             - EMV_CERT_VERIFY: String (cert info)
     *             - EMV_TRANS_SUCCESS/FAIL: String (description)
     *             - CARD_FOUND: Bundle (card info)
     *             - CARD_ERROR: String (error message)
     */
    fun onResult(step: EMVSteps, data: Any?)

    /**
     * Called for transaction result with response code
     * 
     * @param step The result step (EMV_TRANS_SUCCESS, EMV_TRANS_FAIL,
     *             TRANS_PRESENT_CARD)
     * @param desc Description/message
     * @param code Result code from EMV kernel
     */
    fun onResult(step: EMVSteps, desc: String, code: Int)
}
