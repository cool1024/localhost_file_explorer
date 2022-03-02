package com.cool1024.server

import com.cool1024.server.command.UnZipCommand
import com.cool1024.server.data.ApiData
import com.cool1024.server.data.FileData
import com.cool1024.server.data.Message
import com.cool1024.server.data.PageData
import com.cool1024.server.file.DirReader
import com.cool1024.server.file.FileItem
import com.cool1024.server.file.FileType
import com.cool1024.server.file.PreviewFile
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.FileInputStream
import java.lang.RuntimeException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin
class ApiController {

    @GetMapping("/dir")
    fun dir(@RequestParam(name = "dir", required = false) dirPath: String?): ApiData<Array<FileData>> {
        val scanDir = if (dirPath?.isNotBlank() == true)
            String(Base64.getUrlDecoder().decode(dirPath))
        else SpringBootApp.sScanPath
        val rows = DirReader.create(scanDir).scanDir().sortedBy { it.fileName }.map {
            val base64Path = String(Base64.getUrlEncoder().encode(it.path.toByteArray()))
            val previewFile = PreviewFile(it).apply { calculateSize() }
            FileData(
                fileName = it.fileName,
                filePath = base64Path,
                fileType = it.fileType.name,
                previewUrl = "http://${SpringBootApp.sHost}/preview?path=$base64Path&type=${it.fileType.name}",
                downloadUrl = "http://${SpringBootApp.sHost}/download?path=$base64Path&type=${it.fileType.name}",
                parentDir = String(Base64.getUrlEncoder().encode(it.dirReader.path.toByteArray())),
                previewSize = FileData.Size(previewFile.width, previewFile.height)
            )
        }.toTypedArray()
        return ApiData(true, Message.DEFAULT_SUCCESS, rows)
    }

    @GetMapping("/preview")
    fun preview(
        @RequestParam path: String,
        @RequestParam type: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val filePath = String(Base64.getUrlDecoder().decode(path))
        val previewFilePath = PreviewFile(FileItem.create(DirReader.create(""), File(filePath))).previewPath()
        if (previewFilePath.isNotEmpty()) {
            imageStream(previewFilePath, response)
        }
        response.flushBuffer()
    }

    @GetMapping("/download")
    fun download(
        @RequestParam path: String,
        @RequestParam type: String,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        val filePath = String(Base64.getUrlDecoder().decode(path))
        when (type) {
            FileType.VIDEO.name -> {
                videoStream(filePath, request, response)
            }
            else -> imageStream(filePath, response)
        }
        response.flushBuffer()
    }

    @GetMapping("/command")
    fun command(@RequestParam path: String, @RequestParam command: String): ApiData<String> {
        when (command) {
            UnZipCommand.COMMAND_NAME -> UnZipCommand.exec(path)
            else -> {
            }
        }
        return ApiData(result = true, message = "命令已发送", "")
    }


    private fun imageStream(imagePath: String, response: HttpServletResponse) {
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            throw RuntimeException("请求图片文件不存在:${imagePath}")
        }

        val filePath = Paths.get(imagePath)
        response.contentType = Files.probeContentType(filePath)
        response.setHeader("Content-Length", imageFile.length().toString())
        response.status = 200
        FileInputStream(imageFile).apply {
            copyTo(response.outputStream)
        }
    }

    private fun videoStream(videoPath: String, request: HttpServletRequest, response: HttpServletResponse) {

        val videoFile = File(videoPath)
        val range = request.getHeader("range")

        if (!videoFile.exists()) {
            throw RuntimeException("请求视频文件不存在:${videoPath}")
        }

        val filePath = Paths.get(videoPath)
        var startOffset = 0L
        val fileSize = videoFile.length()

        if (range != null && range.isNotEmpty()) {

            var ranges = range.split("=")

            if (ranges.size == 2) {
                ranges = ranges.last().split('-')
                startOffset = ranges.first().toLong()
            }

            response.contentType = Files.probeContentType(filePath)
            response.setHeader("Content-Range", "bytes " + startOffset + "-" + (fileSize - 1) + "/" + fileSize)
            response.setHeader("Content-Length", (fileSize - startOffset).toString())
            response.status = 206
        } else {
            response.contentType = Files.probeContentType(filePath)
            response.setHeader("Content-Length", fileSize.toString())
            response.status = 200
        }
        response.setHeader("Access-Control-Allow-Origin", "*")

        FileInputStream(videoFile).apply {
            skip(startOffset)
            copyTo(response.outputStream)
        }
    }
}
