package com.starsone.controls.common

import com.jfoenix.controls.JFXButton
import com.starsone.controls.utils.TornadoFxUtil
import com.starsone.controls.utils.TornadoFxUtil.Companion.completeUrl
import javafx.beans.property.SimpleStringProperty
import javafx.event.EventTarget
import javafx.scene.Node
import javafx.scene.control.ContextMenu
import javafx.scene.control.Hyperlink
import javafx.scene.control.MenuItem
import javafx.scene.control.TextField
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import kfoenix.jfxbutton
import tornadofx.*
import java.awt.Desktop
import java.net.URI

/**
 * 常用Control的封装
 * @author StarsOne
 * @date Create in  2020/3/24 0024 21:11
 */

/**
 * 设置hypeLink的文本为[text],点击之后打开[url],[url]默认与text相同,右键可复制网址
 */
fun EventTarget.xUrlLink(text: String, url: String = "", op: (Hyperlink.() -> Unit) = {}): Hyperlink {
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
fun EventTarget.xImageView(url: String, imgWidth: Int, imgHeight: Int = 0, lazyload: Boolean = true, op: ImageView.() -> Unit = {}): ImageView {
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
fun ContextMenu.xIconItem(name: String, imgPath: String, imgWidth: Int = 40, imgHeight: Int = 40, keyCombination: KeyCombination? = null, op: MenuItem.() -> Unit = {}) {

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
fun EventTarget.xSelectText(text: String, op: (TextField.() -> Unit) = {}): TextField {

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
fun EventTarget.xJfxButton(imgPath: String, imgWidth: Int, imgHeight: Int = 0, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val button = jfxbutton {
        graphic = if (imgHeight == 0) {
            xImageView(imgPath, imgWidth)
        } else {
            xImageView(imgPath, imgWidth, imgHeight)
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

/**
 * 圆形的图标按钮,鼠标悬浮上去会有圆形的阴影
 *
 * @param imgPath 图片的路径
 * @param imgWidth 按钮的宽度,长宽都一样
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xCircleJfxButton(imgPath: String,imgWidth: Int, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val jfxbutton = jfxbutton {
        graphic = xImageView(imgPath,imgWidth)
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
fun EventTarget.xCircleJfxButton(icon: Node, op: (JFXButton.() -> Unit) = {}): JFXButton {
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
 *
 * 文件输入框+选择按钮(过时方法,请改成x
 * - [fileTypes] 文件类型,以逗号隔开 ,例如:`java,xml`
 * - [fileDesc] 文件提示
 * - [imgPath] 图片路径
 */
fun EventTarget.xCircleJfxButton(fileTypes: String, fileDesc:String, imgPath: String = "", imgWidth: Int = 0, imgHeight: Int = 0, op: (HBox.() -> Unit) = {}): HBox {
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
            jfxbutton {
                graphic=text {
                    text="\ueac5"
                    style{
                        font = loadFont("/ttf/iconfont.ttf",18.0)!!
                        fill= c("#ffad42")
                    }
                }
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
            xJfxButton(imgPath, imgWidth, imgHeight) {
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
 * 选择文件(含输入框和按钮),允许拖动文件到输入框自动输入(此功能需要用不是管理员权限打开java引用)
 *
 * @param - [myfilepath] 可观察的String(SimpleStringProperty),与ViewModel里某字段绑定
 * @param - [fileTypes] 文件类型,以逗号隔开 ,例如:`java,xml`
 * @param - [fileDesc] 文件描述
 * @param - [imgPath]
 * @param - [imgWidth]
 * @param - [imgHeight]
 */
fun EventTarget.xChooseFile(myfilepath: SimpleStringProperty, fileTypes: String, fileDesc: String, imgPath: String = "", imgWidth: Int = 0, imgHeight: Int = 0, op: (HBox.() -> Unit) = {}): HBox {
    val hbox = hbox {

        textfield(myfilepath){
            setOnDragOver {
                it.acceptTransferModes(TransferMode.LINK,TransferMode.COPY,TransferMode.MOVE)
            }
            setOnDragExited {
                val dragboard = it.dragboard
                val flag = dragboard.hasFiles()
                if (flag) {
                    val files = dragboard.files
                    myfilepath.set(files.first().path)
                }
            }
        }

        if (imgPath.isBlank()) {
            //普通按钮
            jfxbutton {
                graphic = text {
                    text = "\ueac5"
                    style {
                        font = loadFont("/ttf/iconfont.ttf", 18.0)!!
                        fill = c("#ffad42")
                    }
                }
                action {
                    val split = fileTypes.split(",")
                    val fileTypeList = split.map { "*.$it" }
                    val files = chooseFile("选择文件", arrayOf(FileChooser.ExtensionFilter(fileDesc, fileTypeList)))
                    if (files.isNotEmpty()) {
                        myfilepath.set(files.first().path)
                    }
                }
            }
        } else {
            //图片按钮
            xJfxButton(imgPath, imgWidth, imgHeight) {
                action {
                    val split = fileTypes.split(",")
                    val fileTypeList = split.map { "*.$it" }
                    val files = chooseFile("选择文件", arrayOf(FileChooser.ExtensionFilter(fileDesc, fileTypeList)))
                    if (files.isNotEmpty()) {
                        myfilepath.set(files.first().path)
                    }
                }
            }
        }

    }
    return opcr(this, hbox, op)
}
