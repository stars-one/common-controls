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
 * @author ChenFei
 * @date 2022/7/8
 *
 *
 * CFAlert 警报
 */
class CFAlert(text: String) : HBox() {
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
        fun create(text: String): CFAlert {
            return CFAlert(text)
        }

        fun create(text: String, alertLevel: AlertLevel): CFAlert {
            val node = RemixIconText(alertLevel.iconName)
            node.style(true) {
                fill = c(alertLevel.iconColor)
            }
            val cfAlert = Companion[node, text]
            cfAlert.styleClass.add(alertLevel.level)
            return cfAlert
        }

        fun create(text: String, node: Node): CFAlert {
            val cfAlert = Companion[node, text]
            //todo 这里的等级需要调整
            cfAlert.styleClass.add("primary")
            return cfAlert
        }

        private operator fun get(graphic: Node, text: String): CFAlert {
            val cfAlert = CFAlert(text)
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

enum class AlertLevel(val level: String, val iconName: String, val iconColor: String) {
    PRIMARY("primary", "message-3-fill", "#5C80EF"),
    SUCCESS("success", "checkbox-circle-fill", "#33C437"),
    INFO("info", "information-fill", "#68C0FF"),
    WARN("warn", "error-warning-fill", "#FFDC42"),
    DANGER("danger", "close-circle-fill", "#FF4B3A")
}


