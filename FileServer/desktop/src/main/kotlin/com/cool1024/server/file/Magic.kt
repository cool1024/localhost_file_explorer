package com.cool1024.server.file

import java.io.File

class Magic(parent: ExplorerDir?, file: File) {

    val type: FileType

    init {
        type = if (!file.isDirectory) {
            when ("." + file.extension) {
                in IMAGE_EX_NAME -> FileType.IMAGE
                in PDF_EX_NAME -> FileType.PDF
                in VIDEO_EX_NAME -> FileType.VIDEO
                in MUSIC_EX_NAME -> FileType.MUSIC
                in DOCS_EX_NAME -> FileType.DOCS
                in LINK_EX_NAME -> FileType.LINK
                in ZIP_EX_NAME -> FileType.ZIP
                in TEXT_EX_NAME -> FileType.TEXT
                in EXCEL_EX_NAME -> FileType.EXCEL
                else -> FileType.OTHER
            }
        } else FileType.DIR
    }

    companion object {
        private val IMAGE_EX_NAME = arrayOf(".jpg", ".jpeg", ".gif", ".webp", ".png", ".bmp", ".svg")
        private val PDF_EX_NAME = arrayOf(".pdf")
        private val VIDEO_EX_NAME = arrayOf(".mp4", ".flv", ".wmv")
        private val MUSIC_EX_NAME = arrayOf(".mp3", ".wav", ".flac")
        private val ZIP_EX_NAME = arrayOf(".rar", ".zip", ".7z")
        private val DOCS_EX_NAME = arrayOf(".doc", ".docx")
        private val EXCEL_EX_NAME = arrayOf(".xls", ".xlsx")
        private val TEXT_EX_NAME = arrayOf(".md", ".html", ".text", ".txt", ".text")
        private val LINK_EX_NAME = arrayOf(".lnk")
    }
}