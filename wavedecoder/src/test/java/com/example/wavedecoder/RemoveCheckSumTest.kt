package com.example.wavedecoder

import com.example.wavedecoder.library.removeCheckSum
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class RemoveCheckSumTest {

    @Test
    fun removeCheckSum_onValidSequence_verifySuccessfully() = runBlocking {
        val value = arrayListOf(8, 0).asFlow()
        val result = value.removeCheckSum(2, 2).toList()
        Assert.assertEquals(listOf(8), result)
    }

    @Test
    fun removeCheckSum_onValidSequence1_verifySuccessfully() = runBlocking {
        val value = arrayListOf(8, 0, 8, 0).asFlow()
        val result = value.removeCheckSum(2, 2).toList()
        Assert.assertEquals(listOf(8, 8), result)
    }

    @Test
    fun removeCheckSum_onValidSequence2_verifySuccessfully() = runBlocking {
        val value = arrayListOf(4, 0, 8, 0).asFlow()
        val result = value.removeCheckSum(2, 2).toList()
        Assert.assertEquals(listOf(4, 8), result)
    }

    @Test
    fun removeCheckSum_onValidSequence3_verifySuccessfully() = runBlocking {
        val value = arrayListOf(4, 8, 4).asFlow()
        val result = value.removeCheckSum(3, 8).toList()
        Assert.assertEquals(listOf(4, 8), result)
    }
}


