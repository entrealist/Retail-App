package com.drugstore.app.login.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drugstore.R
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.data.repository.entity.Input.EMAIL
import com.drugstore.data.repository.entity.Input.PASSWORD
import com.drugstore.databinding.FragmentLoginBinding
import com.kasprov.android.core.activity.*
import com.kasprov.android.core.fragment.navigateTo
import com.kasprov.android.core.fragment.observeEvent
import com.kasprov.android.core.fragment.observeNonNull
import com.kasprov.android.core.fragment.viewDataBindings
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@AndroidEntryPoint
class LoginFragment : Fragment() {

    var binding: FragmentLoginBinding by viewDataBindings()
    val model: LoginVM by viewModels()
    val promptInfo by lazy { createBiometricPromptInfo() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().apply {
            setStatusBarColorResource(themeStatusBarColor)
            setNavigationBarColorResource(themeNavigationBarColor)
            setWindowLightStatusBar(themeWindowLightStatusBar)
            setWindowLightNavigationBar(themeWindowLightNavigationBar)
            toolbar?.visibility = View.GONE
        }

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        createBiometricPrompt().apply {
            observeEvent(model.onAuthenticateBiometric) { authenticate(promptInfo, it) }
        }
        observeEvent(model.onNavigateTo) { navigateTo(it) }

        binding.etLoginPassword.setOnEditorActionDoneListener { model.submit() }
    }

    private fun createBiometricPrompt() = BiometricPrompt(
        this,
        Dispatchers.Main.immediate.asExecutor(),
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                model.setInputError(PASSWORD, errString.toString())
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                model.onAuthenticationSucceeded(result)
            }
        }
    )

    private fun createBiometricPromptInfo() = BiometricPrompt.PromptInfo.Builder()
        .setTitle(getString(R.string.login_biometric_title))
        .setNegativeButtonText(getString(R.string.login_biometric_negative_button))
        .build()

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(
        EMAIL to binding.tilLoginEmail,
        PASSWORD to binding.tilLoginPassword
    )
}