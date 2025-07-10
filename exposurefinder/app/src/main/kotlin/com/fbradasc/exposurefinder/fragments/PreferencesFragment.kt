package com.fbradasc.exposurefinder.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.Navigation
import com.fbradasc.exposurefinder.R
import com.fbradasc.exposurefinder.databinding.FragmentPreferencesBinding
import com.fbradasc.exposurefinder.utils.fitSystemWindows
import com.fbradasc.exposurefinder.utils.onWindowInsets
import com.fbradasc.exposurefinder.utils.topMargin

class PreferencesFragment : BaseFragment<FragmentPreferencesBinding>(R.layout.fragment_preferences) {
    override val binding: FragmentPreferencesBinding by lazy { FragmentPreferencesBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsContainer, SettingsFragment())
            .commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.groupSettingsActions
        adjustInsets()

        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    /**
     * This methods adds all necessary margins to some views based on window insets and screen orientation
     * */
    private fun adjustInsets() {
        activity?.window?.fitSystemWindows()
        binding.btnBack.onWindowInsets { view, windowInsets ->
            view.topMargin = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
        }
    }

    override fun onBackPressed() {
        view?.let { Navigation.findNavController(it).popBackStack() }
    }
}
