package com.example.wavedecoder.library

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Join boolean sequences and return integer value
 * false         -> 0
 * true          -> 1
 * true,true     -> 3
 */
fun Flow<List<Boolean>>.convertToInt(): Flow<Int> {
    return map { list ->
        list.joinToString("") { if (it) "1" else "0" }
    }
        .map {
            Integer.parseInt(it, 2)
        }
}