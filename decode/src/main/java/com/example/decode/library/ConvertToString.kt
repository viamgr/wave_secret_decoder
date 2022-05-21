package com.example.decode.library

import kotlinx.coroutines.flow.*

/**
 * Convert sequence of an integer to a human readable string
 */
fun Flow<Int>.convertToString(): Flow<String> = flow {
    var result = ""
    map {
        result += it.toChar()
    }.onCompletion {
        println(result)
        emit(result)
    }.collect()
}