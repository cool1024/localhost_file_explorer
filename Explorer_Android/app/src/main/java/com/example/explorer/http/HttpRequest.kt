package com.example.explorer.http

import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.Request
import java.io.FileOutputStream
import java.lang.RuntimeException
import java.util.concurrent.TimeUnit

class HttpRequest(
    val config: HttpConfig,
    val instance: OkHttpClient,
    val transformer: ApiDataTransformer,
) {

    fun config(newConfig: HttpConfig): HttpRequest {
        return HttpRequest(newConfig, instance, transformer)
    }

    fun get(
        apiName: String,
        queryParams: Map<String, Any>? = null
    ): Single<ApiOriginData> {
        val request = createUrlRequest(apiName, queryParams)
        return sendRequest(request)
    }


    fun post(
        apiName: String,
        queryParams: Map<String, Any>? = null,
        params: Map<String, Any>? = null
    ): Single<ApiOriginData> {
        val request = createPostRequest(apiName, queryParams, params)
        return sendRequest(request)
    }


    fun download(downloadUrl: String): Response {
        val requestBuilder = Request.Builder()
        val request = requestBuilder.url(downloadUrl).build()
        return instance.newCall(request).execute()
    }

    fun syncDownload(downloadUrl: String, outputStream: FileOutputStream): Boolean {
        var result = false
        download(downloadUrl).body()?.byteStream()?.let { stream ->
            var len: Int
            val buffer = ByteArray(1024)
            while (stream.read(buffer).also { len = it } > 0) {
                outputStream.write(buffer, 0, len)
            }
            outputStream.close()
            result = true
        }
        return result
    }

    /**
     *  发送同步请求（阻塞当前线程）
     */
    private fun sendSyncRequest(request: Request): ApiOriginData {
        var bodyStr: String = RESPONSE_BODY_EMPTY
        var code: Int = CODE_UNKNOWN
        return try {
            val response = instance.newCall(request).execute()
            code = response.code()
            response.body()?.also {
                bodyStr = it.string()
            }
            return if (code == CODE_SUCCESS) {
                ApiOriginData(
                    apiData = transformer.transform(bodyStr, code),
                    originBodyStr = bodyStr,
                    code = code
                )
            } else {
                ApiOriginData.error(
                    errorMsg = HTTP_CODE_MESSAGES[code] ?: REQUEST_OTHER_ERROR,
                    responseData = REQUEST_ERROR,
                    body = bodyStr,
                    code = code
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ApiOriginData.error(
                errorMsg = REQUEST_ERROR,
                responseData = REQUEST_ERROR,
                body = bodyStr,
                code = code
            )
        }
    }

    /**
     *  发送异步请求，返回观察对象
     */
    private fun sendRequest(request: Request): Single<ApiOriginData> {
        return Single.fromCallable<ApiOriginData> {
            sendSyncRequest(request)
        }.subscribeOn(Schedulers.newThread())
            .onErrorReturn { error ->
                error.printStackTrace()
                ApiOriginData.error(
                    errorMsg = REQUEST_ERROR,
                    responseData = REQUEST_ERROR,
                    body = error.toString(),
                    code = CODE_UNKNOWN
                )
            }
    }

    /**
     * 创建一个简单的Url请求对象
     *
     * @param apiName 接口名称
     * @param queryParams  链接附带参数
     */
    private fun createUrlRequest(
        apiName: String,
        queryParams: Map<String, Any>? = null
    ): Request {
        val apiPath = getRequestUrl(apiName, queryParams)
        debug(apiPath)
        return try {
            Request.Builder().url(apiPath).build()
        } catch (e: IllegalArgumentException) {
            Request.Builder().url("http://localhost").build()
        }
    }

    /**
     * 创建一个Post请求对象
     *
     * @param apiName 接口名称
     * @param queryParams  链接附带参数
     * @param params 接口参数
     */
    private fun createPostRequest(
        apiName: String,
        queryParams: Map<String, Any>? = null,
        params: Map<String, Any>? = null
    ): Request {
        var requestBuilder = Request.Builder()
        val apiPath = getRequestUrl(apiName, queryParams)
        val body = if (params != null) {
            val jsonStr = Gson().toJson(params)
            RequestBody.create(MediaType.parse(DEFAULT_MEDIA_TYPE), jsonStr)
        }else{
            RequestBody.create(MediaType.parse(DEFAULT_MEDIA_TYPE),"")
        }
        requestBuilder = requestBuilder.url(apiPath)
        requestBuilder.post(body)
        return requestBuilder.build()
    }

    /**
     * 获取完整的请求连接地址
     * @param apiName 接口名称
     * @param params  附加参数
     */
    private fun getRequestUrl(apiName: String, params: Map<String, Any>? = null): String {
        val queryStr = params?.let { queryParams ->
            val builder = Uri.parse("").buildUpon()
            queryParams.keys.forEach { key ->
                builder.appendQueryParameter(key, queryParams[key].toString())
            }
            builder.build().toString()
        } ?: String()
        return "${config.requestHost}/$apiName$queryStr"
    }

    companion object {

        /**
         * Http Code
         */
        private const val CODE_200 = 200
        private const val CODE_401 = 401
        private const val CODE_403 = 403
        private const val CODE_422 = 422
        private const val CODE_500 = 500
        private const val CODE_UNKNOWN = 0
        const val CODE_SUCCESS = CODE_200

        /**
         * Error  message string
         */
        const val RESPONSE_DATA_ERROR = "数据格式错误"
        const val RESPONSE_BODY_EMPTY = "响应体内容为空"
        const val REQUEST_ERROR = "请求错误，服务器响应异常"
        const val REQUEST_OTHER_ERROR = "其它错误"

        private val HTTP_CODE_MESSAGES = mapOf(
            CODE_200 to "请求成功",
            CODE_401 to "令牌错误",
            CODE_403 to "权限错误",
            CODE_422 to "参数错误",
            CODE_500 to "服务器错误",
            CODE_UNKNOWN to "其它错误"
        )

        private const val DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8"


        private lateinit var requestInstance: HttpRequest

        fun prepare(config: HttpConfig) {
            val client = OkHttpClient.Builder()
                .connectTimeout(config.requestTimeout.toLong(), TimeUnit.SECONDS)
                .build()
            requestInstance = HttpRequest(config, client, ApiDataDefaultTransformer())
        }

        fun config(config: HttpConfig) {
            if (this::requestInstance.isInitialized) {
                requestInstance = requestInstance.config(config)
            } else {
                prepare(config)
            }
        }

        fun instance(): HttpRequest {
            if (!this::requestInstance.isInitialized) {
                throw RuntimeException("You must call prepare() before use it!")
            }
            return requestInstance
        }

        fun debug(message: String) {
            Log.d(HttpRequest::class.java.name, message)
        }
    }
}