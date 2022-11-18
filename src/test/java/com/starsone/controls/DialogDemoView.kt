package com.starsone.controls

import com.starsone.controls.common.RemixIconData
import com.starsone.controls.common.showDialogRadio
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

/**
 * Dialog相关对话框测试
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date create in  2020/11/19 10:21
 */
class DialogDemoView : View("My View") {

    val dirPath = SimpleStringProperty("555")
    override val root = vbox {
        setPrefSize(600.0, 400.0)
        style {
            backgroundColor += c("white")
        }
        RemixIconData.init(resources)

        button("单选按钮对话框") {
            action {
              showDialogRadio(currentStage,"选择选项", listOf("txt","md")){label, index ->
                  println("选中下标$index 内容:$label")
                  val file = chooseDirectory("选择文件夹")
              }
            }
        }


    }


}
