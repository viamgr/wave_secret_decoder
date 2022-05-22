package com.example.flowutils

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class FlowSequenceFinderTest {
    @Test
    fun skipOccurrenceSequence_onStopFind_stopsOnFind() = runBlocking {
        val result = flowOf(1, 1, 1, 1, 2)
            .skipOccurrenceSequence(1, 2, true)
            .toList()
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun skipOccurrenceSequence_skipFirst_shouldSkip() = runBlocking {
        val result = flowOf(1, 1, 1, 1, 2)
            .skipOccurrenceSequence(1, 2)
            .toList()
        Assert.assertEquals(listOf(1, 1, 2), result)
    }

    @Test
    fun skipOccurrenceSequence_middleRange_returnZero() = runBlocking {
        val result = flowOf(4, 5, 1, 1, 2)
            .skipOccurrenceSequence(1, 2)
            .toList()
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun skipOccurrenceSequence_withOneItem_skip() = runBlocking {
        val result = flowOf(1)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun skipOccurrenceSequence_withTwoItemFirstOccurrence_returnsSecond() = runBlocking {
        val result = flowOf(1, 2)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(2, result.first())
    }

    @Test
    fun skipOccurrenceSequence_nestedSkip_works() = runBlocking {
        val result = flowOf(1, 1, 1, 2)
            .skipOccurrenceSequence(1, 1)
            .skipOccurrenceSequence(1, 1)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(2, result.first())
    }

    @Test
    fun skipOccurrenceSequenceWithData_nestedSkipWithSameItems_work() = runBlocking {
        var callbackData: List<Int>? = null
        val callback = { data: List<Int> ->
            callbackData = data
        }
        val result = flowOf(1, 1, 1, 1)
            .skipOccurrenceSequenceWithData(1, callback)
            .toList()

        Assert.assertEquals(listOf(1, 1, 1), result)
        Assert.assertEquals(listOf(1), callbackData)
    }

    @Test
    fun skipOccurrenceSequenceWithData_nestedSkipWithDifferentItems_work() = runBlocking {
        var callbackData: List<Int>? = null
        val callback = { data: List<Int> ->
            callbackData = data
        }
        val result = flowOf(1, 2, 3, 4)
            .skipOccurrenceSequenceWithData(1, callback)
            .toList()

        Assert.assertEquals(listOf(2, 3, 4), result)
        Assert.assertEquals(listOf(1), callbackData)
    }

    @Test
    fun batch_batchWithOne_groupingToOneSliceSize() = runBlocking {
        val result = flowOf(1, 2, 3)
            .batch(1)
            .toList()
        Assert.assertEquals(listOf(listOf(1), listOf(2), listOf(3)), result)
    }


    @Test
    fun batch_batchWithSizeTwoAndEvenItems_groupOneSlice() = runBlocking {
        val result = flowOf(1, 2, 3)
            .batch(2)
            .toList()
        Assert.assertEquals(listOf(listOf(1, 2)), result)
    }

    @Test
    fun batch_batchWithSizeTwoAndOddItems_groupTwoSlice() = runBlocking {
        val result = flowOf(1, 2, 3, 4)
            .batch(2)
            .toList()
        Assert.assertEquals(listOf(listOf(1, 2), listOf(3, 4)), result)
    }

}


