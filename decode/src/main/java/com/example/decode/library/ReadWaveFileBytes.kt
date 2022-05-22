package com.example.decode.library

import com.example.decode.BUFFER_SIZE
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.io.BufferedInputStream
import java.io.File

/**
 * Read and divide bytes with a specific size
 */
@Suppress("BlockingMethodInNonBlockingContext")
fun File.readWaveFileBytes(bufferSize: Int = BUFFER_SIZE): Flow<ByteArray> = flow {
    val buffered: BufferedInputStream = inputStream().buffered()
    buffered.skip(48)

    buffered.use { inputStream ->
        while (currentCoroutineContext().isActive) {
            val buffer = ByteArray(bufferSize)
            val sz = inputStream.read(buffer)
            if (sz > 0)
                emit(buffer)
        }
    }
}