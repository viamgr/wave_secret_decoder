package com.example.decode

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.io.BufferedInputStream
import java.io.File

@Suppress("BlockingMethodInNonBlockingContext")
class FileReader(private val file: File) : ByteReader {

    override fun read(): Flow<ByteArray> = flow {
        val buffered: BufferedInputStream = file.inputStream().buffered()
        buffered.skip(48)

        buffered.use { inputStream ->
            while (currentCoroutineContext().isActive) {
                val buffer = ByteArray(2)
                val sz = inputStream.read(buffer)
                if (sz > 0)
                    emit(buffer)
            }
        }
    }
}