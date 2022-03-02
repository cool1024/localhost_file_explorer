package com.cool1024.server.command

import com.github.junrar.Junrar
import org.springframework.scheduling.annotation.Async
import org.zeroturnaround.zip.ZipUtil
import java.io.File

object UnZipCommand {

    const val COMMAND_NAME = "UNZIP"

    @Async
    fun exec(filePath: String) {
        val zipFile = File(filePath)
        if (zipFile.extension in arrayOf("rar", "RAR")) Junrar.extract(zipFile.absolutePath, zipFile.parent)
        else ZipUtil.explode(zipFile)
    }
}