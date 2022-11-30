package com.starsone.controls.utils

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



