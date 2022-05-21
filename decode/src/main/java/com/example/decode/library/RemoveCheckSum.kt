package com.example.decode.library

import com.example.decode.utils.batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform

/**
 * The checksums will help you detect whether or not your decoding works. The algorithm is a
 * simple sum modulo 256
 */
fun Flow<Int>.removeCheckSum(): Flow<Int> {
    return batch(31)
        .transform { list: List<Int> ->
            val data = list.subList(0, 30)
            val checksum = list.last()
            if (data.sum() % 256 == checksum) {
                emitAll(data.asFlow())
            }
        }
}