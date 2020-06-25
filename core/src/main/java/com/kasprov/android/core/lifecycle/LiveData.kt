package com.kasprov.android.core.lifecycle

import androidx.lifecycle.*

inline fun <T> LiveData<out T?>.observeNonNull(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) = observe(owner, Observer { it?.let(onChanged) })

inline fun <T> LiveData<out Event<T>?>.observeEvent(
    owner: LifecycleOwner,
    crossinline onChanged: (T) -> Unit
) = observe(owner, Observer { it?.contentIfNotHandled?.let(onChanged) })


inline fun <X, Y> LiveData<out X?>.mapNonNull(crossinline transform: (X) -> Y): LiveData<Y> =
    Transformations.map(this) { if (it != null) transform(it) else null }

inline fun <X, Y> LiveData<out X?>.switchMapNonNull(crossinline transform: (X) -> LiveData<Y>): LiveData<Y> =
    Transformations.switchMap(this) { if (it != null) transform(it) else MutableLiveData() }

inline fun <A, B, Result> LiveData<A>.combineNullable(
    other: LiveData<B>,
    crossinline combine: (A?, B?) -> Result
): LiveData<Result> = MediatorLiveData<Result>().apply {
    addSource(this@combineNullable) { value = combine(it, other.value) }
    addSource(other) { value = combine(this@combineNullable.value, it) }
}

inline fun <A, B, Result> LiveData<out A?>.combine(
    other: LiveData<out B?>,
    crossinline combine: (A, B) -> Result
): LiveData<Result> = MediatorLiveData<Result>().apply {
    addSource(this@combine) { a ->
        val b = other.value
        if (a != null && b != null) value = combine(a, b)
    }
    addSource(other) { b ->
        val a = this@combine.value
        if (a != null && b != null) value = combine(a, b)
    }
}

inline fun <A, B, C, Result> LiveData<out A?>.combine(
    other1: LiveData<out B?>,
    other2: LiveData<out C?>,
    crossinline combine: (A, B, C) -> Result
): LiveData<Result> = MediatorLiveData<Result>().apply {
    addSource(this@combine) { a ->
        val b = other1.value
        val c = other2.value
        if (a != null && b != null && c != null) value = combine(a, b, c)
    }
    addSource(other1) { b ->
        val a = this@combine.value
        val c = other2.value
        if (a != null && b != null && c != null) value = combine(a, b, c)
    }
    addSource(other2) { c ->
        val a = this@combine.value
        val b = other1.value
        if (a != null && b != null && c != null) value = combine(a, b, c)
    }
}


inline fun <T, S> MediatorLiveData<T>.addOneTimeSource(
    source: LiveData<out S?>,
    crossinline onChanged: (S?) -> Unit
) = addSource(source) {
    removeSource(source)
    onChanged(it)
}

inline fun <T, S> MediatorLiveData<T>.addNonNullSource(
    source: LiveData<out S?>,
    crossinline onChanged: (S) -> Unit
) = addSource(source) { it?.let(onChanged) }

inline fun <T, S> MediatorLiveData<T>.addOneTimeNonNullSource(
    source: LiveData<out S?>,
    crossinline onChanged: (S) -> Unit
) = addSource(source) {
    it?.let {
        removeSource(source)
        onChanged(it)
    }
}


fun MutableLiveData<Event<Any>?>.call() {
    value = Event(Unit)
}

fun MutableLiveData<Event<Any>?>.postCall() = postValue(Event(Unit))

fun <T> MutableLiveData<Event<T>?>.set(content: T) {
    value = Event(content)
}

fun <T> MutableLiveData<Event<T>?>.post(content: T) = postValue(Event(content))