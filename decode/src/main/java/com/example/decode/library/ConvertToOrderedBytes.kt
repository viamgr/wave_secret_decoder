package com.example.decode.library

import com.example.decode.utils.batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Remove extra values from the digital sequences of boolean array and reverse it for example
 * false, false, true, false, true, false, true, false, true, false, false -> true, false, true, false, true, false, true, false
 */
fun Flow<Boolean>.convertToOrderedBytes(): Flow<List<Boolean>> {
    return batch(11)
        .map {
            it.subList(1, 9).reversed()
        }
}