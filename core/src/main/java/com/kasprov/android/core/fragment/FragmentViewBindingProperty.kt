package com.kasprov.android.core.fragment

import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class FragmentViewBindingProperty<B : ViewBinding>(
    fragment: Fragment
) : ReadWriteProperty<Fragment, B> {
    private var binding: B? = null

    init {
        fragment.viewLifecycleOwnerLiveData.observe(fragment, Observer { viewLifecycleOwner ->
            viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    binding = null
                }
            })
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>) =
        binding ?: throw IllegalStateException("$this accessed when it's not available")

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: B) {
        binding = value
    }
}