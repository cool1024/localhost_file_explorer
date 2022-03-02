package com.cool1024.server.data

data class FileData(
    // 文件名称
    val fileName: String,
    // 文件地址
    val filePath: String,
    // 文件类型
    val fileType: String,
    // 文件预览地址
    val previewUrl: String,
    // 文件下载地址
    val downloadUrl: String,
    // 所属目录地址
    val parentDir: String,
    // 预览尺寸
    val previewSize: Size
) {
    data class Size(val width: Int, val height: Int)
}