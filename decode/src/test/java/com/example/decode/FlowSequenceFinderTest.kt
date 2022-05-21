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
    fun AAAAAAAAAAAAA() = runBlocking {
        println("test")
        Assert.assertEquals(2, 2)
        val result = flowOf(1, 1, 1, 1, 2)
            .skipOccurrenceSequence(1, 2)
            .skipOccurrenceSequence(1, 2, true)
            .toList()
        println(result)
        Assert.assertEquals(2, 2)

    }

    @Test
    fun BBBB() = runBlocking {
        println("asdasd")
        val result = flowOf(0, 1, 2, 3, 4)
            .skipOccurrenceSequenceWithData(4, callback = {
                println(it)
            })
            .toList()
        println(result)
        Assert.assertEquals(2, 2)

    }

}


