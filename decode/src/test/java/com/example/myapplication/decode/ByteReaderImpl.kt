package com.example.myapplication.decode

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import java.io.BufferedInputStream
import java.io.File

@Suppress("BlockingMethodInNonBlockingContext")
class ByteReaderImpl(private val file: File) : ByteReader {
    private var buffered: BufferedInputStream = file.inputStream().buffered()

    init {
        buffered.skip(48)
    }

    override fun read(): Flow<ByteArray> = flow {
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