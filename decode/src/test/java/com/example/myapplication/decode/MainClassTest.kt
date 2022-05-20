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
            .take(4000000)
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
            .map {
                it.subList(1, 9).reversed()
            }
            .map {
                it.map { if (it) "1" else "0" }.joinToString("")
            }
            .map {
                Integer.parseInt(it, 2).toString(16)
            }
            .findSecret()
            .collect {

                it.map {
                    print(it.toInt(16).toChar())
                }
//                val message = Integer.parseInt(it, 2)
//                print(message.toChar())
            }


    }


}

private fun hexToAscii(hexStr: String): String? {
    val output = StringBuilder("")
    var i = 0
    while (i < hexStr.length) {
        val str = hexStr.substring(i, i + 2)
        output.append(str.toInt(16).toChar())
        i += 2
    }
    return output.toString()
}

public fun Flow<String>.findSecret(): Flow<List<String>> = flow {
    var counter = 0
    var leaderCount = 0
    var endBlockCount = 0
    var leaderStep = true
    var checkIdStep = false
    var oldCheckIdStep = 0
    var dataStep = false
    val buffer = mutableListOf<String>()
    var zeroStep = false
    var endBlockStep = false
    collect {

        if (leaderStep && it == "ff") {
            leaderCount++
            if (leaderCount == 652) {
                leaderStep = false
                checkIdStep = true
            }
        } else if (checkIdStep) {
            if (it == "42" && oldCheckIdStep == 0) {
                oldCheckIdStep = counter
            } else if (it == "3" && oldCheckIdStep == counter - 1) {
                checkIdStep = false
                dataStep = true
            } else {
                checkIdStep = false
            }
        } else if (dataStep) {
            if (buffer.size <= 1984) {
                buffer.add(it)
                if (buffer.size == 1984) {
                    dataStep = false
                    zeroStep = true
                }
            } else {
                // TODO: validation
                buffer.clear()
                dataStep = false
                zeroStep = true

            }
        } else if (zeroStep && it == "0") {
            zeroStep = false
            endBlockStep = true
        } else if (endBlockStep && it == "ff") {

            if (endBlockCount == 130 - 2) {

                println("ffStep $endBlockCount")

                emit(buffer)
            }
            endBlockCount++
        }
        counter++
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

