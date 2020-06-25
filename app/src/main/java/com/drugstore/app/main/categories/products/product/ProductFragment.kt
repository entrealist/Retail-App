package com.drugstore.app.main.categories.products.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.ConstraintSet.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.ChangeImageTransform
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet.ORDERING_TOGETHER
import androidx.viewpager2.widget.ViewPager2
import com.drugstore.R
import com.drugstore.app.main.MainActivityVM
import com.drugstore.databinding.FragmentProductBinding
import com.drugstore.databinding.ItemProductImageBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.kasprov.android.core.fragment.*
import com.kasprov.android.core.recyclerview.adapter.SimpleAdapter
import com.kasprov.android.core.recyclerview.adapter.ViewDataBindingViewBinder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : Fragment() {

    var binding: FragmentProductBinding by viewDataBindings()
    val parentModel: MainActivityVM by activityViewModels()
    val model: ProductVM by viewModels()
    var tabLayoutMediator: TabLayoutMediator? = null

    private val toggleImageTransition by lazy {
        AutoTransition().apply {
            addTransition(ChangeImageTransform())
            ordering = ORDERING_TOGETHER
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        for (request in ProductVM.FragmentRequest.values()) {
            setFragmentResultListener(request.key) { _, result ->
                model.onFragmentResult(request, result)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        val productImagesAdapter = SimpleAdapter(ProductImageViewBinder(model)).apply {
            stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.vpProduct.config(productImagesAdapter)

        observeNonNull(model.product) { productImagesAdapter.submitList(it.childrenImageUris)}
        observeNonNull(model.isImageEnlarged) { if (it) enlargeImage() else minimizeImage() }
        observeEvent(model.onNavigateTo) { navigateTo(it) }
        observeEvent(model.onNavigateUp) { navigateUp() }
        observeEvent(parentModel.onRegionChanged) {
            val navController = findNavController()
            navController.popBackStack(navController.graph.startDestination, false)
        }

        addOnBackPressedCallback { model.onBackPressed() }
    }

    private class ProductImageViewBinder(
        private val model: ProductVM
    ) : ViewDataBindingViewBinder<String, ItemProductImageBinding>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem

        override fun createBinding(inflater: LayoutInflater, parent: ViewGroup, viewType: Int) =
            ItemProductImageBinding.inflate(inflater, parent, false)

        override fun bind(binding: ItemProductImageBinding, item: String) {
            binding.item = item
            binding.model = model
        }
    }

    private fun ViewPager2.config(adapter: ListAdapter<*, *>) {
        this.adapter = adapter

        tabLayoutMediator = TabLayoutMediator(binding.tlProduct, this) { _, _ -> }
        tabLayoutMediator?.attach()
    }

    private fun enlargeImage() = binding.clProductRoot.postOnAnimation {
        ConstraintSet().apply {
            clone(binding.clProductRoot)
            connect(R.id.vp_product, BOTTOM, PARENT_ID, BOTTOM)
            TransitionManager.beginDelayedTransition(binding.clProductRoot, toggleImageTransition)
            applyTo(binding.clProductRoot)
            binding.vProductEnlargedImagesBg.visibility = VISIBLE
        }
    }

    private fun minimizeImage() = binding.clProductRoot.postOnAnimation {
        ConstraintSet().apply {
            clone(binding.clProductRoot)
            connect(R.id.vp_product, BOTTOM, R.id.tl_product, TOP)
            TransitionManager.beginDelayedTransition(binding.clProductRoot, toggleImageTransition)
            applyTo(binding.clProductRoot)
            binding.vProductEnlargedImagesBg.visibility = GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayoutMediator?.detach()
        tabLayoutMediator = null
    }
}