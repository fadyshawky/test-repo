package com.neo.neopayplus.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource
import java.security.spec.MGF1ParameterSpec

object KeyStoreRsaUtil {

    private const val TAG = "KeyStoreRsaUtil"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "terminal_rsa_key"
    private const val RSA_TRANSFORMATION = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"

    @JvmStatic
    @Synchronized
    fun ensureRsaKey(): KeyPair {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (ks.containsAlias(KEY_ALIAS)) {
            try {
                val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
                val priv = ks.getKey(KEY_ALIAS, null)
                val spec = OAEPParameterSpec(
                    "SHA-1",
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
                cipher.init(Cipher.DECRYPT_MODE, priv as java.security.PrivateKey, spec)
                Log.i(TAG, "Existing RSA key supports OAEP SHA-1")
                return KeyPair(ks.getCertificate(KEY_ALIAS).publicKey, priv as java.security.PrivateKey)
            } catch (ex: Exception) {
                Log.w(TAG, "Existing key incompatible with OAEP SHA-1, regenerating...")
                ks.deleteEntry(KEY_ALIAS)
            }
        }
        Log.i(TAG, "Generating RSA 2048 OAEP SHA-1 keypair")
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(2048)
            .setDigests(KeyProperties.DIGEST_SHA1)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setUserAuthenticationRequired(false)
            .build()
        kpg.initialize(spec)
        return kpg.generateKeyPair()
    }

    @JvmStatic
    fun exportPublicKeyPem(): String {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        ensureRsaKey()
        val publicKey = ks.getCertificate(KEY_ALIAS).publicKey.encoded
        val b64 = Base64.encodeToString(publicKey, Base64.NO_WRAP)
        return "-----BEGIN PUBLIC KEY-----\n$b64\n-----END PUBLIC KEY-----"
    }

    @JvmStatic
    @Synchronized
    fun decryptKeyMaterial(base64Wrapped: String): ByteArray {
        Log.i(TAG, "decryptKeyMaterial called, input base64 length=${base64Wrapped.length}")
        
        ensureRsaKey()
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val privateKey = ks.getKey(KEY_ALIAS, null)
            ?: throw IllegalStateException("Private key missing")
        
        val encryptedData = Base64.decode(base64Wrapped, Base64.DEFAULT)
        Log.i(TAG, "Encrypted data decoded, ${encryptedData.size} bytes, hex=${encryptedData.joinToString("") { "%02X".format(it) }}")
        
        val oaepSpec = OAEPParameterSpec(
            "SHA-1",
            "MGF1",
            MGF1ParameterSpec.SHA1,
            PSource.PSpecified.DEFAULT
        )
        val cipher = Cipher.getInstance(RSA_TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, privateKey as java.security.PrivateKey, oaepSpec)
        
        val decrypted = cipher.doFinal(encryptedData)
        Log.i(TAG, "Decrypted ${decrypted.size} bytes, hex=${decrypted.joinToString("") { "%02X".format(it) }}")
        
        // Check if decrypted data is base64 string (backend encrypted base64-encoded TMK)
        val asString = try { String(decrypted, Charsets.US_ASCII).trim() } catch (e: Exception) { null }
        if (asString != null && asString.matches(Regex("^[A-Za-z0-9+/=]+$")) && asString.length in 20..50) {
            Log.i(TAG, "Decrypted looks like base64: '$asString', decoding inner layer...")
            try {
                val innerDecoded = Base64.decode(asString, Base64.DEFAULT)
                Log.i(TAG, "Inner base64 decoded to ${innerDecoded.size} bytes: ${innerDecoded.joinToString("") { "%02X".format(it) }}")
                return innerDecoded
            } catch (e: Exception) {
                Log.w(TAG, "Inner base64 decode failed: ${e.message}")
            }
        }
        
        return decrypted
    }

    @JvmStatic
    fun decryptKeyMaterialAuto(base64Wrapped: String): ByteArray {
        ensureRsaKey()
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val privateKey = ks.getKey(KEY_ALIAS, null) as? java.security.PrivateKey
            ?: throw IllegalStateException("Private key missing")
        Log.i(TAG, "Private key: ${privateKey}")
        val encryptedData = Base64.decode(base64Wrapped, Base64.DEFAULT)

        // Try multiple padding schemes in order: OAEP-SHA1, OAEP-SHA256, PKCS#1
        val transformations = listOf(
            "RSA/ECB/OAEPWithSHA-1AndMGF1Padding",
            "RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
            "RSA/ECB/PKCS1Padding"
        )

        for (transformation in transformations) {
            try {
                val cipher = Cipher.getInstance(transformation)
                when {
                    transformation.contains("OAEPWithSHA-1") -> {
                        val spec = OAEPParameterSpec(
                            "SHA-1",
                            "MGF1",
                            MGF1ParameterSpec.SHA1,
                            PSource.PSpecified.DEFAULT
                        )
                        cipher.init(Cipher.DECRYPT_MODE, privateKey, spec)
                    }
                    transformation.contains("OAEPWithSHA-256") -> {
                        val spec = OAEPParameterSpec(
                            "SHA-256",
                            "MGF1",
                            MGF1ParameterSpec.SHA256,
                            PSource.PSpecified.DEFAULT
                        )
                        cipher.init(Cipher.DECRYPT_MODE, privateKey, spec)
                    }
                    else -> {
                        // PKCS#1 padding
                        cipher.init(Cipher.DECRYPT_MODE, privateKey)
                    }
                }
                val decrypted = cipher.doFinal(encryptedData)
                Log.i(TAG, "✓ Decryption succeeded with $transformation")
                return decrypted
            } catch (ex: Exception) {
                Log.w(TAG, "Decryption failed with $transformation: ${ex.message}")
                // Continue to next transformation
            }
        }

        throw IllegalStateException("All decryption attempts failed (OAEP-SHA1, OAEP-SHA256, PKCS#1)")
    }

    @JvmStatic
    fun getKeyInfo(): String {
        val ks = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!ks.containsAlias(KEY_ALIAS)) return "RSA key not found"
        val priv = ks.getKey(KEY_ALIAS, null)
        return "Alias=$KEY_ALIAS Algo=${priv?.algorithm} Format=${priv?.format}"
    }
}
