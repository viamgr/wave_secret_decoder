package com.example.myapplication.decode

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainClassTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        val file = getFileFromPath("file_2.wav") ?: error("file not found")

        val byteReaderImpl = ByteReaderImpl(file)
        byteReaderImpl.read()
            .detectSoundHeight()
            .detectFrequencies()
            .convertFrequenciesToBinary()
            .convertToOrderedBytes()
            .convertToString()
            .findSecret()
            .first()
            .also {
                it.map {
                    print(it.toChar())
                }
            }
        println()
        println("Done")

    }


}

private fun Flow<List<Boolean>>.convertToString(): Flow<Int> {
    return map { list ->
        list.joinToString("") { if (it) "1" else "0" }
    }
        .map {
            Integer.parseInt(it, 2)
        }
}

private fun Flow<Boolean>.convertToOrderedBytes(): Flow<List<Boolean>> {
    return batch(11)
        .map {
            it.subList(1, 9).reversed()
        }
}


private fun Flow<Short>.detectFrequencies(): Flow<Boolean> {
    return batch(14)
        .map {
            it.sum() > 0
        }
}

private fun Flow<ByteArray>.detectSoundHeight(): Flow<Short> {
    return withIndex()
        .filter {
            it.index % 2 == 1
        }
        .map {
            ByteBuffer.wrap(it.value).order(ByteOrder.LITTLE_ENDIAN).short
        }
}

fun Flow<Int>.findSecret(): Flow<List<Int>> = flow {
    var counter = 0
    var leaderCount = 0
    var endBlockCount = 0
    var leaderStep = true
    var checkIdStep = false
    var oldCheckIdStep = 0
    var dataStep = false
    val buffer = mutableListOf<Int>()
    var zeroStep = false
    var endBlockStep = false
    var dataCounter = 0
    collect {

        if (leaderStep && it.toString(16) == "ff") {
            leaderCount++
            if (leaderCount == 652) {
                leaderStep = false
                checkIdStep = true
            }
        } else if (checkIdStep) {
            if (it.toString(16) == "42" && oldCheckIdStep == 0) {
                oldCheckIdStep = counter
            } else if (it.toString(16) == "3" && oldCheckIdStep == counter - 1) {
                checkIdStep = false
                dataStep = true
            } else {
                checkIdStep = false
            }
        } else if (dataStep) {
            if (buffer.size <= 1984) {
                if (dataCounter % 31 == 30) {

                    val subList = buffer.subList(buffer.size - 30, buffer.size)
                    val dataCheckSum = subList.sum() % 256

                    val isValid = it == dataCheckSum
                    if (!isValid) {
                        buffer.removeAll(subList)
                    }

                } else {
                    buffer.add(it)
                }
                dataCounter++
                if (dataCounter == 1984) {
                    dataStep = false
                    zeroStep = true
                }
            } else {
                // TODO: validation
                buffer.clear()
                dataStep = false
                zeroStep = true

            }
        } else if (zeroStep && it.toString(16) == "0") {
            zeroStep = false
            endBlockStep = true
        } else if (endBlockStep && it.toString(16) == "ff") {

            if (endBlockCount == 130 - 2) {
                emit(buffer)
            }
            endBlockCount++
        }
        counter++
    }
}

fun Flow<Boolean>.convertFrequenciesToBinary(): Flow<Boolean> = flow {
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

