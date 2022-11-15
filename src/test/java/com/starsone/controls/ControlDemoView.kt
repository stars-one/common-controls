package com.starsone.controls

import com.starsone.controls.common.RemixIconData
import com.starsone.controls.common.remixIconText
import com.starsone.controls.common.xChooseFileDirectory
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
        RemixIconData.init(resources)
        xChooseFileDirectory("下载目录", dirPath)

        xChooseFileDirectory("下载目录", dirPath, 400.0,jfxbutton {
            graphic = remixIconText("folder-2-fill")
            text = "选择文件夹"
        })



    }
}
