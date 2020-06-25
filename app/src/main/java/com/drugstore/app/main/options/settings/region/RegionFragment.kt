package com.drugstore.app.main.options.settings.region

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.drugstore.R
import com.drugstore.app.main.MainActivityVM
import com.drugstore.databinding.FragmentRegionBinding
import com.drugstore.databinding.ItemRegionBinding
import com.drugstore.domain.entity.Region
import com.kasprov.android.core.fragment.navigateTo
import com.kasprov.android.core.fragment.observeEvent
import com.kasprov.android.core.fragment.observeNonNull
import com.kasprov.android.core.fragment.viewDataBindings
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import com.kasprov.android.core.recyclerview.itemdecoration.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegionFragment : Fragment() {

    var binding: FragmentRegionBinding by viewDataBindings()
    val parentModel: MainActivityVM by activityViewModels()
    val model: RegionVM by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRegionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val regionsAdapter = SimpleAdapter(RegionViewBinder(model, viewLifecycleOwner)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.rvRegion.adapter = regionsAdapter
        binding.rvRegion.config()

        observeNonNull(model.regions) { regionsAdapter.submitList(it) }
        observeEvent(model.onConfigurationChanged) {
            parentModel.regionChanged()
            requireActivity().recreate()
        }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
    }

    private class RegionViewBinder(
        private val model: RegionVM,
        private val owner: LifecycleOwner
    ) : ViewDataBindingViewBinder<Region, ItemRegionBinding>() {
        override fun areItemsTheSame(oldItem: Region, newItem: Region) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Region, newItem: Region) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemRegionBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemRegionBinding, item: Region) {
            binding.item = item
            binding.model = model
            binding.lifecycleOwner = owner
        }
    }

    private fun RecyclerView.config() {
        ContextCompat.getDrawable(requireContext(), R.drawable.div)?.let {
            val paddingStart = resources.getDimensionPixelOffset(R.dimen.grid_2)
            addItemDecoration(DividerItemDecoration(it, paddingStart = paddingStart))
        }
    }
}