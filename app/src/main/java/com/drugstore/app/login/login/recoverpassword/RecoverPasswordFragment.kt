package com.drugstore.app.login.login.recoverpassword

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
import com.drugstore.data.repository.entity.Input.EMAIL
import com.drugstore.databinding.FragmentRecoverPasswordBinding
import com.kasprov.android.core.activity.*
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecoverPasswordFragment : Fragment() {

    var binding: FragmentRecoverPasswordBinding by viewDataBindings()
    val model: RecoverPasswordVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in RecoverPasswordVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().apply {
            setStatusBarColorResource(R.color.statusBarColor_light)
            setNavigationBarColorResource(R.color.navigationBarColor_light)
            setWindowLightStatusBar(true)
            setWindowLightNavigationBar(true)
            toolbar?.visibility = View.VISIBLE
        }

        binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }

        binding.etRecoverPasswordEmail.setOnEditorActionDoneListener { model.submit() }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(
        EMAIL to binding.tilRecoverPasswordEmail
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}