package com.example.myapplication.decode

import java.io.File

class MainClass {
    fun main(file: File) {
        val byteReaderImpl = ByteReaderImpl(file)
    }

}