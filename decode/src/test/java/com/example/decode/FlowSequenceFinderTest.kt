package com.example.decode

import com.example.decode.utils.skipOccurrenceSequence
import com.example.decode.utils.skipOccurrenceSequenceWithData
import kotlinx.coroutines.FlowPreview
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
    fun skipOccurrenceSequence_middleRange_shouldReturnZero() = runBlocking {
        val result = flowOf(4, 5, 1, 1, 2)
            .skipOccurrenceSequence(1, 2)
            .toList()
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun skipOccurrenceSequence_withOneItem_shouldSkip() = runBlocking {
        val result = flowOf(1)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(0, result.size)
    }

    @Test
    fun skipOccurrenceSequence_withTwoItemFirstOccurrence_shouldReturnsSecond() = runBlocking {
        val result = flowOf(1, 2)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(2, result.first())
    }

    @Test
    fun skipOccurrenceSequence_nestedSkip_shouldWorks() = runBlocking {
        val result = flowOf(1, 1, 1, 2)
            .skipOccurrenceSequence(1, 1)
            .skipOccurrenceSequence(1, 1)
            .skipOccurrenceSequence(1, 1)
            .toList()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals(2, result.first())
    }

    @Test
    fun skipOccurrenceSequenceWithData_nestedSkipWithSameItems_shouldWorks() = runBlocking {
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
    fun skipOccurrenceSequenceWithData_nestedSkipWithDifferentItems_shouldWorks() = runBlocking {
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


}


