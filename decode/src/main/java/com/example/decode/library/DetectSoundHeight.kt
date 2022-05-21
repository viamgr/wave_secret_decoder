package com.example.decode.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.withIndex
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Retrieve sound weight value from the second part of the byteArray and convert it to a
 * readable value.
 */
fun Flow<ByteArray>.detectSoundHeight(): Flow<Short> {
    return withIndex()
        .filter {
            it.index % 2 == 1
        }
        .map {
            ByteBuffer.wrap(it.value).order(ByteOrder.LITTLE_ENDIAN).short
        }
}