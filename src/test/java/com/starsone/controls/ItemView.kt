package com.starsone.controls
import ItemViewBase
import javafx.scene.control.TextField
import tornadofx.*

/**
 *
 * @author StarsOne
 * @date Create in  2020/1/21 0021 18:36
 * @description
 *
 */

data class Person(var name: String, var age: String)

class MyItemView : ItemViewBase<Person, MyItemView>(null,null){
    var nameText by singleAssign<TextField>()
    var ageText by singleAssign<TextField>()

    /**
     * 设置输入的改变,当用户输入内容时候,同时改变obList中的beanList数据
     *
     */
    override fun inputChange() {
        val name = nameText.text
        val age = ageText.text
        val copy = bean.copy()
        copy.name = name
        copy.age = age
        obList.update(bean, copy)
    }

    /**
     * 初次设置数值,直接设置相关控件对象的数值
     *
     * @param beanT
     */
    override fun bindData(beanT: Person) {
        nameText.text = beanT.name
        ageText.text = beanT.age
    }

    override val root = hbox {

        nameText = textfield() {

        }
        ageText = textfield {
        }

        //这里必须要调用此方法,将当前所有输入控件传入,进行设置数值变化监听
        setDataChange(nameText,ageText)
    }
}


