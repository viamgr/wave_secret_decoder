package com.example.decode

import com.example.flowutils.skipOccurrenceSequence
import com.example.flowutils.skipOccurrenceSequenceWithData
import com.example.wavedecoder.library.*
import kotlinx.coroutines.flow.*
import java.io.File

val LEADER_VALUE = "ff".toInt(16)
val DIVIDER_VALUE = "0".toInt(16)
val ID_FIRST_VALUE = "42".toInt(16)
val ID_SECOND_VALUE = "3".toInt(16)

const val ID_BYTE_SIZE = 1
const val DIVIDER_SIZE = 1
const val LEADER_BYTE_SIZE = 652
const val MESSAGE_BYTE_SIZE = 1984
const val END_BLOCK_BYTE_SIZE = 130 - 1

/**
 * https://medium.com/poka-techblog/back-to-basics-decoding-audio-modems-with-audacity-c94faa8362a0
 */
fun File.findSecretMessage(): Flow<String> {
    return readWaveFileBytes()
        .detectSoundHeight()
        .detectFrequencies()
        .demodulate()
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
 *
 *     Message format, overall structure and checksum positions in byte-stream
 *     LEADER 652 BYTES -ID 2 BYTES - DATA (30+1) x 64 = 1984 BYTES - ZERO 1 BYTE - END BLOCK 130 BYTES
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