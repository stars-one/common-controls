package com.starsone.controls.view

import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.util.Duration
import java.util.concurrent.TimeUnit

/**
 * @author ChenFei
 * @date 2022年7月22日
 *
 *
 * CFMessage 消息提示
 */
class CFMessage private constructor(stackPane: StackPane) {

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
        /**
         * 绑定容器，一般可以定义一个顶级的容器来存放消息提示
         *
         * @param stackPane
         * @return
         */
        fun bindingContainer(stackPane: StackPane): CFMessage {
            return CFMessage(stackPane)
        }
    }

    init {
        stackPane.children.add(messageBox)
        StackPane.setAlignment(messageBox, Pos.TOP_CENTER)
    }

}
