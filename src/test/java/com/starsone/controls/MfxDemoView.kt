package com.starsone.controls

import io.github.palexdev.materialfx.controls.MFXToggleButton
import javafx.beans.property.SimpleBooleanProperty
import tornadofx.*

/**
 * MFX组件的测试
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date create in  2020/11/19 10:21
 */
class MfxDemoView : View("My View") {

    override val root = vbox {
        setPrefSize(600.0, 400.0)
        style {
            backgroundColor += c("white")
        }

        val selectBooleanProperty = SimpleBooleanProperty()

        //MFX对象的组件都需要这个需要手动导包
        this+= MFXToggleButton("开启").apply {
            setColors(c("blue"),c("red"))
            selectedProperty().addListener { _, _, newValue ->
                selectBooleanProperty.set(newValue)
            }
            selectBooleanProperty.set(selectBooleanProperty.value)
            //setMainColor()
        }
    }

}
