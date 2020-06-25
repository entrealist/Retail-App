package com.drugstore.app.main.options.profile.changepassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.data.repository.entity.Input.CURRENT_PASSWORD
import com.drugstore.data.repository.entity.Input.PASSWORD
import com.drugstore.databinding.FragmentChangePasswordBinding
import com.kasprov.android.core.activity.hideSoftInputFromWindow
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    var binding: FragmentChangePasswordBinding by viewDataBindings()
    val model: ChangePasswordVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in ChangePasswordVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }

        binding.etChangePasswordRepeatedNewPassword.setOnEditorActionDoneListener { model.submit() }
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
        CURRENT_PASSWORD to binding.tilChangePasswordCurrentPassword,
        PASSWORD to binding.tilChangePasswordNewPassword
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}