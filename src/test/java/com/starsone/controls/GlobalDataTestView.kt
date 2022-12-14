package com.starsone.controls

import com.starsone.controls.common.xSwitch
import com.starsone.controls.utils.GlobalDataConfig
import com.starsone.controls.utils.GlobalDataConfigUtil
import tornadofx.*


class GlobalDataTestView : View("My View") {
    val viewModel by inject<GlobalDataTestViewModel>()

    override val root = vbox {


        button{
            action{
                preferences("application") {
                    putBoolean("boolean", true)
                }
            }
        }


        prefWidth = 800.0
        prefHeight = 500.0

        checkbox("开启某功能", viewModel.booleanFlag)

        xSwitch("开启开关",viewModel.booleanFlag)

        hbox {
            text("string绑定")
            textfield(viewModel.stringFlag)
        }
        hbox{
            text("int绑定")
            //注意,这里如果数字数值过大,会出现逗号
            textfield(viewModel.intFlag)
        }
        hbox{
            text("double绑定")
            textfield(viewModel.doubleFlag)
        }
    }



}

class GlobalDataTestViewModel : ViewModel() {
    val booleanFlag = GlobalDataConfigUtil.getSimpleBooleanProperty(GlobalData.booleanFlag)
    val stringFlag = GlobalDataConfigUtil.getSimpleStringProperty(GlobalData.stringFlag)
    val doubleFlag = GlobalDataConfigUtil.getSimpleDoubleProperty(GlobalData.doubleFlag)
    val intFlag = GlobalDataConfigUtil.getSimpleIntegerProperty(GlobalData.intFlag)


}


class Constants {
    companion object {
        const val BOOLEAN_FLAG = "Boolean_FLAG"
        const val STRING_FLAG = "String_FLAG"
        const val INT_FLAG = "Int_FLAG"
        const val DOUBLE_FLAG = "Double_FLAG"


    }
}

/**
 * 主要是仿写不可变的常量
 */
class GlobalData {
    companion object {

        val booleanFlag = GlobalDataConfig(Constants.BOOLEAN_FLAG, false)

        val doubleFlag = GlobalDataConfig(Constants.DOUBLE_FLAG, 0.0)

        val stringFlag = GlobalDataConfig(Constants.STRING_FLAG, "")

        val intFlag = GlobalDataConfig(Constants.INT_FLAG, 1)

    }
}
