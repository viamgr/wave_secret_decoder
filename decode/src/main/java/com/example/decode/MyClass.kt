package com.example.decode

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import java.io.BufferedInputStream
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

val LEADER_VALUE = "ff".toInt(16)
val ID_FIRST_VALUE = "42".toInt(16)
val ID_SECOND_VALUE = "3".toInt(16)
val DIVIDER_VALUE = "0".toInt(16)
const val ID_BYTE_SIZE = 1

const val BUFFER_SIZE = 2
const val RECTANGLE_THRESHOLD = 100
const val BINARY_SLICE_DATA_SIZE = 14
const val DIVIDER_SIZE = 1
const val MESSAGE_BYTE_SIZE = 1984
const val LEADER_BYTE_SIZE = 652
const val END_BLOCK_BYTE_SIZE = 129

fun File.findSecretMessage(): Flow<String> {
    return readBytes()
        .detectSoundHeight()
        .detectFrequencies()
        .convertToBinary()
        .convertToOrderedBytes()
        .convertToInt()
        .getData()
        .removeCheckSum()
        .convertToString()
}

private fun Flow<Int>.convertToString(): Flow<String> = flow {
    var result = ""
    map {
        result += it.toChar()
    }.onCompletion {
        println(result)
        emit(result)
    }.collect()
}

fun Any.getFileFromPath(fileName: String): File? {
    val classLoader = javaClass.classLoader
    val resource = classLoader.getResource(fileName)
    return if (resource != null) {
        File(resource.path)
    } else null
}

@Suppress("BlockingMethodInNonBlockingContext")
fun File.readBytes(bufferSize: Int = BUFFER_SIZE): Flow<ByteArray> = flow {
    val buffered: BufferedInputStream = inputStream().buffered()
    buffered.skip(48)

    buffered.use { inputStream ->
        while (currentCoroutineContext().isActive) {
            val buffer = ByteArray(bufferSize)
            val sz = inputStream.read(buffer)
            if (sz > 0)
                emit(buffer)
        }
    }
}

fun Flow<Int>.removeCheckSum(): Flow<Int> {
    return batch(31)
        .transform { list: List<Int> ->
            val data = list.subList(0, 30)
            val checksum = list.last()
            if (data.sum() % 256 == checksum) {
                emitAll(data.asFlow())
            }
        }
}

fun Flow<List<Boolean>>.convertToInt(): Flow<Int> {
    return map { list ->
        list.joinToString("") { if (it) "1" else "0" }
    }
        .map {
            Integer.parseInt(it, 2)
        }
}

fun Flow<Boolean>.convertToOrderedBytes(): Flow<List<Boolean>> {
    return batch(11)
        .map {
            it.subList(1, 9).reversed()
        }
}


fun Flow<Short>.detectFrequencies(
    batchSize: Int = BINARY_SLICE_DATA_SIZE,
    rectangleThreshold: Int = RECTANGLE_THRESHOLD
): Flow<Boolean> {
    return batch(batchSize)
        .map {
            it.sum() > -rectangleThreshold
        }
}

fun Flow<ByteArray>.detectSoundHeight(): Flow<Short> {
    return withIndex()
        .filter {
            it.index % 2 == 1
        }
        .map {
            ByteBuffer.wrap(it.value).order(ByteOrder.LITTLE_ENDIAN).short
        }
}

fun Flow<Int>.getData(): Flow<Int> {
    var buffer = listOf<Int>()
    return dropWhile {
        it != LEADER_VALUE
    }
        .skipOccurrence(LEADER_VALUE, LEADER_BYTE_SIZE)
        .skipOccurrence(ID_FIRST_VALUE, ID_BYTE_SIZE)
        .skipOccurrence(ID_SECOND_VALUE, ID_BYTE_SIZE)
        .skipWithData(MESSAGE_BYTE_SIZE) {
            buffer = it
        }
        .skipOccurrence(DIVIDER_VALUE, DIVIDER_SIZE)
        .skipOccurrence(LEADER_VALUE, END_BLOCK_BYTE_SIZE, stopOnFind = true)
        .onCompletion {
            emitAll(buffer.asFlow())
        }
}

fun Flow<Int>.skipWithData(count: Int, callback: (data: List<Int>) -> Unit): Flow<Int> = flow {
    val buffer = mutableListOf<Int>()
    var loopCounter = 0
    collect {
        loopCounter++
        when {
            loopCounter == count -> {
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

fun Flow<Int>.skipOccurrence(
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


fun Flow<Boolean>.convertToBinary(): Flow<Boolean> = flow {
    var oldValue = false
    var isCycleFinished = false
    collect { value ->
        isCycleFinished = if (isCycleFinished) {
            val isSame = oldValue == value
            emit(!isSame)
            !isSame
        } else {
            !isCycleFinished
        }
        oldValue = value
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