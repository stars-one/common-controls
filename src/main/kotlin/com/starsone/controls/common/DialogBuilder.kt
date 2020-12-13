package com.starsone.controls.common

import com.jfoenix.controls.*
import com.starsone.controls.model.UpdateInfo
import com.starsone.icontext.MaterialDesignIconText
import com.starsone.icontext.MaterialDesignIconTextFactory
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Border
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import javafx.stage.Modality
import javafx.stage.Stage
import tornadofx.*
import java.awt.Desktop
import java.io.File
import java.net.URL


/**
 * @author StarsOne
 * @date Create in  2019/6/2 0002 20:51
 */
class DialogBuilder(stage: Stage?, modality: Modality = Modality.WINDOW_MODAL) {
    private var message: String? = null
    private var negativeBtn: JFXButton? = null
    private var positiveBtn: JFXButton? = null
    private var negativeBtnPaint = Paint.valueOf("#747474")//否定按钮文字颜色，默认灰色
    private var positiveBtnPaint = Paint.valueOf("#0099ff")
    private var hyperlink: Hyperlink? = null
    private var textField: TextField? = null
    private val dialogLayout = JFXDialogLayout()
    private var alert: JFXAlert<String> = JFXAlert(stage)
    private var onInputListener: ((text: String) -> Unit)? = null
    private var onLoadingListener: ((alert: JFXAlert<String>) -> Unit)? = null

    //右上角的关闭按钮
    lateinit var closeBtn:MaterialDesignIconText
    //是否采用自定义内容
    private var isCustom = false

    init {
        alert.initModality(modality)
        alert.isOverlayClose = false
    }

    fun setTitle(title: String): DialogBuilder {
        val title = Label(title)
        closeBtn = MaterialDesignIconTextFactory.getIconText("close")
        closeBtn.setSize("20px")
        AnchorPane.setLeftAnchor(title, 0.0)
        //设置关闭按钮显示在右上角
        AnchorPane.setRightAnchor(closeBtn, 0.0)
        AnchorPane.setBottomAnchor(closeBtn, 0.0)
        AnchorPane.setTopAnchor(closeBtn, 0.0)
        //添加到对话框头部
        dialogLayout.setHeading(AnchorPane(title, closeBtn))
        title.isFocusTraversable = true
        return this
    }

    /**
     * 设置自定义布局
     */
    fun setCustomContext(node: Node): DialogBuilder {
        dialogLayout.setBody(node)
        isCustom = true
        return this
    }


    fun setMessage(info: UpdateInfo, currentVersion: String): DialogBuilder {
        val messageArray = info.updateContent.split("\\n").toTypedArray()
        val vBox = VBox(10.0)
        val lastVersion = Label("最新版本:")
        lastVersion.style {
            //设置加粗
            fontWeight = FontWeight.BOLD
            //字体大小，第二个参数是单位，一个枚举类型
            fontSize = Dimension(16.0, Dimension.LinearUnits.px)
        }
        vBox.add(HBox(10.0, lastVersion, Label(info.version + "(${info.updateTime})")))
        val version = Label("当前版本:")
        version.style {
            //设置加粗
            fontWeight = FontWeight.BOLD
            //字体大小，第二个参数是单位，一个枚举类型
            fontSize = Dimension(16.0, Dimension.LinearUnits.px)
        }
        vBox.add(HBox(10.0, version, Label(currentVersion)))
        val label = Label("更新内容:")
        label.style {
            //设置加粗
            fontWeight = FontWeight.BOLD
            //字体大小，第二个参数是单位，一个枚举类型
            fontSize = Dimension(16.0, Dimension.LinearUnits.px)
        }
        vBox.add(label)
        for (s in messageArray) {
            vBox.add(Label(s))
        }
        setCustomContext(vBox)
        return this
    }

    fun setMessage(message: String): DialogBuilder {
        this.message = message
        return this
    }

    /**
     * 加载框
     */
    fun setLoadingMessage(message: String, onLoadingListener: ((alert: JFXAlert<String>) -> Unit)): DialogBuilder {
        val label = Label(message)
        val vBox = VBox(label)
        vBox.alignment = Pos.CENTER
        val content = HBox(JFXSpinner(), vBox)
        content.spacing = 30.0
        this.onLoadingListener = onLoadingListener
        return setCustomContext(content)
    }

    /**
     * 设置否定按钮文字和文字颜色
     *
     * @param negativeBtnText 文字
     * @param color           文字颜色 十六进制 #fafafa
     * @return
     */
    fun setNegativeBtn(negativeBtnText: String, color: String): DialogBuilder {
        return setNegativeBtn(negativeBtnText, color, null)
    }

    /**
     * 设置按钮文字和按钮文字颜色，按钮监听器
     *
     * @param negativeBtnText
     * @param negativeBtnOnclickListener
     * @param color                      文字颜色 十六进制 #fafafa
     * @return
     */

