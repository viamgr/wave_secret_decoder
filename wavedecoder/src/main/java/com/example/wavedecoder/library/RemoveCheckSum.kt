package com.example.wavedecoder.library

import com.example.flowutils.batch
import com.example.wavedecoder.DATA_SIZE_WITH_CHECKSUM
import com.example.wavedecoder.MODULO_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform

/**
 * The checksums will help you detect whether or not your decoding works. The algorithm is a
 * simple sum modulo 256
 */
fun Flow<Int>.removeCheckSum(
    batchSize: Int = DATA_SIZE_WITH_CHECKSUM,
    moduloSize: Int = MODULO_SIZE
): Flow<Int> {
    return batch(batchSize)
        .transform { list: List<Int> ->
            val data = list.subList(0, batchSize - 1)
            val checksum = list.last()
            if (data.sum() % moduloSize == checksum) {
                emitAll(data.asFlow())
            }
        }
}