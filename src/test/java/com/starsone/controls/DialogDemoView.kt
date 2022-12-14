package com.starsone.controls

import com.starsone.controls.common.*
import com.starsone.controls.view.AlertLevel
import com.starsone.controls.view.XMessage
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


        button("吐司弹窗") {
            action {
                showToast(this@vbox, "测试弹窗")
            }
        }

        button("普通对话框") {
            action {
                showDialog(currentStage, "提示", "这是测试的文本内容")
            }
        }

        button("普通对话框(含取消和确定)") {
            action {
                DialogBuilder(currentStage)
                        .setTitle("标题")
                        .setMessage("内容")
                        .setPositiveBtn("取消") {
                            println("点击了取消按钮")
                        }.setNegativeBtn("确定") {
                            println("点击了确定按钮")
                        }
                        .create()
            }
        }

        button("输入对话框") {
            action {
                showDialog(currentStage, "提示", "请输入内容:", { text ->
                    println("用户输入内容: $text")
                })
            }
        }

        button("输出文件对话框") {
            action {
                val filePath = "D:\\project\\javafx\\CommonControls\\src\\main\\kotlin\\com\\starsone\\controls\\common\\DialogBuilder.kt"
                showDialog(currentStage, "提示", "输出文件位于", filePath, false)
            }
        }

        button("输出地址对话框") {
            action {
                val url = "https://stars-one.site"
                showDialog(currentStage, "输出文件", "输出网址为", url, true)
            }
        }

        /* button("下载对话框") {
             action {
                 val fileDownloadUrl = "https://downapp.baidu.com/appsearch/AndroidPhone/1.0.82.160/1/1009556z/20221024104408/appsearch_AndroidPhone_1-0-82-160_1009556z.apk?responseContentDisposition=attachment%3Bfilename%3D%22appsearch_AndroidPhone_1009556z.apk%22&responseContentType=application%2Fvnd.android.package-archive&request_id=1668783779_2211530606&type=static"
                 DownloadDialogView(currentStage,fileDownloadUrl,"D:\\temp\\百度手机助手.apk").show()
             }
         }*/

        button("加载对话框") {
            action {
                showLoadingDialog(currentStage, "标题", "内容", "取消") { alert ->
                    runAsync {
                        //这里做网络请求或者其他耗时的操作
                        for (i in 0..3) {
                            Thread.sleep(200)
                            println(i)
                        }
                    } ui {
                        //调用close或者hideWithAnimation关闭对话框
                        alert.hideWithAnimation()
                    }

                }
            }
        }
        button("加载对话框(带图片)") {
            action {
                showLoadingDialog(currentStage, "https://img2022.cnblogs.com/blog/1210268/202211/1210268-20221119010323539-317158345.gif", 200.0, 200.0, "加载中") { stage ->
                    runAsync {
                        //这里做网络请求或者其他耗时的操作
                        for (i in 0..3) {
                            Thread.sleep(200)
                            println(i)
                        }
                    } ui {
                        stage.hide()
                    }
                }
            }
        }

        button("关闭程序对话框") {
            action {
                showStopDialog(currentStage, "标题", "点击确定关闭程序")
            }
        }

        button("右下角弹窗") {
            action {
                showDialogPopup(currentStage, "标题", "内容说明")
            }
        }

        button("单选按钮对话框") {
            action {
                showDialogRadio(currentStage, "选择选项", listOf("txt", "md")) { label, index ->
                    println("选中下标$index 内容:$label")
                    val file = chooseDirectory("选择文件夹")
                }
            }
        }

        //显示通知条的效果
        //listOf(CFAlert.create("测试", AlertLevel.PRIMARY),
        //        CFAlert.create("测试", AlertLevel.INFO),
        //        CFAlert.create("测试", AlertLevel.SUCCESS),
        //        CFAlert.create("测试", AlertLevel.WARN),
        //        CFAlert.create("测试", AlertLevel.DANGER)).forEach {
        //    add(it)
        //}

        //val cfMessage = CFMessage.bindingContainer(this)
        //val cfMessage = CFMessage.bindingContainer(this@stackpane)
        //通知条出现是从下往上
        //val cfMessage = CFMessage.bindingContainer(this@stackpane,false)

        val cfMessage = XMessage.bindingContainer(this)
        button("普通文字弹窗") {
            action {
                cfMessage.create("hello")
            }
        }
        button("错误弹窗") {
            action {
                cfMessage.create("抱歉,删除失败", AlertLevel.DANGER)
            }
        }
    }

}
