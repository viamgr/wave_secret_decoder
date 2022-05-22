package com.example.wavedecoder

import java.io.File

fun Any.getFileByFileName(fileName: String): File? {
    val classLoader = javaClass.classLoader
    val resource = classLoader.getResource(fileName)
    return if (resource != null) {
        File(resource.path)
    } else null
}
