package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.securityDataStore: DataStore<Preferences> by preferencesDataStore(name = "security_settings")

class SecurityPreferences(private val context: Context) {

    companion object {
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val SESSION_TIMEOUT_MINUTES = intPreferencesKey("session_timeout_minutes")
        val REQUIRE_BIOMETRIC_FOR_PAYMENTS = booleanPreferencesKey("require_biometric_for_payments")
        val USER_PIN = stringPreferencesKey("user_pin")
        val LAST_ACTIVE_TIMESTAMP = longPreferencesKey("last_active_timestamp")
        val IS_ENROLLED_DEMO = booleanPreferencesKey("is_enrolled_demo")
    }

    val isBiometricEnabled: Flow<Boolean> = context.securityDataStore.data.map { prefs ->
        prefs[BIOMETRIC_ENABLED] ?: false
    }

    val sessionTimeoutMinutes: Flow<Int> = context.securityDataStore.data.map { prefs ->
        prefs[SESSION_TIMEOUT_MINUTES] ?: 1 // Default 1 min
    }

    val requireBiometricForPayments: Flow<Boolean> = context.securityDataStore.data.map { prefs ->
        prefs[REQUIRE_BIOMETRIC_FOR_PAYMENTS] ?: true
    }

    val userPin: Flow<String> = context.securityDataStore.data.map { prefs ->
        prefs[USER_PIN] ?: "1234"
    }

    val lastActiveTimestamp: Flow<Long> = context.securityDataStore.data.map { prefs ->
        prefs[LAST_ACTIVE_TIMESTAMP] ?: System.currentTimeMillis()
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.securityDataStore.edit { prefs ->
            prefs[BIOMETRIC_ENABLED] = enabled
        }
    }

    suspend fun setSessionTimeoutMinutes(minutes: Int) {
        context.securityDataStore.edit { prefs ->
            prefs[SESSION_TIMEOUT_MINUTES] = minutes
        }
    }

    suspend fun setRequireBiometricForPayments(required: Boolean) {
        context.securityDataStore.edit { prefs ->
            prefs[REQUIRE_BIOMETRIC_FOR_PAYMENTS] = required
        }
    }

    suspend fun setUserPin(pin: String) {
        context.securityDataStore.edit { prefs ->
            prefs[USER_PIN] = pin
        }
    }

    suspend fun updateLastActiveTimestamp() {
        context.securityDataStore.edit { prefs ->
            prefs[LAST_ACTIVE_TIMESTAMP] = System.currentTimeMillis()
        }
    }
}
