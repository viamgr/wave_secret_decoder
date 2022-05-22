package com.example.decode

import com.example.decode.library.detectFrequencies
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DetectFrequenciesTest {
    @Test
    fun detectFrequencies_sameValue_doubleTrueValues() = runBlocking {
        val result =
            flowOf(10.toShort(), 10.toShort())
                .detectFrequencies(1)
                .toList()
        Assert.assertEquals(listOf(true, true), result)
    }

    @Test
    fun detectFrequencies_differentRange_returnDifferentValues() = runBlocking {
        val result =
            flowOf(10.toShort(), (-10).toShort())
                .detectFrequencies(1, 1)
                .toList()
        Assert.assertEquals(listOf(true, false), result)
    }

    @Test
    fun detectFrequencies_irregularRange_workProperly() = runBlocking {
        val result =
            flowOf(
                10.toShort(),
                (-10).toShort(),
                (-50).toShort(),
                (1000).toShort(),
                (-1001).toShort()
            )
                .detectFrequencies(1, 1)
                .toList()
        Assert.assertEquals(listOf(true, false, false, true, false), result)
    }

    @Test
    fun detectFrequencies_setThreshold_workProperly() = runBlocking {
        val result =
            flowOf(
                15.toShort(),
                (-5).toShort(),
                (-15).toShort(),
                (5).toShort(),
                20.toShort(),
                (-200).toShort(),
                (-1).toShort(),
                (7).toShort()
            )
                .detectFrequencies(2, 100)
                .toList()
        Assert.assertEquals(listOf(true, true, false, true), result)
    }

    @Test
    fun detectFrequencies_setThresholdOne_workProperly() = runBlocking {
        val result =
            flowOf(
                15.toShort(),
                (-5).toShort(),
                (-15).toShort(),
                (5).toShort(),
                20.toShort(),
                (-200).toShort(),
                (-1).toShort(),
                (7).toShort()
            )
                .detectFrequencies(2, 1)
                .toList()
        Assert.assertEquals(listOf(true, false, false, true), result)
    }
}


