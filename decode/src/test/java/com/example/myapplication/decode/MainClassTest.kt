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
        val file = getFileFromPath("file_1.wav") ?: error("file not found")

        //01010101
        //1111
        //0->-6546546546546
        //1->897892612161656


        val byteReaderImpl = ByteReaderImpl(file)
        byteReaderImpl.read()
            .take(200)
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
            .drop(1)
            .withIndex()
            .filter {
                it.index % 14 == 0
            }
            .collect {
                println("${it}")
            }


    }

}