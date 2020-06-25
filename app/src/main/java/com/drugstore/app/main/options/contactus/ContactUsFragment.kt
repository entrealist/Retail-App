package com.drugstore.app.main.options.contactus

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.app.core.doAfterTextChanged
import com.drugstore.app.core.item.ActionItem
import com.drugstore.app.core.item.LoadingItem
import com.drugstore.app.core.setErrorsFrom
import com.drugstore.app.core.setLinkMovementMethod
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.app.core.viewbinder.LoadingItemViewBinder
import com.drugstore.data.repository.entity.Input.*
import com.drugstore.databinding.FragmentContactUsBinding
import com.drugstore.databinding.ItemContactUsContactDetailBinding
import com.drugstore.domain.entity.ContactDetail
import com.kasprov.android.core.activity.hideSoftInputFromWindow
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.CompositeAdapter
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.scrollToTopOf
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactUsFragment : Fragment() {

    var binding: FragmentContactUsBinding by viewDataBindings()
    val model: ContactUsVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in ContactUsVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentContactUsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val contactDetailsAdapter = SimpleAdapter(ContactDetailViewBinder()).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val contactDetailsFooterAdapter = CompositeAdapter(
            LoadingItem::class.java to LoadingItemViewBinder(ViewGroup.LayoutParams.WRAP_CONTENT),
            ActionItem::class.java to ActionItemViewBinder
        )
        binding.rvContactUsContactDetails.adapter = MergeAdapter(contactDetailsAdapter, contactDetailsFooterAdapter)

        observeNonNull(model.contactDetails) { contactDetailsAdapter.submitList(it) }
        observe(model.contactDetailsFooterItems) { contactDetailsFooterAdapter.submitList(it) }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
    }

    private class ContactDetailViewBinder : ViewDataBindingViewBinder<ContactDetail, ItemContactUsContactDetailBinding>() {
        override fun areItemsTheSame(oldItem: ContactDetail, newItem: ContactDetail) = oldItem.data == newItem.data
        override fun areContentsTheSame(oldItem: ContactDetail, newItem: ContactDetail) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemContactUsContactDetailBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemContactUsContactDetailBinding, item: ContactDetail) {
            binding.item = item
            binding.tvContactUsContactDetail.setLinkMovementMethod()
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        createInputToTextInputLayoutMap().apply {
            model.inputs = keys
            observeNonNull(model.inputErrors) { setErrorsFrom(it) }
            doAfterTextChanged { model.clearInputError(it) }
            observeEvent(model.onFocusInput) {
                this[it]?.apply { post { binding.nsvContactUs.scrollToTopOf(this) } }
            }
        }
    }

    private fun createInputToTextInputLayoutMap() = linkedMapOf(
        EMAIL to binding.tilContactUsEmail,
        NAME to binding.tilContactUsName,
        CONTENT to binding.tilContactUsDetails
    )

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().hideSoftInputFromWindow()
    }
}