package com.example.decode

import com.example.decode.library.*
import com.example.decode.utils.skipOccurrenceSequence
import com.example.decode.utils.skipOccurrenceSequenceWithData
import kotlinx.coroutines.flow.*
import java.io.File

val LEADER_VALUE = "ff".toInt(16)
val DIVIDER_VALUE = "0".toInt(16)
val ID_FIRST_VALUE = "42".toInt(16)
val ID_SECOND_VALUE = "3".toInt(16)

const val BUFFER_SIZE = 2
const val ID_BYTE_SIZE = 1
const val DIVIDER_SIZE = 1
const val LEADER_BYTE_SIZE = 652
const val MESSAGE_BYTE_SIZE = 1984
const val END_BLOCK_BYTE_SIZE = 130 - 1
const val RECTANGLE_THRESHOLD = 100
const val BINARY_SLICE_DATA_SIZE = 14

/**
 * https://medium.com/poka-techblog/back-to-basics-decoding-audio-modems-with-audacity-c94faa8362a0
 * readBytes
 * detectSoundHeight
 * detectFrequencies
 * convertToBinary
 * convertToOrderedBytes
 * convertToInt
 * getData
 * removeCheckSum
 * convertToString
 */
fun File.findSecretMessage(): Flow<String> {
    return readBytes()
        .detectSoundHeight()
        .detectFrequencies()
        .convertToBinary()
        .convertToOrderedBytes()
        .convertToInt()
        .retrieveData()
        .removeCheckSum()
        .convertToString()
}

/**
 * • The bit-stream that can be extracted from the decoded audio signal can be converted into
bytes

 * The signal starts with a lead tone of roughly 2.5 seconds (all 1-bits, or 0xff bytes),
 * and ends with an end block of about 0.5 seconds (all 1-bits).
 *     11 bits are used to encode a single byte – 8 bits for the byte plus one start bit
 *     (valued 0) and two stop bits (valued 1).
 *     The data is encoded with least-significant bit first
 *
 * The byte-stream has the following form:
 *     The first two bytes are 0x42 and 0x03
 *     After that,the data is structured in 64 messages of 30 bytes each, with the 31st byte
 *     being the checksum of the 30 bytes before that (in total 1984 bytes = 64 * 31 bytes).
 *     The last byte before the end block is a 0x00 byte.
 */
fun Flow<Int>.retrieveData(): Flow<Int> {
    var buffer = listOf<Int>()
    return dropWhile {
        it != LEADER_VALUE
    }
        .skipOccurrenceSequence(LEADER_VALUE, LEADER_BYTE_SIZE)
        .skipOccurrenceSequence(ID_FIRST_VALUE, ID_BYTE_SIZE)
        .skipOccurrenceSequence(ID_SECOND_VALUE, ID_BYTE_SIZE)
        .skipOccurrenceSequenceWithData(MESSAGE_BYTE_SIZE) {
            buffer = it
        }
        .skipOccurrenceSequence(DIVIDER_VALUE, DIVIDER_SIZE)
        .skipOccurrenceSequence(LEADER_VALUE, END_BLOCK_BYTE_SIZE, stopOnFind = true)
        .onCompletion {
            emitAll(buffer.asFlow())
        }
}