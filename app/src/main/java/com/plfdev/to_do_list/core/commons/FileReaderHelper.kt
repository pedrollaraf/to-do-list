package com.plfdev.to_do_list.core.commons

internal object FileReaderHelper {
    fun readFileFromResources(fileName: String): String {
        val stream = javaClass.classLoader?.getResourceAsStream(fileName)
        return stream?.bufferedReader()?.readText() ?: ""
    }
}