package com.example.wavedecoder.library

import com.example.flowutils.batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Bit-stream encoding, a single byte in the bit-stream
    0 b b b b b b b b 1 0 2 1 3 4 5 6 7 1
    START BIT - 1 Byte DATA (8 bit) -  2 STOP BITS
 * Remove extra values from the digital sequences of boolean array and reverse it for example
 * false, false, true, false, true, false, true, false, true, false, false -> true, false, true, false, true, false, true, false
 */
fun Flow<Boolean>.convertToOrderedBytes(): Flow<List<Boolean>> {
    return batch(11)
        .map {
            it.subList(1, 9).reversed()
        }
}