package com.neo.neopayplus.keys

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.VisibleForTesting
import androidx.core.content.edit

/**
 * Persists key metadata (e.g., pin_key_id, KCV) so components outside the EMV stack
 * can reference the current provisioning state without touching the secure element.
 *
 * Backed by SharedPreferences to survive process restarts.
 */
object KeyRegistry {

    data class KeyState(
        val pinKeyId: String? = null,
        val tpkKcv: String? = null,
        val lastUpdatedMs: Long = 0L
    )

    private const val PREFS_NAME = "key_registry"
    private const val KEY_PIN_ID = "pin_key_id"
    private const val KEY_TPK_KCV = "tpk_kcv"
    private const val KEY_UPDATED = "updated"

    @Volatile
    private var prefs: SharedPreferences? = null

    /**
     * Initializes the registry. Must be called once from Application.onCreate().
     */
    @JvmStatic
    fun init(context: Context) {
        if (prefs == null) {
            synchronized(this) {
                if (prefs == null) {
                    prefs = context.applicationContext.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                    )
                }
            }
        }
    }

    private fun ensurePrefs(): SharedPreferences =
        prefs ?: throw IllegalStateException("KeyRegistry not initialized")

    /**
     * Returns the currently stored key metadata.
     */
    @JvmStatic
    fun current(): KeyState {
        val p = ensurePrefs()
        return KeyState(
            pinKeyId = p.getString(KEY_PIN_ID, null),
            tpkKcv = p.getString(KEY_TPK_KCV, null),
            lastUpdatedMs = p.getLong(KEY_UPDATED, 0L)
        )
    }

    /**
     * Persists the provided state atomically.
     */
    @JvmStatic
    fun save(state: KeyState) {
        val p = ensurePrefs()
        p.edit {
            putString(KEY_PIN_ID, state.pinKeyId)
            putString(KEY_TPK_KCV, state.tpkKcv)
            putLong(KEY_UPDATED, state.lastUpdatedMs)
        }
    }

    /**
     * Convenience helper to mutate the stored state.
     */
    @JvmStatic
    fun update(transform: (KeyState) -> KeyState) {
        val next = transform(current())
        save(next)
    }

    /**
     * Visible for tests to reset the registry state.
     */
    @VisibleForTesting
    internal fun clear() {
        prefs?.edit { clear() }
    }
}


