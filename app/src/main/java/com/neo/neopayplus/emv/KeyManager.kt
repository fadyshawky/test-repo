package com.neo.neopayplus.emv

import android.os.Bundle
import android.util.Base64
import com.neo.neopayplus.Constant
import com.neo.neopayplus.utils.ByteUtil
import com.neo.neopayplus.utils.LogUtil
import com.sunmi.pay.hardware.aidlv2.security.SecurityOptV2
import com.sunmi.payservice.AidlConstantsV2
import com.sunmi.pay.hardware.aidl.AidlConstants
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Key lifecycle manager that follows the RSA → TMK → TPK flow described in EMV_DOC.md.
 *
 * Responsibilities:
 * 1. Generate terminal RSA pair on first boot and export the public key for server provisioning.
 * 2. Receive wrapped TMK (RSA) + wrapped TPK (under TMK) and load them into PaySDK slots.
 * 3. Provide helper APIs to encrypt/decrypt data (PIN blocks) under the active TPK.
 */
class KeyManager(
    private val securityOpt: SecurityOptV2,
    private val rsaSlot: Int = RSA_KEY_INDEX,
    private val tmkSlot: Int = TMK_INDEX,
    private val tpkSlot: Int = TPK_INDEX,
) {

    data class KeyStatus(
        val slot: Int,
        val kcv: String
    )

    /**
     * Generates (or refreshes) the RSA keypair stored inside the secure element and
     * returns the base64 encoded public key that must be uploaded to the key server.
     */
    fun generateAndExportTerminalPublicKey(): String =
        Base64.encodeToString(invokeGenerateRsaKeypair(), Base64.NO_WRAP)

    /**
     * Imports a TMK wrapped with the terminal RSA public key.
     */
    fun importWrappedTmk(wrappedBase64: String, kcv: String): KeyStatus =
        importWrappedTmk(Base64.decode(wrappedBase64, Base64.NO_WRAP), kcv)

    fun importWrappedTmk(wrapped: ByteArray, kcv: String): KeyStatus {
        LogUtil.e("importWrappedTmk", "wrapped size: ${wrapped.size}, kcv: $kcv")
        val tmkClear = invokeRsaDecrypt(rsaSlot, wrapped)
        LogUtil.e("importWrappedTmk", "tmkClear size: ${tmkClear.size}")
        return importPlainTmk(tmkClear, kcv)
    }

    /**
     * Imports a plaintext TMK (decrypted via RSA) into the secure element.
     * @param tmkClear Clear TMK bytes (16 or 24 bytes for 3DES)
     * @param tmkKcv KCV hex string (6 chars)
     */
    fun importPlainTmk(tmkClear: ByteArray, tmkKcv: String): KeyStatus {
        require(tmkClear.isNotEmpty()) { "TMK is empty" }
        require(tmkKcv.isNotEmpty()) { "TMK KCV is required" }
        require(tmkClear.size == 16 || tmkClear.size == 24) { 
            "Invalid TMK length: ${tmkClear.size} bytes (expected 16 or 24)" 
        }

        val kcvBytes = ByteUtil.hexStr2Bytes(tmkKcv)
        
        val rc = securityOpt.savePlaintextKey(
            AidlConstants.Security.KEY_TYPE_TMK,
            tmkClear,
            kcvBytes,
            AidlConstants.Security.KEY_ALG_TYPE_3DES,
            tmkSlot
        )
        if (rc < 0) {
            throw IllegalStateException("Failed to save TMK, rc=$rc")
        }
        
        LogUtil.e(Constant.TAG, "✓ TMK injected (slot=$tmkSlot, KCV=$tmkKcv)")
        return KeyStatus(tmkSlot, tmkKcv)
    }

    /**
     * Imports a TPK wrapped (encrypted) under TMK.
     * SDK decrypts internally using TMK at tmkSlot.
     * @param wrapped TPK ciphertext bytes
     * @param tpkKcv KCV hex string (6 chars)
     */
    fun importWrappedTpk(wrapped: ByteArray, tpkKcv: String): KeyStatus {
        val kcvHex = if (tpkKcv.length > 6) tpkKcv.substring(0, 6).uppercase() else tpkKcv.uppercase()
        val kcvBytes = if (kcvHex.isNotEmpty()) ByteUtil.hexStr2Bytes(kcvHex) else null

        val rc = securityOpt.saveCiphertextKey(
            AidlConstants.Security.KEY_TYPE_PIK,
            wrapped,
            kcvBytes,
            tmkSlot,
            AidlConstants.Security.KEY_ALG_TYPE_3DES,
            tpkSlot
        )
        
        if (rc != 0) {
            throw IllegalStateException("Failed to save TPK, rc=$rc")
        }
        
        LogUtil.e(Constant.TAG, "✓ TPK injected (slot=$tpkSlot, KCV=$kcvHex)")
        return KeyStatus(tpkSlot, kcvHex)
    }

    /**
     * Encrypts data (typically an ISO-0 PIN block) under the active TPK slot (index 12).
     */
    fun encryptUnderTpk(plain: ByteArray): ByteArray {
        val cfg = Bundle().apply {
            putInt("keyIndex", tpkSlot)
            putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_PIK)
            putInt("dataMode", AidlConstantsV2.Security.DATA_MODE_ECB)
            putByteArray("dataIn", plain)
        }
        val out = ByteArray(plain.size)
        val rc = securityOpt.dataEncryptEx(cfg, out)
        check(rc == 0) { "Failed to encrypt under TPK, rc=$rc" }
        return out
    }

    /**
     * Lightweight check to confirm a key exists in the secure element.
     */
    fun hasKey(slot: Int, keyType: Int): Boolean {
        val kcv = ByteArray(8)
        
        return securityOpt.getKeyCheckValue(keyType, slot, kcv) == 0
    }

    private fun unwrapUnderTmk(cipher: ByteArray): ByteArray {
        val cfg = Bundle().apply {
            putInt("keyIndex", tmkSlot)
            putInt("keyType", AidlConstantsV2.Security.KEY_TYPE_TMK)
            putInt("dataMode", AidlConstantsV2.Security.DATA_MODE_ECB)
            putByteArray("dataIn", cipher)
        }
        val out = ByteArray(cipher.size)
        val rc = securityOpt.dataDecryptEx(cfg, out)
        check(rc == 0) { "Failed to unwrap under TMK, rc=$rc" }
        return out
    }

    @Suppress("AndroidLintGetInstance")
    private fun computeSoftwareKcv(key: ByteArray): String {
        val cipher = Cipher.getInstance("DESede/ECB/NoPadding")
        val spec: Key = SecretKeySpec(key, "DESede")
        cipher.init(Cipher.ENCRYPT_MODE, spec)
        val zero = ByteArray(8)
        val block = cipher.doFinal(zero)
        return block.take(3).joinToString("") { String.format("%02X", it) }
    }

    private fun invokeGenerateRsaKeypair(): ByteArray {
        val methods = securityOpt.javaClass.methods.filter { it.name == "generateRSAKeypairEx" }
        for (method in methods) {
            try {
                val params = method.parameterTypes
                when (params.size) {
                    2 -> {
                        val result = method.invoke(securityOpt, rsaSlot, 2048)
                        if (result is ByteArray) {
                            return result
                        }
                    }
                    3 -> {
                        val outSize = 512
                        val buffer = ByteArray(outSize)
                        val result = method.invoke(securityOpt, rsaSlot, 2048, buffer)
                        if (result == null || (result is Int && result == 0)) {
                            return buffer
                        }
                    }
                }
            } catch (t: Throwable) {
                LogUtil.e(Constant.TAG, "generateRSAKeypairEx invocation failed: ${t.message}")
            }
        }
        throw IllegalStateException("generateRSAKeypairEx not available on this SDK")
    }

    private fun invokeRsaDecrypt(keyIndex: Int, cipherBytes: ByteArray): ByteArray {
        val clazz = securityOpt.javaClass

        LogUtil.e("TMK_DEBUG", "invokeRsaDecrypt: cipherBytes.len=${cipherBytes?.size ?: 0}")

        // 1) try byte[] -> byte[] method (returns ByteArray)
        clazz.methods.filter { it.name.contains("rsaDecrypt", true) || it.name.contains("rsa", true) }.forEach { m ->
            try {
                LogUtil.e("TMK_DEBUG", "Trying method ${m.name} params=${m.parameterTypes.joinToString()}")
                val params = m.parameterTypes

                when {
                    params.size == 2 && params[0] == Int::class.javaPrimitiveType && params[1] == ByteArray::class.java -> {
                        val res = m.invoke(securityOpt, keyIndex, cipherBytes)
                        if (res is ByteArray) {
                            LogUtil.e("TMK_DEBUG", "${m.name} returned ByteArray len=${res.size}")
                            return res
                        } else {
                            LogUtil.e("TMK_DEBUG", "${m.name} returned non-ByteArray: $res")
                        }
                    }
                    params.size == 3 && params[0] == Int::class.javaPrimitiveType && params[1] == ByteArray::class.java && params[2] == ByteArray::class.java -> {
                        val out = ByteArray(512) // big enough buffer
                        val res = m.invoke(securityOpt, keyIndex, cipherBytes, out)
                        when (res) {
                            is Int -> {
                                LogUtil.e("TMK_DEBUG", "${m.name} returned int=$res")
                                if (res == 0) {
                                    LogUtil.e("TMK_DEBUG", "${m.name} out[0..31]=${out.copyOfRange(0, minOf(32, out.size)).joinToString("") { "%02X".format(it) }}")
                                    return out // return entire buffer (caller must check length)
                                } else {
                                    LogUtil.e("TMK_DEBUG", "${m.name} failed rc=$res")
                                }
                            }
                            null -> {
                                LogUtil.e("TMK_DEBUG", "${m.name} returned null, returning out buffer")
                                return out
                            }
                            else -> LogUtil.e("TMK_DEBUG", "${m.name} returned unknown type ${res.javaClass}")
                        }
                    }
                    // Try signature variants with offsets/lengths
                    params.size == 5 && params[0] == Int::class.javaPrimitiveType -> {
                        // common form: (index, in, inLen, out, outLenRef) - attempt best-effort
                        val out = ByteArray(512)
                        try {
                            val res = m.invoke(securityOpt, keyIndex, cipherBytes, cipherBytes.size, out, IntArray(1))
                            LogUtil.e("TMK_DEBUG", "${m.name} invoked; res=$res; out[0..31]=${out.copyOfRange(0,32).joinToString(""){ "%02X".format(it)}}")
                            return out
                        } catch (ex: Throwable) {
                            LogUtil.e("TMK_DEBUG", "${m.name} invocation failed: ${ex.message}")
                        }
                    }
                    // Try string variant: (index, String)
                    params.size == 2 && params[0] == Int::class.javaPrimitiveType && params[1] == String::class.java -> {
                        val b64 = Base64.encodeToString(cipherBytes, Base64.NO_WRAP)
                        val res = m.invoke(securityOpt, keyIndex, b64)
                        if (res is ByteArray) {
                            LogUtil.e("TMK_DEBUG", "${m.name} (string) returned bytes len=${res.size}")
                            return res
                        } else if (res is Int && res == 0) {
                            LogUtil.e("TMK_DEBUG", "${m.name} (string) returned rc=0 - but no bytes")
                        } else LogUtil.e("TMK_DEBUG", "${m.name} (string) returned $res")
                    }
                }
            } catch (t: Throwable) {
                LogUtil.e("TMK_DEBUG", "method ${m.name} invocation threw: ${t.javaClass.name}: ${t.message}")
            }
        }

        // fallback: try android keystore decryption path if you previously created keystore keys
        try {
            LogUtil.e("TMK_DEBUG", "Attempting AndroidKeyStore fallback decrypt...")
            val fallback = com.neo.neopayplus.security.KeyStoreRsaUtil.decryptKeyMaterialAuto(
                Base64.encodeToString(cipherBytes, Base64.DEFAULT)
            )
            LogUtil.e("TMK_DEBUG", "AndroidKeystore fallback len=${fallback.size} hex=${fallback.joinToString("") { "%02X".format(it) }}")
            return fallback
        } catch (ex: Throwable) {
            LogUtil.e("TMK_DEBUG", "AndroidKeyStore fallback failed: ${ex.message}")
        }

        throw IllegalStateException("rsaDecryptWithIndex not available or decrypt failed (see TMK_DEBUG logs)")
    }

    companion object {
        @JvmField val RSA_KEY_INDEX = 10
        @JvmField val TMK_INDEX = 11
        @JvmField val TPK_INDEX = 12
    }
}


