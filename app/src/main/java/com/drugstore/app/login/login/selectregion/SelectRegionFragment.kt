package com.drugstore.app.login.login.selectregion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.MergeAdapter
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.R
import com.drugstore.app.core.item.ActionItem
import com.drugstore.app.core.item.LoadingItem
import com.drugstore.app.core.viewbinder.ActionItemViewBinder
import com.drugstore.app.core.viewbinder.LoadingItemViewBinder
import com.drugstore.databinding.FragmentSelectRegionBinding
import com.drugstore.databinding.ItemSelectRegionBinding
import com.drugstore.domain.entity.Region
import com.kasprov.android.core.activity.setNavigationBarColorResource
import com.kasprov.android.core.activity.setWindowLightNavigationBar
import com.kasprov.android.core.activity.toolbar
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.CompositeAdapter
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.recyclerview.itemdecoration.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectRegionFragment : Fragment() {

    var binding: FragmentSelectRegionBinding by viewDataBindings()
    val model: SelectRegionVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireActivity().apply {
            setNavigationBarColorResource(R.color.navigationBarColor_light)
            setWindowLightNavigationBar(true)
            toolbar?.visibility = View.GONE
        }

        binding = FragmentSelectRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val regionsAdapter = SimpleAdapter(RegionViewBinder(model)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        val regionsFooterAdapter = CompositeAdapter(
            LoadingItem::class.java to LoadingItemViewBinder(),
            ActionItem::class.java to ActionItemViewBinder
        )
        binding.rvSelectRegion.adapter = MergeAdapter(regionsAdapter, regionsFooterAdapter)
        binding.rvSelectRegion.config()

        observeNonNull(model.regions) { regionsAdapter.submitList(it) }
        observe(model.regionsFooterItems) { regionsFooterAdapter.submitList(it) }
        observeEvent(model.onConfigurationChanged) { requireActivity().recreate() }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }

        addOnBackPressedCallback { requireActivity().finish() }
    }

    private class RegionViewBinder(
        private val model: SelectRegionVM
    ): ViewDataBindingViewBinder<Region, ItemSelectRegionBinding>() {
        override fun areItemsTheSame(oldItem: Region, newItem: Region) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Region, newItem: Region) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemSelectRegionBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemSelectRegionBinding, item: Region) {
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
}