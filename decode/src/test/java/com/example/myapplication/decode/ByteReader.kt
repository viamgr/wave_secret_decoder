package com.example.myapplication.decode

import kotlinx.coroutines.flow.Flow

interface ByteReader {
    fun read(): Flow<ByteArray>
}