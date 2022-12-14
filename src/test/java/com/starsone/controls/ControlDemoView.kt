package com.starsone.controls

import com.starsone.controls.common.remixIconText
import com.starsone.controls.common.xChooseFileDirectory
import com.starsone.controls.common.xSwitch
import com.starsone.controls.utils.TornadoFxUtil
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import kfoenix.jfxbutton
import tornadofx.*

/**
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date create in  2020/11/19 10:21
 */
class ControlDemoView : View("My View") {

    val dirPath = SimpleStringProperty("555")
    override val root = vbox {
        setPrefSize(600.0, 400.0)
        style {
            backgroundColor += c("white")
        }

        xChooseFileDirectory("下载目录", dirPath)

        xChooseFileDirectory("下载目录", dirPath, 400.0, jfxbutton {
            graphic = remixIconText("folder-2-fill")
            text = "选择文件夹"
        })

        println("本机序列号为 ${TornadoFxUtil.getDeviceSn()}")

        val select = SimpleBooleanProperty()

        xSwitch( "开启自动更新",select) {
            toggleColor = c("blue")
            toggleLineColor =c("red")

            unToggleColor = c("green")
            unToggleLineColor = c("orange")
        }

        button {
            action {
                println(select.value)
            }
        }


    }


}
