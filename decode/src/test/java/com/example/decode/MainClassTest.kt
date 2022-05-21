@file:OptIn(FlowPreview::class)

package com.example.decode

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

class MainClassTest {
    @Test
    fun addition_isCorrect() = runBlocking {
        val file = getFileFromPath("file_3.wav") ?: error("Failed to read file")

        file.findSecretMessage().collect {
            print(it)
        }



        println("Done")

    }

}


