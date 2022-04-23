package com.example.explorer.http

import com.example.explorer.http.ApiData

interface ApiDataTransformer {
    fun transform(body: String, code: Int): ApiData
}