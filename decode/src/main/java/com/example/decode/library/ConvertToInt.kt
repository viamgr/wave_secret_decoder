package com.example.decode.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Join boolean sequences to a integer value
 */
fun Flow<List<Boolean>>.convertToInt(): Flow<Int> {
    return map { list ->
        list.joinToString("") { if (it) "1" else "0" }
    }
        .map {
            Integer.parseInt(it, 2)
        }
}