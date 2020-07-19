package com.starsone.controls.view

import com.starsone.controls.common.DialogBuilder
import com.starsone.demo.util.*
import javafx.scene.input.KeyCombination
import kfoenix.jfxbutton
import tornadofx.*


class MainView : View() {

    override var root = vbox {

        setPrefSize(500.0, 300.0)

        jfxbutton("显示Toast") {
            action {
                showToast(this@vbox, "这是测试Toast")
            }
        }


        jfxbutton("测试消息") {

            action {
                jfxdialog(currentStage, "测试对话框", "这是对话框内容")
            }
        }
        jfxbutton("测试确认对话框") {
            action {
                DialogBuilder(currentStage)
                        .setTitle(title)
                        .setTitle("标题")
                        .setMessage("内容")
                        .setNegativeBtn("取消"){ println("点击了取消按钮")}
                        .setPositiveBtn("确定") { println("点击了确定按钮")}
                        .create()
            }
        }
        jfxbutton("测试输入对话框") {

            action {
                jfxdialog(currentStage, "带输入框的对话框", "输入整数内容", { text -> println(text) })
            }
        }

        jfxbutton("测试输出对话框") {
            action {
                //文件
                jfxdialog(currentStage, "带链接的输入框", "输出目录为","D:\\text.txt"
                        ,false)
                //网址
                jfxdialog(currentStage, "带链接的输入框", "输出目录为","stars-one.site"
                        ,true)
            }
        }

        jfxbutton("测试加载对话框") {
            action {
                loadingDialog(currentStage,"标题","内容"){alert ->
                    runAsync {
                        for (i in 0..3) {
                            Thread.sleep(200)
                            println(i)
                        }
                    } ui{
                        //或alert.close()
                        alert.hideWithAnimation()
                    }

                }

            }
        }
        jfxbutton("关闭程序对话框") {
            action {
                stopDialog(currentStage,"标题","点击确定结束当前程序")
            }
        }
        jfxbutton("检测更新") {
            action {
                TornadoFxUtil.checkVersion(currentStage, "https://www.cnblogs.com/stars-one/p/13284015.html", 0, "v1.0")
            }
        }
        jfxbutton("测试更新对话框") {
            action {
                DownloadDialogView(currentStage,
                        "http://gdown.baidu.com/data/wisegame/01946d7d87cedf3e/cacd01946d7d87cedf3e3f030165d2c5.apk",
                        "Q:\\doc\\tsest.apk")
                        .show()
            }
        }
        jfxbutton("显示Toast") {
            action {
                showToast(this@vbox, "这是测试Toast")
            }
        }

        urlLink("博客地址","stars-one.site")

        jfxbutton("右键菜单") {
            contextMenu = contextmenu {
                iconItem("文件","x5.jpg",keyCombination = KeyCombination.keyCombination("ctrl+u")){
                    action{

                    }
                }
            }
        }


        circlejfxbutton("x5.jpg",20)


    }
}



