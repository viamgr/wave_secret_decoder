package com.example.decode

import com.example.decode.library.detectSoundHeight
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class DetectSoundHeightTest {
    @Test
    fun detectSoundHeight_randomValues_worksFine() = runBlocking {
        val result =
            flowOf(byteArrayOf(1, 1), byteArrayOf(1, 1)).detectSoundHeight()
                .first()
        Assert.assertEquals((257).toShort(), result)
    }

    @Test
    fun detectSoundHeight_randomValues1_worksFine() = runBlocking {
        val result =
            flowOf(byteArrayOf(1, 2), byteArrayOf(1, 3)).detectSoundHeight()
                .first()
        Assert.assertEquals((769).toShort(), result)
    }
}


