package com.drugstore.app.core

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import com.drugstore.CoreGraphDirections
import com.drugstore.R
import com.drugstore.data.repository.exception.InvalidOperationOutputException
import com.drugstore.data.repository.exception.OperationException
import com.drugstore.data.repository.exception.OperationValidationException
import com.drugstore.data.repository.exception.UnsuccessfulOperationException
import com.google.android.material.textfield.TextInputLayout
import com.kasprov.android.core.lifecycle.Event
import com.kasprov.android.core.lifecycle.set
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

inline fun Exception.handle(
    navigateTo: MutableLiveData<Event<NavDirections>?>,
    noinline onOperationValidationException: (OperationValidationException.() -> Unit)? = null
) = when (this) {
    is InvalidOperationOutputException -> navigateTo.set(toMessageDestination())
    is OperationValidationException -> if (onOperationValidationException == null || errors.isNullOrEmpty()) {
        navigateTo.set(toMessageDestination())
    } else {
        onOperationValidationException()
    }
    is OperationException -> navigateTo.set(toMessageDestination())
    is UnsuccessfulOperationException -> navigateTo.set(toMessageDestination())
    is ConnectException -> navigateTo.set(toMessageDestination())
    is UnknownHostException -> navigateTo.set(toMessageDestination())
    is SocketTimeoutException -> navigateTo.set(toMessageDestination())
    else -> navigateTo.set(toMessageDestination())
}

fun InvalidOperationOutputException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    messageResId = R.string.message_invalid_operation_output_message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun OperationException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    message = if (code != null) "$code: $message" else message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun OperationValidationException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    message = message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun UnsuccessfulOperationException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    message = "HTTP $code $message",
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun ConnectException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    messageResId = R.string.message_connect_exception_message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun UnknownHostException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    messageResId = R.string.message_unknown_host_exception_message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun SocketTimeoutException.toMessageDestination() = CoreGraphDirections.actionToMessage(
    messageResId = R.string.message_socket_timeout_exception_message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun Exception.toMessageDestination() = CoreGraphDirections.actionToMessage(
    message = message,
    buttonTextResId = R.string.message_exception_button,
    cancelable = false
)

fun <K, V> Map<K, V>.findFirstContainedKey(keys: Collection<K>) = keys.find { contains(it) }

fun <K> Map<K, TextInputLayout?>.setErrorsFrom(errorMap: Map<K, String?>) =
    forEach { it.value?.error = errorMap[it.key] }

inline fun <K> Map<K, TextInputLayout?>.doAfterTextChanged(crossinline action: (key: K) -> Unit) =
    forEach { it.value?.editText?.doAfterTextChanged { _ -> action(it.key) } }

fun Context.isBiometricAvailable() =
    BiometricManager.from(this).canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS