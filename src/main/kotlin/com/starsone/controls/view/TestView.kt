package com.starsone.controls.view

import javafx.geometry.Pos
import kfoenix.jfxspinner
import tornadofx.*

/**
 *
 * @author StarsOne
 * @date Create in  2020/7/17 0017 9:46
 * @description
 *
 */
class TestView : View("My View") {
    override val root = hbox(20.0) {

        jfxspinner{
            progress=0.6245
        }
        vbox(10.0) {
            style{
                alignment = Pos.CENTER
            }
//            label("20.4%")
            label("200.56KB/s")
            label("下载中")
        }
        /*form {
            fieldset {
                field {
                    text("当前版本")
                    text("v1.0")
                }
            }
            fieldset {
                field {
                    text("最新版本")
                    text("v1.0")
                }
            }
            fieldset {
                field {
                    text("更新日期")
                    text("2020-7-10")
                }
            }
            fieldset {
                field {
                    text("更新内容")
                    vbox {
                        text("1.更新是就打开房间看到小即可即发")
                        text("1.更新是就打开房间看到小即可即发")
                        text("1.更新是就打开房间看到小即可即发")
                    }
                }
            }
        }*/
    }
}
