package com.starsone.controls.utils

import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.DialogPane
import javafx.scene.layout.*
import javafx.scene.text.TextFlow
import tornadofx.*
import kotlin.reflect.full.declaredFunctions


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

/**
 * 设置布局的margin
 */
fun Pane.setMargin(insets: Insets) {
    val classList = listOf(VBox::class, HBox::class, AnchorPane::class, BorderPane::class, DialogPane::class,FlowPane::class,GridPane::class, TextFlow::class,TilePane::class)
    classList.forEach {
        val kClass = it.java.kotlin
        if (kClass.isInstance(this)) {
            val method = kClass.declaredFunctions.find {
                it.name == "setMargin" && it.parameters.size == 2
            }
            method?.call(this, insets)
        }
    }
}
