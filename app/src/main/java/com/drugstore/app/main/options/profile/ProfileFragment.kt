package com.drugstore.app.main.options.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.drugstore.R
import com.drugstore.databinding.FragmentProfileBinding
import com.kasprov.android.core.fragment.activityToolbar
import com.kasprov.android.core.fragment.navigateTo
import com.kasprov.android.core.fragment.observeEvent
import com.kasprov.android.core.fragment.viewDataBindings
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    var binding: FragmentProfileBinding by viewDataBindings()
    val model: ProfileVM by viewModels()
    val toolbar by activityToolbar()
    var initialToolbarElevation: Float? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        initialToolbarElevation = toolbar?.elevation
        toolbar?.elevation = 0f

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.model = model
        binding.executePendingBindings()

        observeEvent(model.onNavigateTo) { navigateTo(it) }
    }

    override fun onStart() {
        super.onStart()
        model.onStart()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.profile, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        initialToolbarElevation?.let { toolbar?.elevation = it }
    }
}