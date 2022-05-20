package com.example.myapplication.decode

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class MainClassTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        val file = getFileFromPath("file_2.wav") ?: error("file not found")

        //01010101
        //1111
        //0->-6546546546546
        //1->897892612161656
        /**
         *
        IndexedValue(index=0, value=-16383)         0
        IndexedValue(index=14, value=-16383)        0
        IndexedValue(index=28, value=16383)         1
        IndexedValue(index=42, value=16383)         1
        IndexedValue(index=56, value=-16383)        0
        IndexedValue(index=70, value=-16383)        0
        IndexedValue(index=84, value=16383)         1
        IndexedValue(index=98, value=16383)         1

        IndexedValue(index=14, value=-121099)
        IndexedValue(index=28, value=-207371)
        IndexedValue(index=42, value=17307)
        IndexedValue(index=56, value=186287)
        IndexedValue(index=70, value=-21586)
        IndexedValue(index=84, value=-187160)
        IndexedValue(index=98, value=21412)

        -121098
        -207370
        17306
        186289
        -21589
        -187158
        21409
        187125
        -21420
        -187124
        21419
        187123
        -21415

        001100110011 ->000000
        010100110011 ->110000


         */

        val byteReaderImpl = ByteReaderImpl(file)
        byteReaderImpl.read()
            .take(400000)
            .withIndex()
            .filter {
                it.index % 2 == 1
            }
            .map {
                ByteBuffer.wrap(it.value).order(ByteOrder.LITTLE_ENDIAN).short
            }
            .withIndex()
            .runningFold(0) { accumulator, value ->
                val sumAccumulator = if (value.index % 14 == 0) 0 else accumulator
                sumAccumulator + value.value
            }
            .withIndex()
            .filter {
                it.index != 0 && it.index % 14 == 0
            }
            .map {
                it.value >= 0
            }
            .waveToBinary()
            .batch(11)
            .collect {
                println(" collect: ${it.map { if (it) 1 else 0 }}")
            }


    }


}

public fun Flow<Boolean>.waveToBinary(): Flow<Boolean> = flow {
    var oldValue = false
    var allow = false
    collect { value ->
        allow = if (allow) {
            val isSame = oldValue == value
            emit(!isSame)
            !isSame
        } else {
            !allow
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

