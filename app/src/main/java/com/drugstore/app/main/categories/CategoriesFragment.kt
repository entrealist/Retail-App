package com.drugstore.app.main.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.R
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.app.main.MainActivityVM
import com.drugstore.databinding.FragmentCategoriesBinding
import com.drugstore.databinding.ItemCategoryBinding
import com.drugstore.domain.entity.Category
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.recyclerview.itemdecoration.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    var binding: FragmentCategoriesBinding by viewDataBindings()
    val parentModel: MainActivityVM by activityViewModels()
    val model: CategoriesVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val categoriesAdapter = SimpleAdapter(CategoryViewBinder(model)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val categoriesFooterAdapter = SimpleAdapter(ActionItemViewBinder)
        binding.rvCategories.adapter = MergeAdapter(categoriesAdapter, categoriesFooterAdapter)
        binding.rvCategories.config()

        observeNonNull(model.categories) { categoriesAdapter.submitList(it) }
        observe(model.categoriesFooterItems) { categoriesFooterAdapter.submitList(it) }
        observeNonNull(model.isCategoriesRefreshingIndicatorVisible) { binding.swlCategories.isRefreshing = it }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(parentModel.onRegionChanged) { /* consume event */ }

        binding.swlCategories.setOnRefreshListener { model.refresh() }
    }

    private class CategoryViewBinder(
        private val model: CategoriesVM
    ): ViewDataBindingViewBinder<Category, ItemCategoryBinding>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemCategoryBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemCategoryBinding, item: Category) {
            binding.item = item
            binding.model = model
        }
    }

    private fun RecyclerView.config() {
        ContextCompat.getDrawable(requireContext(), R.drawable.div)?.let {
            addItemDecoration(DividerItemDecoration(it))
            addItemDecoration(DividerItemDecoration(it, LinearLayout.VERTICAL))
        }
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }
}