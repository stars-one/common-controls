package com.starsone.controls.utils

import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


fun Long.toDateString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format).format(Date(this))
}

fun Date.toDateString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    return SimpleDateFormat(format).format(this)
}

/**
 * 将字节(B)转为对应的单位
 */
fun Long.toUnitString(): String {
    val df = DecimalFormat("#.00")
    val bytes = this
    return when {
        bytes < 1024 -> return df.format(bytes.toDouble()) + "B"
        bytes < 1048576 -> df.format(bytes.toDouble() / 1024) + "K"
        bytes < 1073741824 -> df.format(bytes.toDouble() / 1048576) + "Mb"
        else -> df.format(bytes.toDouble() / 1073741824) + "Gb"
    }
}

/**
 * Double保留几位小数
 *
 * @param num
 * @return
 */
fun Double.toFix(num: Int=2):Double{
    val one = this
    val two = BigDecimal(one)
    return two.setScale(num, BigDecimal.ROUND_HALF_UP).toDouble()
}

/**
 * 将json数据转为List<T>
 *
 * @param T 数据类型
 * @return
 */
fun <T> String.parseJsonToList(clazz: Class<T>): List<T> {
    val gson = Gson()
    val type = `$Gson$Types`.newParameterizedTypeWithOwner(null, ArrayList::class.java, clazz)
    val data: List<T> = gson.fromJson(this, type)
    return data
}

/**
 * 将json字符串数据转为某个类
 *
 * @param T
 * @return
 */
inline fun <reified T> String.parseJsonToObject(): T {
    val gson = Gson()
    val result = gson.fromJson(this, T::class.java)
    return result
}

/**
 * 前置补0操作
 *
 * 例子:
 * - `1.fillZero(3)` //结果为"001"
 * - `112.fillZero(3)` //结果为"112"
 *
 * @param maxLength 最大位数
 */
fun Int.fillZero(maxLength:Int):String{
    return String.format("%0${maxLength}d", this)
}

/**
 * 前置补0操作
 *
 * 例子:
 * - `1L.fillZero(3)` //结果为"001"
 * - `112L.fillZero(3)` //结果为"112"
 *
 * @param maxLength 最大位数
 */
fun Long.fillZero(maxLength:Int):String{
    return String.format("%0${maxLength}d", this)
}
