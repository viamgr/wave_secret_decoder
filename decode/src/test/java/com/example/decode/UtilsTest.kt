package com.example.decode

import com.example.decode.utils.getFileFromPath
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Test

class UtilsTest {
    @Test
    fun getFileFromPath_onValidFile_returnFile() = runBlocking {
        val file = getFileFromPath("file_1.wav")!!
        Assert.assertEquals(2531868, file.inputStream().available())
    }

    @Test
    fun getFileFromPath_onInvalidFile_returnNull() = runBlocking {
        val file = getFileFromPath("invalidFile.wav")
        Assert.assertNull(file)
    }


}


