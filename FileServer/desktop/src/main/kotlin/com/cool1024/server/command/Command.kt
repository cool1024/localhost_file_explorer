package com.cool1024.server.command

import java.io.BufferedReader
import java.io.InputStreamReader

object Command {

    fun exec(vararg args: String) {
        val processBuilder = ProcessBuilder(*args)
        val process = processBuilder.start()
        val msgStream = BufferedReader(InputStreamReader(process.inputStream))
        val errStream = BufferedReader(InputStreamReader(process.errorStream))
        var consoleLineStr = ""
        while (msgStream.readLine()?.also { consoleLineStr = it } != null) {
            println(consoleLineStr)
        }
        msgStream.close()
        while (errStream.readLine()?.also { consoleLineStr = it } != null) {
            println(consoleLineStr)
        }
        errStream.close()
        process.waitFor()
        process.destroy()
    }
}