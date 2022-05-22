package com.example.decode

import com.example.decode.library.readWaveFileBytes
import com.example.decode.utils.getFileFromPath
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class ReadWaveFileBytesTest {

    @Test
    fun getFileFromPath_file1_workFine() = runBlocking {
        val result = getFileFromPath("file_1.wav")!!
            .readWaveFileBytes().first().toList()
        Assert.assertEquals(1, result.first().toInt())
        Assert.assertEquals(-64, result[1].toInt())
    }

    @Test
    fun getFileFromPath_file2_workFine() = runBlocking {
        val result = getFileFromPath("file_2.wav")!!
            .readWaveFileBytes().first().toList()
        Assert.assertEquals(29, result.first().toInt())
        Assert.assertEquals(-7, result[1].toInt())
    }

    @Test
    fun getFileFromPath_file3_workFine() = runBlocking {
        val result = getFileFromPath("file_3.wav")!!
            .readWaveFileBytes().first().toList()
        Assert.assertEquals(-1, result.first().toInt())
        Assert.assertEquals(-1, result[1].toInt())
    }

}


