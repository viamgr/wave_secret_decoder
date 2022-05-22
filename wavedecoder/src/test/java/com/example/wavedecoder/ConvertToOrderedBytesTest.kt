package com.example.wavedecoder

import com.example.wavedecoder.library.convertToOrderedBytes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConvertToOrderedBytesTest {
    @Test
    fun convertToOrderedBytes_onRandom_reverseOndOrderCorrectly() = runBlocking {
        val result = flowOf(false, false, true, false, true, false, true, false, true, false, false)
            .convertToOrderedBytes()
            .first()
        Assert.assertEquals(listOf(true, false, true, false, true, false, true, false), result)
    }

    @Test
    fun convertToOrderedBytes_onRandom1_reverseOndOrderCorrectly() = runBlocking {
        val result =
            flowOf(false, false, true, true, true, false, false, false, false, false, false)
                .convertToOrderedBytes()
                .first()
        Assert.assertEquals(listOf(false, false, false, false, true, true, true, false), result)
    }
}


