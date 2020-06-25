package com.kasprov.android.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

internal class OnDestroyClearedReadWriteProperty<T, V>(
    owner: LifecycleOwner
) : ReadWriteProperty<T, V> {
    private var value: V? = null

    init {
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                value = null
            }
        })
    }

    override fun getValue(thisRef: T, property: KProperty<*>) =
        value ?: throw IllegalStateException("$this accessed when it's not available")

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this.value = value
    }
}