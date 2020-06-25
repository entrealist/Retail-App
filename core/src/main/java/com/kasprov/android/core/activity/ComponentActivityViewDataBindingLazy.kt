package com.kasprov.android.core.activity

import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

internal class ComponentActivityViewDataBindingLazy<B : ViewDataBinding>(
    private val activity: ComponentActivity,
    private val bindingProducer: () -> B
) : Lazy<B> {
    private var binding: B? = null

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                binding?.unbind()
            }
        })
    }

    override val value: B
        get() = binding ?: bindingProducer().also {
            it.lifecycleOwner = activity
            binding = it
        }

    override fun isInitialized() = binding != null
}