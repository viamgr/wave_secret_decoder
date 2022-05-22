package com.example.wavedecoder

import com.example.wavedecoder.library.readWaveFileBytes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ReadWaveFileBytesTest {

    @Test
    fun getFileFromPath_file1_workFine() = runBlocking {
        val result = getFileByFileName("test_file.wav")!!
            .readWaveFileBytes().first().toList()
        Assert.assertEquals(1, result.first().toInt())
        Assert.assertEquals(-64, result[1].toInt())
    }


}


