package com.example.explorer

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.explorer.http.HttpConfig
import com.example.explorer.http.HttpRequest

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener { config, key ->
            if (key.equals("msg")) {
                HttpRequest.instance().post(
                    "/msg", mapOf(
                        "msg" to config.getString("msg", "")!!
                    )
                ).subscribe()
            } else {
                val requestConfig = HttpRequest.instance().config
                HttpRequest.config(
                    HttpConfig(
                        requestHost = config.getString("host", "")!!,
                        requestTimeout = requestConfig.requestTimeout
                    )
                )
            }
        }
    }
}