package com.starsone.controls.utils

import com.google.gson.Gson
import com.starsone.controls.utils.net.NetWorkLogInterceptor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


/**
 * 网络请求工具类
 *
 * @constructor Create empty Net work util
 */
object NetWorkUtil {

    val JSON = "application/json;charset=utf-8".toMediaTypeOrNull()

    val gson by lazy { Gson() }

    val okHttpClient by lazy {
        val client = OkHttpClient.Builder()
                .addInterceptor(NetWorkLogInterceptor())
                .build()
        client
    }

    /**
     * 发起post请求(传统的表单)
     *
     * @param url 接口地址
     * @param data 数据HashMap
     * @param headers 请求头 HashMap
     * @param callBack 回调函数
     */
    inline fun <reified T> post(url: String, data: HashMap<String, out Any>, headers: HashMap<String, String>, callBack: RespCallBack<T>) {
        //1.构造传参参数
        val formBody = FormBody.Builder()
        data.forEach { (t, v) ->
            formBody.add(t, v.toString())
        }
        val formBodyData = formBody.build()
        //2.构造request
        val requestBuilder =  Request.Builder()
                .url(url)
                .post(formBodyData)

        //构造头部参数
        headers.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }
        val request = requestBuilder.build()
        //3.发送请求
        sendRequest(request, callBack)
    }

    /**
     * Post请求(传json数据)
     *
     * @param T
     * @param url
     * @param data 实体类数据(会自动转为json字符串)
     * @param headers 请求头
     * @param callBack
     */
    inline fun <reified T> postJson(url: String, data: Any, headers: HashMap<String, String>, callBack: RespCallBack<T>) {
        //1.构造传参参数
        val requestBody = gson.toJson(data).toRequestBody(JSON)

        //2.构造request
        val requestBuilder =  Request.Builder()
                .url(url)
                .post(requestBody)

        //构造头部参数
        headers.forEach { (t, u) ->
            requestBuilder.addHeader(t, u)
        }
        val request = requestBuilder.build()
        //3.发送请求
        sendRequest(request, callBack)
    }

    /**
     * Get 请求
     *
     * @param url
     * @param data 数据map,会自动拼接
     * @param headers
     * @param callBack
     */
    inline fun <reified T> get(url: String, data: HashMap<String, out Any>, headers: HashMap<String, String>, callBack: RespCallBack<T>) {
        //1.构造传参参数
        val param = data.map { (t, u) ->
            """$t=$u"""
        }.joinToString("&")

        var finalUrl = url + param
        if (!url.endsWith("?")) {
            finalUrl = url + "?" + param
        }
        val builder = Request.Builder()
                .url(finalUrl)
        headers.forEach { (t, u) ->
            builder.addHeader(t, u)
        }
        //2.构造request
        val request = builder.get().build()
        //3.发出请求
        sendRequest(request, callBack)
    }


    /**
     * 发起网络请求
     *
     * @param T
     * @param request okhttp的request请求
     * @param callBack 回调
     */
    inline fun <reified T> sendRequest(request: Request, callBack: RespCallBack<T>) {
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callBack.onFailure(call, e)
            }

            override fun onResponse(call: Call, response: Response) {
                val resp = response.body?.string()

                if (resp != null) {
                    //判断如果是string,直接返回数据,不做实体类转换操作
                    if (T::class == String::class) {
                        callBack.onResponse(call, resp as T)
                    } else {
                        val result = gson.fromJson(resp, T::class.java)
                        callBack.onResponse(call, result)
                    }
                } else {
                    callBack.onFailure(call, Exception("数据获取失败!"))
                }
            }
        })
    }


    /**
     * 回调函数
     *
     * @param T
     * @constructor Create empty Resp call back
     */
    abstract class RespCallBack<T> {
        abstract fun onFailure(call: Call, e: Exception)

        abstract fun onResponse(call: Call, data: T)
    }
}
