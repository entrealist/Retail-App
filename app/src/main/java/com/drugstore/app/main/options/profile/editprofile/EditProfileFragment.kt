package com.drugstore.app.main.options.profile.editprofile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.data.repository.entity.Input.EMAIL
import com.drugstore.databinding.FragmentEditProfileBinding
import com.kasprov.android.core.activity.hideSoftInputFromWindow
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    var binding: FragmentEditProfileBinding by viewDataBindings()
    val model: EditProfileVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in EditProfileVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }

        binding.etEditProfileEmail.setOnEditorActionDoneListener { model.submit() }
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
        EMAIL to binding.tilEditProfileEmail
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}