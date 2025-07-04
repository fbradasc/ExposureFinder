package com.fbradasc.exposurefinder.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.fbradasc.exposurefinder.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}
