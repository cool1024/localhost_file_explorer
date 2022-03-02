package com.cool1024.server.data

data class PageData<T>(
    val total: Int = 0,
    val rows: List<T>
)