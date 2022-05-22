package com.example.wavedecoder

import com.example.wavedecoder.library.convertToString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConvertToStringTest {
    @Test
    fun convertToString_onHello_workCorrectly() = runBlocking {
        val result = flowOf(72 ,69 ,76, 76, 79)
            .convertToString()
            .first()
        Assert.assertEquals("HELLO", result)
    }

    @Test
    fun convertToString_onRandomValue_workCorrectly() = runBlocking {
        val result = flowOf(45, 85, 55, 33, 44, 98)
            .convertToString()
            .first()
        Assert.assertEquals("-U7!,b", result)
    }

}


