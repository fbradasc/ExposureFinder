package com.fbradasc.exposurefinder.fragments

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.fbradasc.exposurefinder.R
import com.fbradasc.exposurefinder.fragments.CameraFragment.Companion.invalidateExposure


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<ListPreference>("film_speed")
            ?.setOnPreferenceChangeListener { _, _ ->
                invalidateExposure()
                true
            }

        findPreference<ListPreference>("filter")
            ?.setOnPreferenceChangeListener { _, _ ->
                invalidateExposure()
                true
            }
    }
}
