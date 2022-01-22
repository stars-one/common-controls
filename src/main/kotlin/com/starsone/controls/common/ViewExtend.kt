package com.starsone.controls.common

import javafx.scene.control.Button
import tornadofx.*


/**
 * 是否快速点击
 */
fun Button.isFastClick(time:Long): Boolean {
    val lastClickTime = userData as Long?
    val currentTime = System.currentTimeMillis()

    userData = currentTime
    if (lastClickTime != null && currentTime - lastClickTime <= time) {
        return true
    }
    return false
}

/**
 * 设置防抖的Action
 * @param time 单位毫秒，默认1000（1s内防抖）
 */
fun Button.setActionHank(time:Long = 1000,op: () -> Unit) {
    action {
        if (!isFastClick(time)) {
            op()
        }
    }
}
