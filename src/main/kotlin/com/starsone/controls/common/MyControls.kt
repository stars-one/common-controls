package com.starsone.controls.common

import com.jfoenix.controls.JFXAlert
import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXSpinner
import com.starsone.controls.common.TornadoFxUtil.Companion.completeUrl
import com.starsone.controls.download.HttpDownloader
import com.starsone.controls.download.LanzouParse
import com.starsone.icontext.icontext
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import kfoenix.jfxbutton
import tornadofx.*
import java.awt.Desktop
import java.io.IOException
import java.net.URI


/**
 * 常用Control的封装
 * @author StarsOne
 * @date Create in  2020/3/24 0024 21:11
 */

/**
 * 设置hypeLink的文本为[text],点击之后打开[url],[url]默认与text相同,右键可复制网址
 */
fun EventTarget.urlLink(text: String, url: String = "", op: (Hyperlink.() -> Unit) = {}): Hyperlink {
    //处理得到正确的网址

    val webUrl = if (url.isBlank()) {
        completeUrl(text)
    } else {
        completeUrl(url)
    }
    //网址转uri
    val uri = URI(webUrl)

    val urlLink = hyperlink(text) {
        style {
            border = null
        }
        action {
            Desktop.getDesktop().browse(uri)
        }
    }
    //创建右键菜单
    val cm = contextmenu {
        item("复制网址") {
            action {
                TornadoFxUtil.copyTextToClipboard(webUrl)
            }
        }
    }
    //设置右键菜单
    urlLink.contextMenu = cm
    return opcr(this, urlLink, op)
}

/**
 * 创建指定宽高的ImageView,单独指定[imgWidth]会生成正方形的图形
 */
fun EventTarget.imageview(url: String, imgWidth: Int, imgHeight: Int = 0, lazyload: Boolean = true, op: ImageView.() -> Unit = {}): ImageView {
    val img = imageview(url) {
        fitWidth = imgWidth.toDouble()
        fitHeight = if (imgHeight == 0) {
            imgWidth.toDouble()
        } else {
            imgHeight.toDouble()
        }
    }
    return opcr(this, img, op)
}

/**
 * 带图标的Context MenuItem(可设置快捷键)
 * -[keyCombination] 快捷键设置:`KeyCombination.keyCombination("ctrl+y")`
 */
fun ContextMenu.iconItem(name: String, imgPath: String, imgWidth: Int = 40, imgHeight: Int = 40, keyCombination: KeyCombination? = null, op: MenuItem.() -> Unit = {}) {

    if (imgPath.isBlank()) {
        MenuItem(name).also {
            keyCombination?.apply { it.accelerator = this }
            op(it)
            this += it
        }
    } else {
        val imgNode = imageview(imgPath) {
            fitWidth = imgWidth.toDouble()
            fitHeight = imgHeight.toDouble()
        }
        MenuItem(name, imgNode).also {
            keyCombination?.apply { it.accelerator = this }
            op(it)
            this += it
        }
    }
}


/**
 * 可选择的文本框(本质是TextField)
 */
fun EventTarget.selectText(text: String, op: (TextField.() -> Unit) = {}): TextField {

    val selectTf = textfield(text) {
        isEditable = false
        //防止默认选中
        isFocusTraversable = false
        style {
            backgroundColor += c("transparent")
        }
    }
    return opcr(this, selectTf, op)
}

/**
 * 图标扁平按钮,单独设置[imgWidth]为正方形按钮
 */
fun EventTarget.jfxbutton(imgPath: String, imgWidth: Int, imgHeight: Int = 0, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val button = jfxbutton {
        graphic = if (imgHeight == 0) {
            imageview(imgPath, imgWidth)
        } else {
            imageview(imgPath, imgWidth, imgHeight)
        }
        setOnMouseEntered {
            style {
                backgroundColor += c(0, 0, 0, 0.1)
                backgroundRadius += box(20.percent)
            }

        }
        setOnMouseExited {
            style {

            }
        }
    }
    return opcr(this, button, op)
}

