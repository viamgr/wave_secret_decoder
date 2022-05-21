package com.example.decode.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Convert Boolean sequences to the digital values for example:
 * 00110011 -> 0000
 * 01010101 -> 10101010
 * 00100101 -> 010111
 * 00101100 -> 01100
 */
fun Flow<Boolean>.convertToBinary(): Flow<Boolean> = flow {
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