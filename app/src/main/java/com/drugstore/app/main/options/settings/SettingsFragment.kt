package com.drugstore.app.main.options.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drugstore.app.core.isBiometricAvailable
import com.drugstore.databinding.FragmentSettingsBinding
import com.kasprov.android.core.fragment.navigateTo
import com.kasprov.android.core.fragment.observeEvent
import com.kasprov.android.core.fragment.observeNonNull
import com.kasprov.android.core.fragment.viewDataBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    var binding: FragmentSettingsBinding by viewDataBindings()
    val model: SettingsVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeNonNull(model.biometricStatusResId) { binding.vSettingsBiometric.subtitle = getString(it) }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
    }

    override fun onStart() {
        super.onStart()
        model.isBiometricAvailable.value = requireContext().isBiometricAvailable()
    }
}