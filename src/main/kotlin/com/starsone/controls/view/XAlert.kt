package com.starsone.controls.view

import com.starsone.controls.common.RemixIconText
import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.function.Consumer

/**
 * 窗口内通知条
 * @author stars-one
 * @date 2022-12-14 22:35:36
 *
 */
class XAlert(text: String) : HBox() {
    private val iconLabel = Label()
    private val textLabel = Label()
    private val closeLabel = Label()

    /**
     * 关闭事件
     *
     * @param consumer
     */
    fun closeEvent(consumer: Consumer<MouseEvent?>) {
        closeLabel.graphic = Label()
        closeLabel.cursor = Cursor.HAND
        closeLabel.onMouseClicked = EventHandler { event: MouseEvent? -> consumer.accept(event) }
    }

    companion object {
        /**
         * 创建纯文字的提示
         *
         * @param text
         * @return
         */
        fun create(text: String): XAlert {
            return XAlert(text)
        }

        /**
         * 创建图标和文字提示
         *
         * @param text 文字提示
         * @param alertLevel 等级(包含了图标)
         * @return
         */
        fun create(text: String, alertLevel: AlertLevel): XAlert {
            val node = RemixIconText(alertLevel.iconName)
            node.style(true) {
                fill = c(alertLevel.iconColor)
            }
            val cfAlert = Companion[node, text]
            cfAlert.styleClass.add(alertLevel.level)
            return cfAlert
        }

        /**
         * 创建文本提示和节点
         *
         * @param text 文字
         * @param node 节点
         * @return
         */
        fun create(text: String, node: Node): XAlert {
            val cfAlert = Companion[node, text]
            cfAlert.styleClass.add("primary")
            return cfAlert
        }

        private operator fun get(graphic: Node, text: String): XAlert {
            val cfAlert = XAlert(text)
            cfAlert.iconLabel.graphic = graphic
            return cfAlert
        }
    }

    init {
        styleClass.add("cf-alert")
        textLabel.maxWidth = Double.MAX_VALUE
        children.addAll(iconLabel, textLabel, closeLabel)
        setHgrow(textLabel, Priority.ALWAYS)
        //class
        iconLabel.styleClass.add("icon")
        textLabel.styleClass.add("text")
        closeLabel.styleClass.add("close")
    }

    init {
        iconLabel.graphic = Label()
        textLabel.text = text
    }
}

/**
 * 通知等级,不同等级有不同颜色和图标
 *
 * @property level
 * @property iconName
 * @property iconColor
 * @constructor Create empty Alert level
 */
enum class AlertLevel(val level: String, val iconName: String, val iconColor: String) {
    PRIMARY("primary", "message-3-fill", "#5C80EF"),
    SUCCESS("success", "checkbox-circle-fill", "#33C437"),
    INFO("info", "information-fill", "#68C0FF"),
    WARN("warn", "error-warning-fill", "#e6a23c"),
    DANGER("danger", "close-circle-fill", "#FF4B3A")
}


