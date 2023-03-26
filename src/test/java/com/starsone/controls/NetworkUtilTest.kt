package com.starsone.controls

import com.starsone.controls.utils.net.NetworkUtil
import okhttp3.Call

/**
 *
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2023/03/27 01:12
 *
 */

fun main() {
    //开启网络请求日志输出
    NetworkUtil.isDebug = true
    
    //get请求示例
    val url = "http://127.0.0.1:8099/userlogin"
    val data = hashMapOf("username" to "hello")

    NetworkUtil.get(url, data, hashMapOf(), object : NetworkUtil.RespCallBack<String>() {

        override fun onResponse(call: Call, data: String) {

        }

        override fun onFailure(call: Call, e: Exception) {
            e.printStackTrace()
        }
    })
}
