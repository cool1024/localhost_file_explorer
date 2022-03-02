package com.cool1024.server.data

import com.google.gson.Gson

data class ApiData<T>(val result: Boolean, val message: String, val data: T) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}