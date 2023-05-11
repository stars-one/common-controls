package com.starsone.controls.common

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXToggleButton
import com.starsone.controls.utils.TornadoFxUtil
import com.starsone.controls.utils.TornadoFxUtil.Companion.completeUrl
import javafx.animation.Interpolator
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCombination
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.stage.FileChooser
import javafx.util.Duration
import kfoenix.jfxbutton
import tornadofx.*
import java.awt.Desktop
import java.io.File
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
    val img = imageview(url, lazyload) {
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
fun EventTarget.xCircleJfxButton(imgPath: String, imgWidth: Int, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val jfxbutton = jfxbutton {
        graphic = xImageView(imgPath, imgWidth)
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
 * 圆形按钮
 *
 * @param node 图片
 * @param btnBgColor 背景色,默认白色
 * @param textColor 文本颜色,默认黑色
 * @param buttonType 按钮类型,默认悬浮
 * @param borderColor 边框颜色,默认为null
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xIconButton(node: Node, btnBgColor: Paint = c("white"), textColor: Paint = c("balck"), buttonType: JFXButton.ButtonType = JFXButton.ButtonType.RAISED, borderColor: Paint? = null, op: (JFXButton.() -> Unit) = {}): JFXButton {
    val jfxbutton = jfxbutton {
        val width = 54.0
        setPrefSize(width, width)
        graphic = node

        style {
            backgroundColor += btnBgColor
            textFill = textColor
            borderRadius += box(50.percent)
            backgroundRadius += box(50.percent)

            borderColor?.let {
                //设置边框和颜色
                borderWidth += box(1.px)
                this.borderColor += box(it)
            }
        }
        this.buttonType = buttonType
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
fun EventTarget.xCircleJfxButton(fileTypes: String, fileDesc: String, imgPath: String = "", imgWidth: Int = 0, imgHeight: Int = 0, op: (HBox.() -> Unit) = {}): HBox {
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

        textfield(myfilepath) {
            setOnDragOver {
                it.acceptTransferModes(TransferMode.LINK, TransferMode.COPY, TransferMode.MOVE)
            }
            setOnDragExited {
                val dragboard = it.dragboard
                val flag = dragboard.hasFiles()
                if (flag) {
                    val files = dragboard.files
                    myfilepath.set(files.first().path)
                }
            }
            promptText = "输入文件路径或拖放文件到此处"
            prefWidth = 400.0
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

/**
 * 选择文件夹
 *
 * @param tip 提示
 * @param filePathProperty 绑定的数据
 * @param textFieldWidth 输入框的宽度
 * @param node 选择文件的按钮(传null则使用默认)
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xChooseFileDirectory(tip: String, filePathProperty: SimpleStringProperty, textFieldWidth: Double = 400.0, node: Button? = null, op: (HBox.() -> Unit) = {}): HBox {
    val hbox = hbox {

        alignment = Pos.CENTER_LEFT

        textfield(filePathProperty) {
            prefWidth = textFieldWidth
            promptText = tip
            style {
                backgroundColor += c("#f5f5f5")
                promptTextFill = c("#3e3e3e")
                textFill = c("black")
                backgroundRadius += box(10.px)
                padding = box(10.px)
            }
            promptText = "输入或选择$tip"
        }

        val action = {
            val filePath = filePathProperty.value
            val dirFile = File(filePath)

            val flag = !dirFile.exists()

            //文件路径为空或文件夹不存在,则不选中文件夹时候不打开指定目录
            val file = if (filePath.isBlank() || flag) {
                chooseDirectory("选择$tip")
            } else {
                chooseDirectory("选择$tip", dirFile)
            }

            if (file != null) {
                filePathProperty.set(file.path)
            } else {
                //没有选择,检测一下当前的路径是否正确
                if (flag) {
                    //文件不存在,清空数据
                    filePathProperty.set("")
                }
            }
        }

        if (node == null) {
            //普通按钮
            jfxbutton {
                graphic = remixIconText("folder-2-fill") {
                    style {
                        fill = c("#ffad42")
                    }
                }
                action {
                    action.invoke()
                }
            }
        } else {
            node.apply {
                action {
                    action.invoke()
                }
            }
            add(node)
        }
    }
    return opcr(this, hbox, op)
}

/**
 * 开关组件
 * - 想要文字在前,图标在后,可以设置属性`nodeOrientation = NodeOrientation.RIGHT_TO_LEFT`
 * - 修改颜色属性示例代码
```
toggleColor = c("blue")
toggleLineColor =c("red")
unToggleColor = c("green")
unToggleLineColor = c("orange")
```
 * @param desc 描述内容
 * @param property 监听
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xSwitch(desc: String, property: Property<Boolean>? = null, op: (JFXToggleButton.() -> Unit) = {}): JFXToggleButton {
    val node = JFXToggleButton().apply {
        text = desc
        if (property != null) bind(property)
    }
    return opcr(this, node, op)
}

/**
 * Toggle button
 *
 * @param desc 描述
 * @param property
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.toggleButton(desc: String, property: Property<Boolean>? = null, op: (ToggleButton.() -> Unit) = {}): ToggleButton {
    val node = ToggleButton(desc).apply {
        if (property != null) bind(property)
    }
    return opcr(this, node, op)
}

fun ToggleButton.bind(property: Property<Boolean>, readonly: Boolean = false) =
        selectedProperty().internalBind(property, readonly)

fun <T> Property<T>.internalBind(property: ObservableValue<T>, readonly: Boolean) {
    ViewModel.register(this, property)

    if (readonly || (property !is Property<*>)) bind(property) else bindBidirectional(property as Property<T>)
}

/**
 * 水平自动滚动公告栏
 * @param simpleNotice 为了好的展示效果,这里最好是开头加上8个空格,还有注意处理换行符
 * @param onClickAction 点击公告事件
 * @param width 公告栏的宽度
 * @param speed 展示速度,默认1s滚动8字
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xNoticeBar(simpleNotice: Property<String>, onClickAction: ((String) -> Unit)? = null, width: Double = 560.0, speed: Int = 8, op: (ScrollPane.() -> Unit) = {}): ScrollPane {
    val node = ScrollPane().apply {
        this.prefWidth = width

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
                    val time = newValue.length / speed
                    translateXProperty().animate(-(newValue.length * 10), Duration.seconds(time.toDouble()), Interpolator.LINEAR) {
                        cycleCount = -1
                        setOnFinished {
                            translateXProperty().set(defaultValue)
                        }
                    }
                }
                simpleNotice.onChange {
                    rollAction.invoke(it ?: "")
                }
                setOnMouseClicked {
                    onClickAction?.invoke(simpleNotice.value)
                }
                rollAction.invoke(simpleNotice.value)
            }
        }
    }

    return opcr(this, node, op)
}

/**
 * 圆形头像框
 *
 */
fun EventTarget.xCircleImageView(url: String? = null, fitWidth: Double, lazyload: Boolean = true, op: ImageView.() -> Unit = {}) =
        opcr(this, if (url == null) ImageView().apply {
            this.fitWidth = fitWidth
            this.fitHeight = fitWidth
            clip = Circle(fitHeight / 2, fitWidth / 2, fitWidth / 2)
        } else ImageView(Image(url, lazyload)), op).apply {
            this.fitWidth = fitWidth.toDouble()
            this.fitHeight = fitWidth.toDouble()
            clip = Circle(fitHeight / 2, fitWidth / 2, fitWidth / 2)
        }

fun EventTarget.xCircleImageView(
        url: ObservableValue<String>,
        fitWidth: Double,
        lazyload: Boolean = true,
        op: ImageView.() -> Unit = {}
) = ImageView().attachTo(this, op) { imageView ->
    imageView.imageProperty().bind(objectBinding(url) { value?.let { Image(it, lazyload) } })
    imageView.apply {
        this.fitWidth = fitWidth
        this.fitHeight = fitWidth
        clip = Circle(fitHeight / 2, fitWidth / 2, fitWidth / 2)
    }
}

fun EventTarget.xCircleImageView(image: ObservableValue<Image?>, fitWidth: Double, op: ImageView.() -> Unit = {}) = ImageView().attachTo(this, op) {
    it.imageProperty().bind(image)
    it.apply {
        this.fitWidth = fitWidth
        this.fitHeight = fitWidth
        clip = Circle(fitHeight / 2, fitWidth / 2, fitWidth / 2)
    }
}

fun EventTarget.xCircleImageView(image: Image, fitWidth: Double, op: ImageView.() -> Unit = {}) = ImageView(image).apply {
    this.fitWidth = fitWidth
    this.fitHeight = fitWidth
    clip = Circle(fitHeight / 2, fitWidth / 2, fitWidth / 2)
}.attachTo(this, op)

/**
 * tag标签
 *
 * @param tagText 文字
 * @param bgColor 背景色
 * @param textColor 文本颜色
 * @param bgRadius 边框圆角
 * @param op
 * @receiver
 * @return
 */
fun EventTarget.xTag(tagText: ObservableValue<String>, bgColor: Paint = c("#67c23a"), textColor: Paint = c("white"), bgRadius: Int = 8, op: (Label.() -> Unit) = {}): Label {
    val vbox = label(tagText) {
        padding = insets(6)
        style {
            backgroundColor += bgColor
            textFill = textColor
            backgroundRadius += box(bgRadius.px)
        }
    }
    return opcr(this, vbox, op)
}

fun EventTarget.xTag(tagText: String, bgColor: Paint = c("#67c23a"), textColor: Paint = c("white"), bgRadius: Int = 8, op: (Label.() -> Unit) = {}): Label {
    val vbox = label(tagText) {
        padding = insets(6)
        style {
            backgroundColor += bgColor
            textFill = textColor
            backgroundRadius += box(bgRadius.px)
        }
    }
    return opcr(this, vbox, op)
}

/**
 * 倒计时按钮组件(类似发送验证码)
 *
 * @param text 文本提示
 * @param countDownTime 倒计时间
 * @param action 按钮点击事件
 * @param op
 * @receiver
 * @receiver
 * @return
 */
fun EventTarget.xCountDownBtn(text: String, countDownTime: Int, action:()->Unit, op: (JFXButton.() -> Unit) = {}): JFXButton {
    //倒计时的按钮提示
    val tip = text
    val codeTip = SimpleStringProperty(tip)
    val time = SimpleIntegerProperty(0)

    val vbox = jfxbutton(codeTip) {
        disableWhen { time.greaterThan(0) }

        time.addListener { _, _, newValue ->
            runLater {
                if (newValue == 0) {
                    codeTip.value = tip
                } else {
                    codeTip.value = "$tip(${newValue}S)"
                }
            }
        }
        action {
            action.invoke()

            time.value = countDownTime
            runAsync {
                repeat(60) {
                    runLater {
                        time.value = time.value - 1
                    }
                    Thread.sleep(1000)
                }
            }

        }
    }

    return opcr(this, vbox, op)
}
