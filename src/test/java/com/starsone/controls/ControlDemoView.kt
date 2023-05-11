package com.starsone.controls

import com.starsone.controls.common.*
import com.starsone.controls.utils.QRCodeUtil
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

        remixIconText("arrow-up-double-line")

        xChooseFileDirectory("下载目录", dirPath)

        xChooseFileDirectory("下载目录", dirPath, 400.0, jfxbutton {
            graphic = remixIconText("folder-2-fill")
            text = "选择文件夹"
        })

        println("本机序列号为 ${TornadoFxUtil.getDeviceSn()}")

        val select = SimpleBooleanProperty()

        xSwitch("开启自动更新", select) {
            toggleColor = c("blue")
            toggleLineColor = c("red")

            unToggleColor = c("green")
            unToggleLineColor = c("orange")
        }

        button {
            action {
                println(select.value)
            }
        }

        val simpleStringProperty = SimpleStringProperty("        这里是滚动的测试文本示例...")
        xNoticeBar(simpleStringProperty, speed = 4)


        //得到的swing的image对象
        val buImg = QRCodeUtil.getQRcodeFxImg("这是测试文本")
        val buImg1 = QRCodeUtil.getQRcodeFxImg("这是测试文本", null, "底部文字")
        val buImg2 = QRCodeUtil.getQRcodeFxImg("这是测试文本", "/x5.jpg", "底部文字")

        val list = listOf(buImg, buImg1, buImg2)

        hbox(20.0) {
            list.forEach {
                imageview(it) {
                    fitWidth = 200.0
                    fitHeight = 200.0
                }
            }
        }

        hbox(10.0) {
            xCircleImageView("/x5.jpg", 50.0)
            xCircleImageView(SimpleStringProperty("/x5.jpg"), 50.0)
        }
        val simpleStringpro = SimpleStringProperty("免费")
        xTag(simpleStringpro)

        //倒计时的按钮提示
        xCountDownBtn("发送验证码", 90, {
            //点击事件
            println("已点击按钮...")
        })
    }


}
