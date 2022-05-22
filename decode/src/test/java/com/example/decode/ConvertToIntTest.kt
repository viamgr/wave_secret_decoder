package com.example.decode

import com.example.decode.library.convertToInt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ConvertToIntTest {
    @Test
    fun convertToInt_onFalse_returnZero() = runBlocking {
        val result = flowOf(listOf(false, false, false, false))
            .convertToInt()
            .first()
        Assert.assertEquals(0, result)
    }

    @Test
    fun convertToInt_onTrue_returnOne() = runBlocking {
        val result = flowOf(listOf(true))
            .convertToInt()
            .first()
        Assert.assertEquals(1, result)
    }

    @Test
    fun convertToInt_onDoubleTrue_return3() = runBlocking {
        val result = flowOf(listOf(true, true))
            .convertToInt()
            .first()
        Assert.assertEquals(3, result)
    }

    @Test
    fun convertToInt_onTripleTrue_return7() = runBlocking {
        val result = flowOf(listOf(true, true,true))
            .convertToInt()
            .first()
        Assert.assertEquals(7, result)
    }

    @Test
    fun convertToInt_onRandomValues_convertProperly() = runBlocking {
        val result = flowOf(listOf(true, false, true, false))
            .convertToInt()
            .first()
        Assert.assertEquals(10, result)
    }

}


