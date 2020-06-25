package com.drugstore.app.main.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.R
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.databinding.FragmentOrdersBinding
import com.drugstore.databinding.ItemOrderBinding
import com.drugstore.domain.entity.OrderDomain
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.recyclerview.itemdecoration.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersFragment : Fragment() {

    var binding: FragmentOrdersBinding by viewDataBindings()
    val model: OrdersVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val ordersAdapter = SimpleAdapter(OrderViewBinder(model, viewLifecycleOwner)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val ordersFooterAdapter = SimpleAdapter(ActionItemViewBinder)
        binding.rvOrders.adapter = MergeAdapter(ordersAdapter, ordersFooterAdapter)
        binding.rvOrders.config()

        observeNonNull(model.orders) { ordersAdapter.submitList(it) }
        observe(model.ordersFooterItems) { ordersFooterAdapter.submitList(it) }
        observeNonNull(model.isOrdersRefreshingIndicatorVisible) { binding.swlOrders.isRefreshing = it }
        observeEvent(model.onNavigateTo) { navigateTo(it) }

        binding.swlOrders.setOnRefreshListener { model.refresh() }
    }

    private class OrderViewBinder(
        private val model: OrdersVM,
        private val owner: LifecycleOwner
    ): ViewDataBindingViewBinder<OrderDomain, ItemOrderBinding>() {
        override fun areItemsTheSame(oldItem: OrderDomain, newItem: OrderDomain) = oldItem.order.id == newItem.order.id
        override fun areContentsTheSame(oldItem: OrderDomain, newItem: OrderDomain) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemOrderBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemOrderBinding, item: OrderDomain) {
            binding.item = item
            binding.model = model
            binding.lifecycleOwner = owner
        }
    }

    private fun RecyclerView.config() {
        ContextCompat.getDrawable(requireContext(), R.drawable.div)?.let {
            val padding = resources.getDimensionPixelOffset(R.dimen.grid_2)
            addItemDecoration(DividerItemDecoration(it, padding = padding))
        }
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}