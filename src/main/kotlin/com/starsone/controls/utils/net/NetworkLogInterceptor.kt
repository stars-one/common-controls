package com.starsone.controls.utils.net

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.net.URLDecoder
import java.nio.charset.Charset


/**
 * okhttp拦截器,生成日志
 * @author StarsOne
 * @url [http://stars-one.site](http://stars-one.site)
 * @date Create in  2023/03/26 22:29
 */
class NetworkLogInterceptor(var isDebugger: Boolean = false) : Interceptor {

    val gson = Gson()
    val charset = Charset.forName("utf-8")

    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        if (isDebugger) {
            val requestBody = request.body
            try {
                var bodyStr = ""
                if (requestBody != null) {
                    val buffer = Buffer()
                    requestBody.writeTo(buffer)
                    bodyStr = buffer.readString(charset)
                }
                val header = gson.toJson(request.headers)
                bodyStr = if (bodyStr == "") "无参数" else bodyStr

                val response = chain.proceed(request)
                val responseBody = response.body
                var bodyStrRespon = ""
                val source = responseBody?.source()

                source?.request(Long.MAX_VALUE)
                val buffer = source?.buffer

                bodyStrRespon = buffer!!.clone().readString(charset)
                bodyStrRespon = if (bodyStrRespon === "") "无响应Body" else bodyStrRespon

                println("------------------------ 请求开始 ------------------------")
                println("请求url: " + request.url)
                println("请求url(解码): " + URLDecoder.decode(request.url.toString()))
                println("请求方式 " + request.method)
                println("请求头: $header")
                if (request.method.equals("get", true)) {
                    val arr = request.url.query?.split("&")
                    println("请求参数:")
                    arr?.forEach {
                        arr.forEach {
                            println("   " + it)
                        }
                    }
                } else {
                    val arr = URLDecoder.decode(bodyStr).split("&")
                    println("请求参数:")
                    arr.forEach {
                        println("   " + it)
                    }
                }
                println("响应body: $bodyStrRespon")
                println("------------------------ 请求结束 ------------------------")
                return response
            } catch (e: Exception) {
                e.printStackTrace()
                System.err.println("okhttp拦截器打印日志发生错误!!!!!!")
                return chain.proceed(request)
            }
        } else {
            return chain.proceed(request)
        }


    }
}
