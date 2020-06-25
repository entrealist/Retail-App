package com.drugstore.app.main.options.settings.notificaitons

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.drugstore.databinding.FragmentNotificationsBinding
import com.kasprov.android.core.fragment.viewDataBindings

class NotificationsFragment : Fragment() {

    var binding: FragmentNotificationsBinding by viewDataBindings()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }
}