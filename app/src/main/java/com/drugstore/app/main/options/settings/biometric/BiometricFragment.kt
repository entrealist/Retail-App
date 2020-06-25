package com.drugstore.app.main.options.settings.biometric

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drugstore.R
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.isBiometricAvailable
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.data.repository.entity.Input.PASSWORD
import com.drugstore.databinding.FragmentBiometricBinding
import com.kasprov.android.core.activity.hideSoftInputFromWindow
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@AndroidEntryPoint
class BiometricFragment : Fragment() {

    var binding: FragmentBiometricBinding by viewDataBindings()
    val model: BiometricVM by viewModels()
    val promptInfo by lazy { createBiometricPromptInfo() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBiometricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        createBiometricPrompt().apply {
            observeEvent(model.onAuthenticateBiometric) { authenticate(promptInfo, it) }
        }
        observeNonNull(model.titleResId) { binding.tvBiometricTitle.setText(it) }
        observeEvent(model.onNavigateTo) { navigateTo(it) }

        binding.etBiometricDisabledPassword.setOnEditorActionDoneListener { model.enable() }
    }

    private fun createBiometricPrompt() = BiometricPrompt(
        this,
        Dispatchers.Main.immediate.asExecutor(),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                model.biometricAuthenticationError.value = errString
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                model.onAuthenticationSucceeded(result)
            }
        }
    )

    private fun createBiometricPromptInfo() = BiometricPrompt.PromptInfo.Builder()
        .setTitle(getString(R.string.biometric_biometric_title))
        .setNegativeButtonText(getString(R.string.biometric_biometric_negative_button))
        .build()

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(PASSWORD to binding.tilBiometricDisabledPassword)

    override fun onStart() {
        super.onStart()
        if (!requireContext().isBiometricAvailable()) navigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}