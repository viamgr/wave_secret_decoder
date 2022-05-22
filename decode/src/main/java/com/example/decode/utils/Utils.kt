package com.example.decode.utils

import java.io.File

fun Any.getFileFromPath(fileName: String): File? {
    val classLoader = javaClass.classLoader
    val resource = classLoader.getResource(fileName)
    return if (resource != null) {
        File(resource.path)
    } else null
}
