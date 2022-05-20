package com.example.myapplication.decode

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import java.io.File
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

        val byteReaderImpl = ByteReaderImpl(file)
        byteReaderImpl.read()
            .take(10)

            .collect {
                println(it)
            }


    }

}