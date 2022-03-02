package com.cool1024.server.file

import java.io.File
import java.lang.RuntimeException

class DirReader(private val parentDir: DirReader?, val path: String) {

    fun scanDir(): List<FileItem> {

        val selfFile = File(parentDir?.path ?: "", path)
        val children = arrayListOf<FileItem>()

        if (!selfFile.isDirectory) {
            throw RuntimeException("此路径不是文件夹:$path")
        }

        selfFile.listFiles()?.forEach {
            children.add(FileItem.create(this, it))
        }

        return children
    }

    companion object {
        fun create(path: String): DirReader {
            return DirReader(null, path)
        }
    }
}