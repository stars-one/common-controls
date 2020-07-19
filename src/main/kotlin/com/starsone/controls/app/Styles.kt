package com.starsone.controls.app

import javafx.scene.text.FontWeight
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val heading by cssclass()
        val iconBtn by cssclass()
    }

    init {
        label and heading {
            padding = box(10.px)
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
        }
        iconBtn{
            textFill = c("blue")
            and(hover){
                backgroundColor += c(0, 0, 0, 0.1)
                backgroundRadius += box(50.percent)
            }

        }
    }
}