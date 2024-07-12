package com.demn.fooplugin.services

import android.content.Context

class BasicSettingsRepository(
    private val context: Context
) {
    companion object {
        const val PreferencesName = "basic_settings"
    }

    private val sharedPreferences = context.getSharedPreferences(PreferencesName, Context.MODE_PRIVATE)

    fun write(key: String, value: String) {
        sharedPreferences.edit().apply {
            putString(key, value)
            commit()
        }
    }

    fun read(key: String): String = sharedPreferences.getString(key, null) ?: ""
}