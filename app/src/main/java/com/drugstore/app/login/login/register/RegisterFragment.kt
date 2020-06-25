package com.drugstore.app.login.login.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.drugstore.R
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.app.core.setLinkMovementMethod
import com.drugstore.data.repository.entity.Input.*
import com.drugstore.databinding.FragmentRegisterBinding
import com.kasprov.android.core.activity.*
import com.kasprov.android.core.fragment.navigateTo
import com.kasprov.android.core.fragment.observeEvent
import com.kasprov.android.core.fragment.observeNonNull
import com.kasprov.android.core.fragment.viewDataBindings
import com.kasprov.android.core.scrollToTopOf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    var binding: FragmentRegisterBinding by viewDataBindings()
    val model: RegisterVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in RegisterVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().apply {
            setStatusBarColorResource(R.color.statusBarColor_light)
            setNavigationBarColorResource(R.color.translucent)
            setWindowLightStatusBar(true)
            toolbar?.visibility = View.VISIBLE
        }

        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }

        binding.tvRegisterUserConsent.setLinkMovementMethod()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
            observeEvent(model.onFocusInput) {
                this[it]?.apply { post { binding.svRegister.scrollToTopOf(this) } }
            }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(
        EMAIL to binding.tilRegisterEmail,
        FIRST_NAME to binding.tilRegisterFirstName,
        LAST_NAME to binding.tilRegisterLastName,
        PASSWORD to binding.tilRegisterPassword,
        BIRTHDAY to binding.tilRegisterBirthdayDate
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}