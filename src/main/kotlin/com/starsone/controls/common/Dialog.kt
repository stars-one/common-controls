package com.starsone.controls.common

import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXSpinner
import com.starsone.controls.download.HttpDownloader
import com.starsone.controls.download.LanzouParse
import com.starsone.controls.utils.TornadoFxUtil
import javafx.application.Platform
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Popup
import javafx.stage.Stage
import javafx.stage.StageStyle
import kfoenix.jfxradiobutton
import tornadofx.*
import java.io.File
import java.io.IOException

/**
 * 显示对话框和Toast的相关方法
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date Create in  2020/12/13 13:55
 *
 */

/**
 * 显示Toast
 */
fun showToast(pane: Pane, message: String) {
    val snackbar = JFXSnackbar(pane)
    snackbar.prefWidth = 300.0
    val label = Text(message)
    label.style {
        fill = c("white")
    }
    val hBox = HBox(label)
    hBox.style {
        backgroundColor += c("#323232")
        alignment = Pos.CENTER
    }
    snackbar.fireEvent(JFXSnackbar.SnackbarEvent(hBox))
}

/**
 * 输出对话框
 */
fun showDialog(stage: Stage?, title: String = "", message: String,
               url: String,
               isUrl: Boolean,
               positiveBtnText: String = "确定",
               modality: Modality = Modality.APPLICATION_MODAL
): JFXAlert<String> {
    return DialogBuilder(stage, modality)
            .setTitle(title)
            .setMessage(message)
            .setHyperLink(url, isUrl)
            .setPositiveBtn(positiveBtnText)
            .create()

}

/**
 * 消息提示框(只有确定按钮)
 */
fun showDialog(stage: Stage?, title: String = "", message: String,
               positiveBtnText: String = "确定",
               modality: Modality = Modality.APPLICATION_MODAL
): JFXAlert<String> {
    return DialogBuilder(stage, modality)
            .setTitle(title)
            .setMessage(message)
            .setPositiveBtn(positiveBtnText)
            .create()
}

/**
 * 带输入框的对话框
 */
fun showDialog(stage: Stage?, title: String,
               inputHint: String = "输入",
               listener: ((text: String) -> Unit),
               modality: Modality = Modality.APPLICATION_MODAL,
               negativeBtnText: String = "取消", positiveBtnText: String = "确定"
): JFXAlert<String> {
    return DialogBuilder(stage, modality)
            .setTitle(title)
            .setTextFieldText(inputHint, listener)
            .setPositiveBtn(positiveBtnText).setNegativeBtn(negativeBtnText)
            .create()
}

/**
 * 加载对话框
 */
fun showLoadingDialog(stage: Stage?, title: String, message: String, negativeBtnText: String, negativeBtnOnclickListener: (() -> Unit)? = null, onLoadingListener: ((alert: JFXAlert<String>) -> Unit)): JFXAlert<String> {
    return DialogBuilder(stage)
            .setTitle(title)
            .setLoadingMessage(message, onLoadingListener)
            .setNegativeBtn(negativeBtnText, negativeBtnOnclickListener)
            .create()
}

/**
 * 关闭程序并打开新版本的对话框
 *
 * @param stage
 * @param title
 * @param message
 * @param file 下载后的包
 * @return
 */
fun showStopDialog(stage: Stage?, title: String, message: String, file: File): JFXAlert<String> {
    return DialogBuilder(stage)
            .setTitle(title)
            .setMessage(message)
            .setPositiveBtn("确定") {
                //判断是否为jar文件
                TornadoFxUtil.openFile(file)
                //关闭当前应用
                stage?.close()
                Platform.exit()
            }
            .create()
}

/**
 * 关闭程序的对话框
 */
fun showStopDialog(stage: Stage?, title: String, message: String): JFXAlert<String> {
    return DialogBuilder(stage)
            .setTitle(title)
            .setMessage(message)
            .setPositiveBtn("确定") { stage?.close() }
            .create()
}

/**
 * 加载对话框
 */
fun showLoadingDialog(stage: Stage?, title: String, text: String = ""): JFXAlert<String> {
    val jfxSp = JFXSpinner()
    val speedLabel = Text()
    val label = Label(text)
    val vBox = VBox(10.0, speedLabel, label)
    vBox.style {
        alignment = Pos.CENTER_LEFT
    }
    val space = Label()
    space.setPrefSize(30.0, 2.0)
    val hbox = HBox(20.0, space, jfxSp, vBox)
    hbox.style {
        alignment = Pos.CENTER_LEFT
    }
    val alert = DialogBuilder(stage)
            .setTitle(title)
            .setCustomContext(hbox)
            .create()
    return alert
}


/**
 * 下载进度对话框
 */
class DownloadDialogView(val stage: Stage?, val url: String, val file: String = "") {

    val speedSSP = SimpleStringProperty("")
    val simpleDoubleProperty = SimpleDoubleProperty(0.0)
    val content = generateDownloadView()

    fun show() {
        //取消下载
        var task: Task<Unit>? = null
        val alert = DialogBuilder(stage)
                .setTitle("提示")
                .setCustomContext(content)
                .setNegativeBtn("后台下载")
                .setPositiveBtn("取消") {
                    task?.cancel()
                }
                .create()
        task = runAsync {
            val downloadUrl = if (url.contains("lanzou")) {
                LanzouParse().parseUrl(url, "")
            } else {
                url
            }
            HttpDownloader(downloadUrl, file)
                    .startDownload(object : HttpDownloader.OnDownloading {
                        override fun onProgress(progress: Double, percent: Int, speed: String) {
                            simpleDoubleProperty.set(progress / 100)
                            speedSSP.set(speed)
                        }

                        override fun onFinish() {
                            runLater {
                                showStopDialog(stage, "提示", "新版本已成功下载,点击确认即可打开新版本!", File(file))
                            }
                            alert.close()
                        }

                        override fun onError(e: IOException) {
                            println(e.message)
                            alert.close()
                        }
                    })
        }
    }

