package com.drugstore.app.main.options.profile.addresses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.databinding.FragmentAddressesBinding
import com.drugstore.databinding.ItemAddressBinding
import com.drugstore.domain.entity.Address
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressesFragment : Fragment() {

    var binding: FragmentAddressesBinding by viewDataBindings()
    val model: AddressesVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val addressesAdapter = SimpleAdapter(AddressViewBinder(model)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val addressesFooterAdapter = SimpleAdapter(ActionItemViewBinder)
        binding.rvAddresses.adapter = MergeAdapter(addressesAdapter, addressesFooterAdapter)
        binding.rvAddresses.config()

        observeNonNull(model.addresses) { addressesAdapter.submitList(it) }
        observe(model.addressesFooterItems) { addressesFooterAdapter.submitList(it) }
        observeNonNull(model.isAddressesRefreshingIndicatorVisible) { binding.swlAddresses.isRefreshing = it }
        observeEvent(model.onNavigateTo) { navigateTo(it) }

        binding.swlAddresses.setOnRefreshListener { model.refresh() }
    }

    private class AddressViewBinder(
        private val model: AddressesVM
    ) : ViewDataBindingViewBinder<Address, ItemAddressBinding>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemAddressBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemAddressBinding, item: Address) {
            binding.item = item
            binding.model = model
        }
    }

    private fun RecyclerView.config() {
//        TODO delete operation is hidden for now
//        val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.bg_addresses_on_swipe_delete))
//        val icon = requireContext().getDrawable(R.drawable.ic_delete, R.color.ic_addresses_on_swipe_delete)!!
//        val swipeItemTouchCallback = SwipeItemTouchCallback(adapter, background, icon) { model.delete(it) }
//        ItemTouchHelper(swipeItemTouchCallback).attachToRecyclerView(view?.rvAddresses)
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}