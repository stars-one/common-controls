package com.starsone.controls.utils

import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.DialogPane
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.Mnemonic
import javafx.scene.layout.*
import javafx.scene.text.TextFlow
import javafx.stage.Window
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
 * 为按钮设置快捷键,触发快捷键后会触发按钮的action操作 **（此方法需要在`onBeforeShow()`方法中调用）**
 * @param keyCodeCombination 快捷键，如 ctrl+alt+c快捷键为`KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN)`
 * @param currentWindow 当前窗口
 */
fun Button.addShortcut(keyCodeCombination: KeyCodeCombination, currentWindow: Window?) {
    val mnemonic1 = Mnemonic(this, keyCodeCombination)
    currentWindow?.scene?.addMnemonic(mnemonic1)
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
