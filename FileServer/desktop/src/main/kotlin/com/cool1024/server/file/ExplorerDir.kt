package com.cool1024.server.file

import java.io.File
import java.nio.file.Path

class ExplorerDir(parent: ExplorerDir?, private val name: String) {

    val path: String

    val children: List<Magic>
        get() = File(path).listFiles()?.map { file -> Magic(this, file) } ?: emptyList()

    init {
        path = parent?.let {
            Path.of(it.path, name).toString()
        } ?: name
    }
}