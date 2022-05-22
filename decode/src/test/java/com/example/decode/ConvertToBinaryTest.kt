package com.example.decode

import com.example.decode.library.demodulate
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConvertToBinaryTest {
    @Test
    fun convertToBinary_ofTwoSameSequence_totallyFalse() = runBlocking {
        val result =
            flowOf(false, false, true, true, false, false, true, true)
                .demodulate()
                .toList()
        Assert.assertEquals(listOf(false, false, false, false), result)
    }

    @Test
    fun convertToBinary_random1_workFine() = runBlocking {
        val result =
            flowOf(false, true, true, false, false, false, false, true)
                .demodulate()
                .toList()
        Assert.assertEquals(listOf(true, false, false, false), result)
    }

    @Test
    fun convertToBinary_random2_workFine() = runBlocking {
        val result =
            flowOf(false, true, false, true, false, true, false, true)
                .demodulate()
                .toList()
        Assert.assertEquals(listOf(true, true, true, true, true, true, true), result)
    }


    @Test
    fun convertToBinary_invalidInput_returnFalseForAll() = runBlocking {
        val result =
            flowOf(false, false, false, false, false, false, false, false)
                .demodulate()
                .toList()
        Assert.assertEquals(listOf(false, false, false, false), result)
    }

}


