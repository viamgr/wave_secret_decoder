package com.example.wavedecoder.library

import kotlinx.coroutines.flow.*

/**
 * Convert sequence of an integer to a human readable string
 */
fun Flow<Int>.convertToString(): Flow<String> = flow {
    var result = ""
    map {
        result += it.toChar()
    }.onCompletion {
        emit(result)
    }.collect()
}