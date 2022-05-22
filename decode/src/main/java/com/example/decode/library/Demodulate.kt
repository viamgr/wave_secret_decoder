package com.example.decode.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Modulation system
 * “1“ ................... t = 320 µs
 * “0“ ................... 2t = 640 µs
 * Convert Boolean sequences to the digital values for example:
 * false, false, true, true, false, false, true, true -> 0000
 * 01010101 -> 10101010
 * 00100101 -> 010111
 * 00101100 -> 01100
 */
fun Flow<Boolean>.demodulate(): Flow<Boolean> = flow {
    var oldValue = false
    var isCycleFinished = false
    collect { value ->
        isCycleFinished = if (isCycleFinished) {
            val isSame = oldValue == value
            emit(!isSame)
            !isSame
        } else {
            !isCycleFinished
        }
        oldValue = value
    }
}