    fun setNegativeBtn(negativeBtnText: String, color: String? = null, negativeBtnOnclickListener: (() -> Unit)? = null): DialogBuilder {
        if (color != null) {
            this.negativeBtnPaint = Paint.valueOf(color)
        }
        this.negativeBtn?.isCancelButton = true
        return setNegativeBtn(negativeBtnText, negativeBtnOnclickListener)
    }

    /**
     * 设置按钮文字和点击监听器
     *
     * @param negativeBtnText            按钮文字
     * @param negativeBtnOnclickListener 点击监听器
     * @return
     */
    fun setNegativeBtn(negativeBtnText: String, negativeBtnOnclickListener: (() -> Unit)?): DialogBuilder {

        negativeBtn = JFXButton(negativeBtnText)
        negativeBtn!!.isCancelButton = true
        negativeBtn!!.textFill = negativeBtnPaint
        negativeBtn!!.buttonType = JFXButton.ButtonType.FLAT
        negativeBtn!!.setOnAction { _ ->
            alert.hideWithAnimation()
            negativeBtnOnclickListener?.invoke()
        }
        //右上角的关闭按钮与取消按钮绑定同样的事件
        closeBtn.setOnMouseClicked {
            alert.hideWithAnimation()
            negativeBtnOnclickListener?.invoke()
        }
        return this
    }

    /**
     * 设置按钮文字和颜色
     *
     * @param positiveBtnText 文字
     * @param color           颜色 十六进制 #fafafa
     * @return
     */
    fun setPositiveBtn(positiveBtnText: String, color: String): DialogBuilder {
        return setPositiveBtn(positiveBtnText, color, null)
    }

    /**
     * 设置按钮文字，颜色和点击监听器
     *
     * @param positiveBtnText            文字
     * @param positiveBtnOnclickListener 点击监听器
     * @param color                      颜色 十六进制 #fafafa
     * @return
     */
    fun setPositiveBtn(positiveBtnText: String, color: String? = null, positiveBtnOnclickListener: (() -> Unit)? = null): DialogBuilder {
        if (color != null) {
            this.positiveBtnPaint = Paint.valueOf(color)
        }
        return setPositiveBtn(positiveBtnText, positiveBtnOnclickListener)
    }

    /**
     * 设置按钮文字和监听器
     *
     * @param positiveBtnText            文字
     * @param positiveBtnOnclickListener 点击监听器
     * @return
     */
    fun setPositiveBtn(positiveBtnText: String, positiveBtnOnclickListener: (() -> Unit)?): DialogBuilder {
        positiveBtn = JFXButton(positiveBtnText)
        positiveBtn!!.isDefaultButton = true
        positiveBtn!!.textFill = positiveBtnPaint
        positiveBtn!!.setOnAction { _ ->
            alert.hideWithAnimation()
            positiveBtnOnclickListener?.invoke()
        }
        return this
    }


    /**
     * 设置超链接（文件输出路径，网址跳转），会自动打开指定浏览器或者是资源管理器执行操作
     *
     * @param text 文件的路径，或者是网址，
     * @return
     */
    fun setHyperLink(text: String, isUrl: Boolean): DialogBuilder {
        hyperlink = Hyperlink(text)
        hyperlink!!.border = Border.EMPTY
        hyperlink?.action {
            if (isUrl) {
                val result = TornadoFxUtil.completeUrl(text)
                Desktop.getDesktop().browse(URL(result).toURI())
            } else {
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    Runtime.getRuntime().exec("explorer.exe /select,$text")
                } else {
                    Desktop.getDesktop().open(File(text))
                }
            }
        }
        return this
    }

    /**
     * 设置输入框
     */
    fun setTextFieldText(hintText: String, onInputListener: ((text: String) -> Unit)): DialogBuilder {
        val jfxtf = JFXTextField()
        jfxtf.promptText = hintText
        this.textField = jfxtf
        this.onInputListener = onInputListener
        return this
    }

    /**
     * 创建对话框并显示
     *
     * @return JFXAlert<String>
     */
    fun create(): JFXAlert<String> {

        if (!isCustom) {
            //添加hyperlink超链接文本或者是输入框
            when {

                hyperlink != null -> dialogLayout.setBody(HBox(Label(this.message), hyperlink))
                textField != null -> {
                    dialogLayout.setBody(VBox(Label(this.message), textField))
                    positiveBtn!!.setOnAction { _ ->
                        alert.result = textField!!.text
                        alert.hideWithAnimation()
                    }
                }
                else -> dialogLayout.setBody(VBox(Label(this.message)))
            }
        } else {
            onLoadingListener?.invoke(alert)
        }

        //添加确定和取消按钮
        if (negativeBtn != null && positiveBtn != null) {
            dialogLayout.setActions(negativeBtn, positiveBtn)
        } else {
            if (negativeBtn != null) {
                dialogLayout.setActions(negativeBtn)
            } else if (positiveBtn != null) {
                dialogLayout.setActions(positiveBtn)
            }
        }

        alert.setContent(dialogLayout)
        if (textField != null) {
            alert.showAndWait()
            //不为空，则回调接口
            if (alert.result is String) {
                onInputListener?.invoke(alert.result)
            }

        } else {
            alert.show()

        }

        return alert
    }

}
