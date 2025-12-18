package com.neo.neopayplus.emv

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton
import com.sunmi.pay.hardware.aidlv2.emv.EMVOptV2
import com.sunmi.pay.hardware.aidlv2.pinpad.PinPadOptV2
import com.sunmi.pay.hardware.aidlv2.readcard.ReadCardOptV2

/**
 * Factory for creating EMVHandler instances.
 * 
 * Uses dependency injection to provide SDK services.
 * Each transaction gets a new EMVHandler instance with its own callback.
 */
@Singleton
class EmvHandlerFactory @Inject constructor(
    private val readCardOptV2: ReadCardOptV2?,
    private val emvOptV2: EMVOptV2?,
    private val pinPadOptV2: PinPadOptV2?
) {
    /**
     * Create a new EMVHandler instance for a transaction.
     * 
     * @param context Application context
     * @param callback EMV callback interface
     * @return New EMVHandler instance
     */
    fun create(context: Context, callback: EMVCallback): EMVHandler {
        return EMVHandler(
            context,
            callback,
            readCardOptV2,
            emvOptV2,
            pinPadOptV2
        )
    }
}

