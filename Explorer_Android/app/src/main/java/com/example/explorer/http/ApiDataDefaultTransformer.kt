package com.example.explorer.http

import com.google.gson.Gson

class ApiDataDefaultTransformer : ApiDataTransformer {

    class TransformerData(val result: Boolean, val message: String, val data: Any?)

    override fun transform(body: String, code: Int): ApiData {
        return try {
            val json = Gson().fromJson(body, TransformerData::class.java)
            val source = (json.data ?: String())
            val data = if (source is String) {
                source
            } else {
                Gson().toJson(source)
            }
            ApiData(
                result = json.result,
                message = json.message,
                data = data
            )
        } catch (e: Exception) {
            ApiData.dataError(body, e.toString()).storeException(e)
        }
    }
}