fun EventTarget.circlejfxbutton(imgPath: String,imgWidth: Int, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val jfxbutton = jfxbutton {
        graphic = imageview(imgPath,imgWidth)
        setOnMouseEntered {
            style {
                backgroundColor += c(0, 0, 0, 0.1)
                backgroundRadius += box(50.percent)
            }

        }
        setOnMouseExited {
            style {

            }
        }
    }
    return opcr(this, jfxbutton, op)
}
/**
 * 圆形图标扁平按钮(鼠标滑过会有阴影)
 */
fun EventTarget.circlejfxbutton(icon: Node, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val jfxbutton = jfxbutton {
        graphic = icon
        setOnMouseEntered {
            style {
                backgroundColor += c(0, 0, 0, 0.1)
                backgroundRadius += box(50.percent)
            }

        }
        setOnMouseExited {
            style {

            }
        }
    }
    return opcr(this, jfxbutton, op)
}

/**
 * 输出对话框
 */
fun jfxdialog(stage: Stage?, title: String = "", message: String,
              url: String ,
              isUrl:Boolean,
              positiveBtnText: String = "确定",
              modality: Modality = Modality.APPLICATION_MODAL
): JFXAlert<String> {
    return DialogBuilder(stage, modality)
            .setTitle(title)
            .setMessage(message)
            .setHyperLink(url,isUrl)
            .setPositiveBtn(positiveBtnText)
            .create()

}
/**
 * 消息提示框(只有确定按钮)
 */
fun jfxdialog(stage: Stage?, title: String = "", message: String,
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
fun jfxdialog(stage: Stage?, title: String,
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
fun loadingDialog(stage: Stage?, title: String, message: String,onLoadingListener:((alert: JFXAlert<String>)->Unit)): JFXAlert<String> {
    return DialogBuilder(stage)
            .setTitle(title)
            .setLoadingMessage(message,onLoadingListener)
            .setPositiveBtn("确定")
            .create()
}


/**
 * 文件输入框+选择按钮
 * - [fileTypes] 文件类型,以逗号隔开 ,例如:`java,xml`
 * - [fileDesc] 文件提示
 * - [imgPath] 图片路径
 */
fun EventTarget.filetextfield(fileTypes: String,fileDesc:String,imgPath: String = "", imgWidth: Int = 0, imgHeight: Int = 0, op: (HBox.() -> Unit) = {}): HBox {
    val hbox = hbox {

        val tf = textfield {
            //根据图片设置文本框的高度
            setOnDragExited { event ->
                val files = event.dragboard.files
                //获得文件
                val file = files[0]
                text = file.path
            }
        }
        if (imgPath.isBlank()) {
            //普通按钮
            jfxbutton(graphic = icontext("folder-open", "20px", "blue")) {
                action {
                    val split = fileTypes.split(",")
                    val fileTypeList = split.map { "*.$it" }
                    val files = chooseFile("选择文件", arrayOf(FileChooser.ExtensionFilter(fileDesc, fileTypeList)))
                    if (files.isNotEmpty()) {
                        tf.text = files[0].path
                    }
                }
            }
        } else {
            //图片按钮
            jfxbutton(imgPath, imgWidth, imgHeight) {
                action {
                    val split = fileTypes.split(",")
                    val fileTypeList = split.map { "*.$it" }
                    val files = chooseFile("选择文件", arrayOf(FileChooser.ExtensionFilter(fileDesc, fileTypeList)))
                    if (files.isNotEmpty()) {
                        tf.text = files[0].path
                    }
                }
            }
        }

    }
    return opcr(this, hbox, op)
}

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
 * 关闭程序的对话框
 */
fun stopDialog(stage: Stage?, title: String, message: String): JFXAlert<String> {
    return DialogBuilder(stage)
            .setTitle(title)
            .setMessage(message)
            .setPositiveBtn("确定") { stage?.close() }
            .create()
}

/**
 * 下载进度对话框
 */
class DownloadDialogView(val stage: Stage?, val url: String, val file: String = "") {

    val speedSSP = SimpleStringProperty("")
    val simpleDoubleProperty = SimpleDoubleProperty(0.0)
    val content = generateDownloadView()

    fun show() {
        //todo 取消下载
        val alert = DialogBuilder(stage)
                .setCustomContext(content)
                .setNegativeBtn("后台下载")
                .create()
        runAsync {
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
                                stopDialog(stage, "提示", "新版本已下载,请点击确定结束当前程序,之后打开新版本使用吧~")
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