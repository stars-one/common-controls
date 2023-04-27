package com.starsone.controls

import com.starsone.controls.common.remixIconText
import com.starsone.controls.common.xChooseFileDirectory
import com.starsone.controls.common.xSwitch
import com.starsone.controls.utils.TornadoFxUtil
import javafx.animation.Interpolator
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color
import javafx.util.Duration
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

        //val simpleStringProperty = SimpleStringProperty("        这里是滚动的测试文本示例...")
        //xNoticeBar(simpleStringProperty,speed = 4)

        //simpleStringProperty.set("虽然之前我之前已经有过了解了,不过还是搜索了下解决方案,并没有解决方案,唯一的解决方案就是将链接转为二维码,并将二维码保存在设备本地,然后让用户自己打开微信扫一扫,识别本地图片,微信使用内置浏览器去打开网页,然后触发付款操作")

        val simpleNotice=SimpleStringProperty("      这是一个测试公告信息...")
        scrollpane {
            alignment = Pos.CENTER_LEFT
            prefWidth = 560.0

            style {
                focusColor = Color.TRANSPARENT
                borderWidth += box(0.px)
                borderColor += box(Color.TRANSPARENT)
                hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                vBarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                padding = box(0.px)
            }

            hbox {
                translateY = 5.0
                alignment = Pos.CENTER_LEFT

                label(simpleNotice) {
                    val defaultValue = translateXProperty().value
                    //滚动的逻辑
                    val rollAction: ((String) -> Unit) = {
                        val newValue = it
                        //阅读速度,8个字1s
                        val time = newValue.length / 8
                        translateXProperty().animate(-(newValue.length * 10), Duration.seconds(time.toDouble()), Interpolator.LINEAR) {
                            cycleCount = -1
                            setOnFinished {
                                translateXProperty().set(defaultValue)
                            }
                        }
                    }

                    simpleNotice.onChange {
                        rollAction.invoke(it?:"")
                    }
                    rollAction.invoke(simpleNotice.value)

                    setOnMouseClicked {
                        //点击事件

                    }
                }
            }
        }


    }


}
