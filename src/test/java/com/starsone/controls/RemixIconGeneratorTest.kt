package com.starsone.controls

import com.google.gson.Gson
import tornadofx.*
import java.io.File

/**
 * 快速生成新版本RemixIcon的json数据
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2023/04/18 15:07
 *
 */
fun main() {
    val json = File("D:\\Download\\fonts\\remixicon.glyph.json").readText()
    val jsonObject = loadJsonObject(json)
    val keys = jsonObject.keys
    val list = keys.map {
        val key = it
        val unicode = jsonObject.getJsonObject(key).getString("unicode").replace("&#x", "\\u").replace(";","")
        hashMapOf("name" to key,"unicode" to unicode)
    }
    val result = Gson().toJson(list)
    File("D:\\Download\\fonts\\remixicon.json").writeText(result)

}