    /**
     * 动态生成下载对话框的内容布局
     */
    private fun generateDownloadView(): Node {
        val jfxSp = JFXSpinner(0.0)
        jfxSp.bind(simpleDoubleProperty)
        val speedLabel = Text()
        speedLabel.bind(speedSSP)
        val label = Label("下载中,请稍后")
        val vBox = VBox(10.0, speedLabel, label)
        vBox.style {
            alignment = Pos.CENTER_LEFT
        }
        val space = Label()
        space.setPrefSize(30.0, 2.0)
        val hbox = HBox(20.0, space, jfxSp, vBox)
        hbox.style {
            alignment = Pos.CENTER_LEFT
        }
        return hbox
    }
}

/**
 * 展示不确定进度加载对话框(动态图+文字 居中显示)
 *
 * @param parent
 * @param iv 传null则显示material的圆形进度条,使用resources.imageView方法构造imageview
 * @param stageWidth 加载框的宽度
 * @param stageHeight 加载框的高度
 * @param labelText 文字
 * @param hasBackGround 是否有背景,true则显示白色背景,false即透明
 * @param task 异步任务,里面需要开启一个子线程,需要在耗时任务结束后手动调用stage.hide方法关闭对话框
 */
fun showLoadingDialog(parent: Stage?, iv: ImageView?, stageWidth: Double, stageHeight: Double, labelText: String = "加载中", hasBackGround: Boolean = false, task: (stage: Stage) -> Unit) {
    val stage = Stage()

    stage.initOwner(parent)
    // style
    stage.initStyle(StageStyle.UNDECORATED)
    stage.initStyle(StageStyle.TRANSPARENT)
    stage.initModality(Modality.NONE)

    val vbox = VBox(10.0)
    if (hasBackGround) {
        vbox.background = Background(BackgroundFill(Color.WHITE, null, null))
    } else {
        vbox.background = Background.EMPTY
    }
    val label = Text(labelText)
    vbox.prefWidth = stageWidth
    vbox.prefHeight = stageHeight
    vbox.alignment = Pos.CENTER
    if (iv == null) {
        val jfxSp = JFXSpinner(-1.0)
        vbox.children.addAll(jfxSp)
    } else {
        vbox.children.addAll(iv)
    }
    vbox.children.addAll(label)


    // scene
    val scene = Scene(vbox)
    scene.fill = null
    stage.scene = scene
    stage.width = vbox.prefWidth
    stage.height = vbox.prefHeight
    parent?.let {
        // show center of parent
        val x = parent.x + (parent.width - stage.width) / 2
        val y = parent.y + (parent.height - stage.height) / 2
        stage.x = x
        stage.y = y
        task.invoke(stage)

        stage.show()
    }
}

/**
 * 右下角弹出提示
 *
 * @param stage 窗口
 * @param title 标题
 * @param content 内容
 * @param closeTime 自动关闭倒计时(s) 默认为3s
 */
fun showDialogPopup(stage: Stage?, title: String, content: String, closeTime: Double = 3.0) {
    val popup = Popup()
    val vbox = VBox()
    vbox.style {
        backgroundColor += c("black")
        padding = box(20.px)
    }
    vbox.prefWidth = 300.0
    val title = Text(title)
    title.style {
        fill = c("white")
        fontWeight = FontWeight.BOLD
        fontSize = 18.px
    }
    val content = Text(content)
    content.wrappingWidth = 300.0
    content.style {
        fontSize = 16.px
        fill = c("white")
    }

    vbox.spacing = 20.0
    vbox.children.add(title)
    vbox.children.add(content)
    popup.content.add(vbox)
    popup.show(stage, TornadoFxUtil.getScreenWidth(), TornadoFxUtil.getScreenHeight())

    //倒计时关闭
    runAsync {
        Thread.sleep((closeTime * 1000).toLong())
    } ui {
        popup.hide()
    }

    //todo 实现队列弹窗
}

/**
 * 显示单选框对话框
 *
 * @param stage
 * @param title 提示
 * @param list 数据源列表
 * @param negativeBtnText 取消按钮文字
 * @param positiveBtnText 确定按钮文字
 * @param lambda 确认的回调函数(label: String, index: Int)
 * @receiver
 */
fun showDialogRadio(stage: Stage?, title: String, list: List<String>, negativeBtnText: String = "取消", positiveBtnText: String = "确定", lambda: ((label: String, index: Int) -> Unit)) {
    val currentSelectIndex = SimpleIntegerProperty()

    val mbox = VBox().apply {
        togglegroup() {
            selectedToggleProperty().addListener { _, _, newValue ->
                currentSelectIndex.set(newValue.userData.toString().toInt())
            }

            list.forEachIndexed { index, s ->
                jfxradiobutton(s) {
                    style {
                        padding = box(10.px)
                    }
                    userData = index
                    if (index == 0) {
                        selectedProperty().set(true)
                    }
                }
            }
        }
    }

    DialogBuilder(stage)
            .setTitle(title)
            .setCustomContext(mbox)
            .setNegativeBtn(negativeBtnText)
            .setPositiveBtn(positiveBtnText) {
                val index = currentSelectIndex.value
                lambda.invoke(list[index], index)
            }
            .create().show()
}
