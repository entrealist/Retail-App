package com.drugstore.app.main.categories.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.R
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.app.main.MainActivityVM
import com.drugstore.databinding.FragmentProductsBinding
import com.drugstore.databinding.ItemProductBinding
import com.drugstore.domain.entity.Product
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.recyclerview.itemdecoration.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsFragment : Fragment() {

    var binding: FragmentProductsBinding by viewDataBindings()
    val parentModel: MainActivityVM by activityViewModels()
    val model: ProductsVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val productAdapter = SimpleAdapter(ProductViewBinder(model)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val footerAdapter = SimpleAdapter(ActionItemViewBinder)
        binding.rvProducts.adapter = MergeAdapter(productAdapter, footerAdapter)
        binding.rvProducts.config()

        observeNonNull(model.products) { productAdapter.submitList(it) }
        observe(model.productsFooterItems) { footerAdapter.submitList(it) }
        observeNonNull(model.isProductsRefreshingIndicatorVisible) { binding.swlProducts.isRefreshing = it }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(parentModel.onRegionChanged) {
            val navController = findNavController()
            navController.popBackStack(navController.graph.startDestination, false)
        }

        binding.swlProducts.setOnRefreshListener { model.refresh() }
    }

    private class ProductViewBinder(
        private val model: ProductsVM
    ): ViewDataBindingViewBinder<Product, ItemProductBinding>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemProductBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemProductBinding, item: Product) {
            binding.item = item
            binding.model = model
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