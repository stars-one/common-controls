package com.starsone.controls.utils.net

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response
import tornadofx.*
import java.net.URLEncoder
import kotlin.math.roundToInt


/**
 * okhttp拦截器,传相关系统和APP信息请求头
 * @author StarsOne
 * @url [http://stars-one.site](http://stars-one.site)
 * @date Create in  2023/03/26 22:29
 */
class CommonHeaderInterceptor() : Interceptor {

    val deviceHead by lazy{
        getInfo()
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request() // 获取旧连接
        val requestBuilder = oldRequest.newBuilder() // 建立新的构建者
        // 将旧请求的请求方法和请求体设置到新请求中
        requestBuilder.method(oldRequest.method, oldRequest.body)
        // 获取旧请求的头
        val headers = oldRequest.headers
        if (headers != null) {
            val names: Set<String> = headers.names()
            for (name in names) {
                val value = headers[name].toString()
                // 将旧请求的头设置到新请求中
                requestBuilder.header(name, value)
            }
        }
        // 添加我们请求头
        //防止中文,转换一下
        requestBuilder.header("xn-device-head", URLEncoder.encode(deviceHead))

        // 建立新请求连接
        val newRequest = requestBuilder.build()
        return chain.proceed(newRequest)
    }

    /**
     * 获取APP版本号及系统版本信息(PC版)
     *
     */
    private fun getInfo(): String {
        val gson = Gson()
        val props = System.getProperties()
        val osName = props.getProperty("os.name")
        val arch = props.getProperty("os.arch")
        val osVersionCode = try {
            props.getProperty("os.version").toDouble().roundToInt()
        } catch (e: Exception) {
            1
        }

        val resources = ResourceLookup(this)
        return try {
            //加载图标字体文件
            val jsonObject = loadJsonObject(resources.text("/desc.json"))
            val appVersionCode = jsonObject.getInt("versionCode")
            val appName = jsonObject.getString("appName")
            val versionName = jsonObject.getString("version")
            gson.toJson(AppAndSystemInfo(appName, versionName, appVersionCode, osName, arch, osVersionCode))
        } catch (e: Exception) {
            gson.toJson(AppAndSystemInfo("", "", -1, osName, arch, osVersionCode))
        }
    }
}

data class AppAndSystemInfo(val appName: String, val appVersionName: String, val appVersionCode: Int, val systemName: String,val systemArch: String ,val systemVersionCode: Int)


