package com.kasprov.android.core.fragment

import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.annotation.StringRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.viewbinding.ViewBinding
import com.kasprov.android.core.activity.toolbar
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.OnDestroyClearedReadWriteProperty
import com.kasprov.android.core.lifecycle.observeEvent
import com.kasprov.android.core.lifecycle.observeNonNull
import kotlin.properties.ReadWriteProperty

fun <B : ViewBinding> Fragment.viewBindings(): ReadWriteProperty<Fragment, B> =
    FragmentViewBindingProperty(this)

fun <B : ViewBinding> DialogFragment.viewBindings(): ReadWriteProperty<DialogFragment, B> =
    OnDestroyClearedReadWriteProperty(this)

fun <B : ViewDataBinding> Fragment.viewDataBindings(): ReadWriteProperty<Fragment, B> =
    FragmentViewDataBindingProperty(this)

inline fun <T> Fragment.observe(
    liveData: LiveData<out T?>,
    owner: LifecycleOwner = viewLifecycleOwner,
    crossinline onChanged: (T?) -> Unit
) = liveData.observe(owner, Observer { onChanged(it) })

inline fun <T> DialogFragment.observe(
    liveData: LiveData<out T?>,
    crossinline onChanged: (T?) -> Unit
) = liveData.observe(this, Observer { onChanged(it) })

inline fun <T> Fragment.observeNonNull(
    liveData: LiveData<out T?>,
    owner: LifecycleOwner = viewLifecycleOwner,
    crossinline onChanged: (T) -> Unit
) = liveData.observeNonNull(owner, onChanged)

inline fun <T> DialogFragment.observeNonNull(
    liveData: LiveData<out T?>,
    crossinline onChanged: (T) -> Unit
) = liveData.observeNonNull(this, onChanged)

inline fun <T> Fragment.observeEvent(
    liveData: LiveData<out Event<T>?>,
    owner: LifecycleOwner = viewLifecycleOwner,
    crossinline onChanged: (T) -> Unit
) = liveData.observeEvent(owner, onChanged)

inline fun <T> DialogFragment.observeEvent(
    liveData: LiveData<out Event<T>?>,
    crossinline onChanged: (T) -> Unit
) = liveData.observeEvent(this, onChanged)

fun Fragment.navigateTo(directions: NavDirections) = findNavController(this).navigate(directions)

fun Fragment.navigateUp() = findNavController(this).navigateUp()

fun Fragment.addOnBackPressedCallback(
    owner: LifecycleOwner? = viewLifecycleOwner,
    enabled: Boolean = true,
    onBackPressed: OnBackPressedCallback.() -> Unit
) = requireActivity().onBackPressedDispatcher.addCallback(owner, enabled, onBackPressed)

fun DialogFragment.addOnBackPressedCallback(
    enabled: Boolean = true,
    onBackPressed: OnBackPressedCallback.() -> Unit
) = requireActivity().onBackPressedDispatcher.addCallback(this, enabled, onBackPressed)

fun Fragment.activityToolbar() = lazy { requireActivity().toolbar }

fun Fragment.showToast(@StringRes messageResId: Int) =
    Toast.makeText(requireContext(), messageResId, Toast.LENGTH_LONG).show()