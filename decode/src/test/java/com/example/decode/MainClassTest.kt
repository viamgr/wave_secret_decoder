@file:OptIn(FlowPreview::class)

package com.example.decode

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MainClassTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        val file = getFileFromPath("file_3.wav") ?: error("file not found")

        file.readBytes()
            .detectSoundHeight()
            .detectFrequencies()
            .convertToBinary()
            .convertToOrderedBytes()
            .convertToInt()
            .getData()
            .removeCheckSum()
            .collect {
                print(it.toChar())
            }

        println()
        println("Done")

    }

}


