package com.example.decode.utils

import kotlinx.coroutines.flow.*


fun Flow<Int>.skipOccurrenceSequenceWithData(count: Int, callback: (data: List<Int>) -> Unit): Flow<Int> =
    flow {
        val buffer = mutableListOf<Int>()
        var loopCounter = 0
        collect {
            loopCounter++
            when {
                loopCounter == count -> {
                    buffer.add(it)
                    mutableListOf<Int>().apply { // copy the list and clears the cache
                        addAll(buffer)
                        buffer.clear()
                    }.also(callback)
                }
                loopCounter > count -> {
                    emit(it)
                }
                else -> {
                    buffer.add(it)
                }
            }
        }
    }

/**
 * Maps the Flow<T> to Flow<List<T>>. The list size is at least [batchSize]
 */
fun <T> Flow<T>.batch(batchSize: Int = 10): Flow<List<T>> {
    val cache: MutableList<T> = mutableListOf()

    return map {
        cache.apply { add(it) }
    }.filter { it.size >= batchSize }
        .map {
            mutableListOf<T>().apply { // copy the list and clears the cache
                addAll(cache)
                cache.clear()
            }
        }
}

fun Flow<Int>.skipOccurrenceSequence(
    occurrence: Int,
    count: Int,
    stopOnFind: Boolean = false,
): Flow<Int> {
    var loopCounter = 0
    var foundCounter = 0
    var stop = false
    return takeWhile {
        !stop
    }
        .transform {
            if (it == occurrence && foundCounter < count) {
                foundCounter++
            }
            loopCounter++
            val reachedEnd = foundCounter == count - 1 && stopOnFind
            val hasWrongSequence = loopCounter != foundCounter && foundCounter < count

            if (hasWrongSequence || reachedEnd) {
                stop = true
            } else if (loopCounter > foundCounter) {
                emit(it)
            }
        }
}