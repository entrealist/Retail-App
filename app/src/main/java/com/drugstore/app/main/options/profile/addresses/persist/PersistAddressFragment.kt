package com.drugstore.app.main.options.profile.addresses.persist

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
import com.drugstore.data.repository.entity.Input.*
import com.drugstore.databinding.FragmentPersistAddressBinding
import com.kasprov.android.core.activity.hideSoftInputFromWindow
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.scrollToBottom
import com.kasprov.android.core.scrollToTopOf
import com.kasprov.android.core.setOnEditorActionDoneListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersistAddressFragment : Fragment() {

    var binding: FragmentPersistAddressBinding by viewDataBindings()
    val model: PersistAddressVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in PersistAddressVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPersistAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }

        if (model.isEditing) {
            binding.tvPersistAddressInfo.visibility = View.VISIBLE
            binding.btnPersistAddressSubmit.text = resources.getText(R.string.persist_address_edit_submit)
        }

        binding.etPersistAddressPhoneNumber.setOnEditorActionDoneListener {
            binding.svPersistAddress.scrollToBottom()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
            observeEvent(model.onFocusInput) {
                this[it]?.apply { post { binding.svPersistAddress.scrollToTopOf(this) } }
            }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(
        ADDRESS_TYPE to binding.tilPersistAddressAddressType,
        TITLE to binding.tilPersistAddressTitle,
        FIRST_NAME to binding.tilPersistAddressFirstName,
        LAST_NAME to binding.tilPersistAddressLastName,
        COUNTRY_ID to binding.tilPersistAddressCountryName,
        POSTAL_CODE to binding.tilPersistAddressPostalCode,
        CITY to binding.tilPersistAddressCity,
        STREET to binding.tilPersistAddressStreet,
        HOUSE_NUMBER to binding.tilPersistAddressHouseNumber,
        COMPANY_NAME to binding.tilPersistAddressCompanyName,
        PACKSTATION_ADDRESS to binding.tilPersistAddressPackstationAddress,
        PACKSTATION_NUMBER to binding.tilPersistAddressPackstationNumber,
        PHONE_NUMBER to binding.tilPersistAddressPhoneNumber
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}