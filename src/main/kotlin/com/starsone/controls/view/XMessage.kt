package com.starsone.controls.view

import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.util.Duration
import tornadofx.*
import java.util.concurrent.TimeUnit

/**
 * XMessage 消息提示
 * 参考 [chenfei-javafx-css项目的CFMessage](https://gitee.com/lichenfei_fei/chenfei-javafx-css/blob/control/src/main/java/cn/chenfei/jfx/css/control/CFMessage.java),增加了调整顺序和绑定任意pane的功能
 * @author stars-one
 * @date 2022-12-14 00:43:07
 */
class XMessage private constructor(stackPane: StackPane) {

    private val messageBox = VBox()

    init {
        messageBox.maxWidth = Double.NEGATIVE_INFINITY
        messageBox.maxHeight = Double.NEGATIVE_INFINITY
        //styleClass
        messageBox.styleClass.add("cf-message-box")
    }


    /**
     * 创建消息提示
     *
     * @param message
     */
    fun create(message: String) {
        val cfAlert = CFAlert.create(message)
        messageBox.children.add(cfAlert)
        transitionPlay(cfAlert)
    }

    /**
     * 创建消息提示
     *
     * @param message
     */
    fun create(message: String, node: Node) {
        val cfAlert = CFAlert.create(message, node)
        messageBox.children.add(cfAlert)
        transitionPlay(cfAlert)
    }

    /**
     * 创建消息提示
     *
     * @param message
     */
    fun create(message: String, alertLevel: AlertLevel) {
        val cfAlert = CFAlert.create(message, alertLevel)
        messageBox.children.add(cfAlert)
        transitionPlay(cfAlert)
    }

    /**
     * 出现，消失动画
     *
     * @param cfAlert
     */
    private fun transitionPlay(cfAlert: CFAlert) {
        //移动动画
        val translateTransition = TranslateTransition(Duration.millis(300.0), cfAlert)
        //透明度动画
        val fadeTransition = FadeTransition(Duration.millis(300.0), cfAlert)
        val parallelTransition = ParallelTransition(translateTransition, fadeTransition)
        //出现动画
        translateTransition.fromY = -30.0
        translateTransition.toY = 0.0
        fadeTransition.fromValue = 0.0
        fadeTransition.toValue = 1.0
        parallelTransition.play()
        parallelTransition.onFinished = EventHandler { event: ActionEvent? ->
            //3秒后关闭
            Thread {
                try {
                    TimeUnit.SECONDS.sleep(3)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                Platform.runLater {

                    //消失动画
                    translateTransition.fromY = 0.0
                    translateTransition.toY = -30.0
                    fadeTransition.fromValue = 1.0
                    fadeTransition.toValue = 0.0
                    parallelTransition.play()
                    parallelTransition.onFinished = EventHandler { event1: ActionEvent? -> messageBox.children.remove(cfAlert) }
                }
            }.start()
        }
    }

    companion object {
        var isTop = true

        /**
         * 绑定容器，一般可以定义一个顶级的容器来存放消息提示
         *
         * @param stackPane
         * @param isTop 通知的出现顺序 true:从上往下 false:从下往上
         * @return
         */
        private fun bindingContainer(stackPane: StackPane, isTop: Boolean = true): XMessage {
            this.isTop = isTop
            return XMessage(stackPane)
        }

        /**
         * 绑定容器，一般可以定义一个顶级的容器来存放消息提示
         *
         * @param pane 任意一个pane即可
         * @param isTop 通知的出现顺序 true:从上往下 false:从下往上
         * @return
         */
        fun bindingContainer(pane: Pane, isTop: Boolean = true): XMessage {
            this.isTop = isTop
            //将vbox里面的节点弄出来,存放在tempVbox

            if (pane is StackPane) {
                return bindingContainer(pane, isTop)
            } else {
                //如果绑定的不是stackpane
                //    1.则判断原来的pane是何种类型,将其所以节点移到同类型的一个tempPane
                //    2.创建stackpane,将tempPane节点放入其中
                //    2.清空原pane的所有节点,将上面创建的stackpane节点添加进去
                val tempPane = when (pane) {
                    is VBox -> VBox()
                    is HBox -> HBox()
                    is StackPane -> StackPane()
                    is AnchorPane -> AnchorPane()
                    else -> StackPane()
                }
                val nodeList = pane.children

                tempPane.children.addAll(nodeList)
                //清除掉所有节点
                pane.clear()
                //将tempVbox使用stackPane包裹起来
                val stackPane = StackPane()
                stackPane.add(tempPane)

                pane.add(stackPane)
                return XMessage(stackPane)
            }
        }
    }

    init {
        stackPane.children.add(messageBox)
        if (isTop) {
            StackPane.setAlignment(messageBox, Pos.TOP_CENTER)
        } else {
            //这里可以改变顺序,从底上往上展示
            StackPane.setAlignment(messageBox, Pos.BOTTOM_CENTER)
        }

    }

}
