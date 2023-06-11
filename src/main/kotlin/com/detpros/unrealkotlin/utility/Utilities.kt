package com.detpros.unrealkotlin.utility

import kotlinx.coroutines.*
import java.io.File
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 *  Utilities
 *
 * @author IvanEOD ( 5/24/2023 at 11:51 AM EST )
 */


inline fun <B, reified T: Any> B.addIfNotNull(
    element: T?,
    block: B.(T) -> B
): B = if (element != null) block(element) else this

inline fun <B, reified T: Any, M> B.addIfNotNull(
    element: T?,
    mapper: (T) -> M,
    block: B.(M) -> B
): B = if (element != null) block(mapper(element)) else this

inline fun <B, reified T> B.addIfNotEmpty(
    collection: Collection<T>,
    block: B.(Array<out T>) -> B
): B = if (collection.isNotEmpty()) block(collection.toTypedArray()) else this

@JvmName("addIfNotEmptyT")
inline fun <B, reified T, reified M> B.addIfNotEmpty(
    collection: Collection<T>,
    mapper: (T) -> M,
    block: B.(Array<out M>) -> B
): B = if (collection.isNotEmpty()) block(collection.map(mapper).toTypedArray()) else this

inline fun <B, reified T, reified M> B.addIfNotEmpty(
    collection: Collection<T>,
    mapper: (T) -> M,
    block: B.(Iterable<M>) -> B
): B = if (collection.isNotEmpty()) block(collection.map(mapper)) else this

inline fun <B, reified T> B.addFolding(
    collection: Collection<T>,
    block: B.(T) -> B
): B = if (collection.isEmpty()) this else collection.fold(this, block)

inline fun <B, reified T, M> B.addFolding(
    collection: Collection<T>,
    mapper: (T) -> M,
    block: B.(M) -> B
): B = if (collection.isEmpty()) this else collection.map(mapper).fold(this, block)

@JvmName("isSameAsT")
inline fun <reified T> Collection<T>.isSameAs(other: Collection<T>, predicate: (T, T) -> Boolean): Boolean {
    if (this.size != other.size) return false
    val exclude = mutableSetOf<T>()
    for (element in this) {
        val otherElement = other.firstOrNull { it !in exclude && predicate(element, it) } ?: return false
        exclude.add(otherElement)
    }
    return true
}

inline fun <reified T, reified U> Collection<T>.isSameAs(other: Collection<U>, predicate: (T, U) -> Boolean): Boolean {
    if (this.size != other.size) return false
    val exclude = mutableSetOf<U>()
    for (element in this) {
        val otherElement = other.firstOrNull { it !in exclude && predicate(element, it) } ?: return false
        exclude.add(otherElement)
    }
    return true
}

@JvmName("isSameAsT")
inline fun <T> T?.isSameAs(other: T?, block: (T, T) -> Boolean): Boolean {
    if (this == null && other == null) return true
    if (this == null || other == null) return false
    return block(this, other)
}

inline fun <T, U> T?.isSameAs(other: U?, block: (T, U) -> Boolean) : Boolean {
    if (this == null && other == null) return true
    if (this == null || other == null) return false
    return block(this, other)
}

inline fun <T> MutableCollection<T>.removeFirst(predicate: (T) -> Boolean = { true }) {
    val match = firstOrNull(predicate) ?: return
    remove(match)
}

fun File.safeResolveDir(name: String): File {
    val file = resolve(name)
    if (!file.exists()) file.mkdirs()
    return file
}

fun File.safeResolveFile(name: String): File {
    val file = resolve(name)
    if (!file.parentFile.exists()) file.parentFile.mkdirs()
    if (!file.exists()) file.createNewFile()
    return file
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> debounce(
    waitMs: Long = 300L,
    scope: CoroutineScope = GlobalScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var debounceJob: Job? = null
    return { param ->
        debounceJob?.cancel()
        debounceJob = scope.launch {
            delay(waitMs)
            destinationFunction(param)
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> throttleFirst(
    skipMs: Long = 300L,
    coroutineScope: CoroutineScope = GlobalScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    return { param: T ->
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction(param)
                delay(skipMs)
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun <T> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope = GlobalScope,
    destinationFunction: (T) -> Unit
): (T) -> Unit {
    var throttleJob: Job? = null
    var latestParam: T
    return { param: T ->
        latestParam = param
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}


@OptIn(ExperimentalContracts::class)
fun callOnce(
    block: () -> Unit
) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    block()
}