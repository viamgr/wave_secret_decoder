package com.example.decode.library

import com.example.decode.BINARY_SLICE_DATA_SIZE
import com.example.decode.RECTANGLE_THRESHOLD
import com.example.decode.utils.batch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Grouping short sequences into a specific size array of slices to calculate waveForm
 * rectangle sum weight
 */
fun Flow<Short>.detectFrequencies(
    batchSize: Int = BINARY_SLICE_DATA_SIZE,
    rectangleThreshold: Int = RECTANGLE_THRESHOLD
): Flow<Boolean> {
    return batch(batchSize)
        .map {
            it.sum() > -rectangleThreshold
        }
}