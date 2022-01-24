package com.example.map.googlemap.utils

import android.content.Context
import com.securepreferences.SecurePreferences

class PrefUtils(context: Context) {

    private val secureSharedPref = SecurePreferences(context)

    private fun getSecureEdit() = secureSharedPref.edit()

    fun saveLocations(locationListToJsonString: String) {
        getSecureEdit().putString(PREF_KEY_SEARCH_LOCATION_LIST, locationListToJsonString).apply()
    }

    fun loadLocations(): String {
        return secureSharedPref.getString(PREF_KEY_SEARCH_LOCATION_LIST, "") ?: ""
    }
}