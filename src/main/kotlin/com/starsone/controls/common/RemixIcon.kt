package com.starsone.controls.common

import com.jfoenix.controls.JFXButton
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.paint.Paint
import javafx.scene.text.Text
import org.apache.commons.text.StringEscapeUtils
import tornadofx.*

object RemixIconData {
    val iconIndexMap = hashMapOf<String, String>()

    val jsonPath = "/ttf/remixicon.json"
    val fontPath = "/ttf/remixicon.ttf"

    fun init(resources: ResourceLookup) {
        //加载图标字体文件
        val jsonArray = loadJsonArray(resources.text(jsonPath))

        val iterator = jsonArray.iterator()
        while (iterator.hasNext()) {
            val jsonObject = iterator.next().asJsonObject()
            val key = jsonObject.getString("name")
            val value = jsonObject.getString("unicode")
            iconIndexMap[key] = StringEscapeUtils.unescapeJava(value)
        }
    }

}

/**
 * Remix字体图标Text控件,使用时候需要在Application中进行初始化数据操作
 * @param name    图标参考地址 https://remixicon.com,目前使用的remix icon 2.5.0版本
 * @param fontColor 字体颜色,默认是黑色
 * @param fontSize 字体大小,默认为25px
 * @param op
 * @return
 */
fun EventTarget.remixIconText(name: String, fontColor: Paint = c("black"), fontSize: Int = 25, op: (Text.() -> Unit) = {}): Text {
    val text = text {
        font = loadFont(RemixIconData.fontPath, fontSize)!!
        text = RemixIconData.iconIndexMap[name]
        style {
            fill = fontColor
        }
    }
    return opcr(this, text, op)
}

/**
 * Remix字体图标Label控件,使用时候需要在Application中进行初始化数据操作
 * @param name    图标参考地址 https://remixicon.com,目前使用的remix icon 2.5.0版本
 * @param fontColor 字体颜色,默认是黑色
 * @param fontSize 字体大小,默认为25px
 * @param op
 * @return
 */
fun EventTarget.remixIconLabel(name: String, fontColor: Paint = c("black"), fontSize: Int = 25, op: (Label.() -> Unit) = {}): Label {
    val label = label {
        font = loadFont(RemixIconData.fontPath, fontSize)!!
        text = RemixIconData.iconIndexMap[name]
        style {
            textFill = fontColor
        }
    }
    return opcr(this, label, op)
}

/**
 * Remix圆形图标按钮
 *
 * @param name 图标名称
 * @param fontColor 文本颜色
 * @param fontSize 文本字体大小
 * @param bgColor 按钮背景色
 * @param buttonType 按钮类型
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.remixIconButton(name: String, fontColor: Paint = c("black"), fontSize: Int = 25, bgColor: Paint = c("white"), buttonType: JFXButton.ButtonType = JFXButton.ButtonType.RAISED, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val btn = xIconButton(remixIconLabel(name, fontColor, fontSize), bgColor, fontColor, buttonType,fontColor)
    return opcr(this, btn, op)
}
