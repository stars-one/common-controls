package com.starsone.controls

import GridLayoutSetting
import RvAdapter
import RvDataObservableList
import XRecyclerView
import com.starsone.controls.common.remixIconButton
import com.starsone.controls.common.remixIconButtonWithBorder
import com.starsone.controls.common.showLoadingDialog
import com.starsone.controls.common.xUrlLink
import tornadofx.*

/**
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date create in  2020/11/19 10:21
 */
class RvTestView : View("My View") {
    val dataList = RvDataObservableList<Person, MyItemView>()

    override val root = vbox {
        setPrefSize(1000.0, 400.0)
       /* val p = Person("hello",Math.random().toString())
        dataList.add(p)*/


        hbox{
            xUrlLink("测试","www.stars-one.site")


            //remixIconLabel("home-4-fill")

            remixIconButton("home-4-fill")

            remixIconButton("home-4-fill")

            remixIconButtonWithBorder("home-4-fill")

        }

        hbox {
            button("添加"){
                action{
                    dataList.add(Person("hello", Math.random().toString()))
                }
            }
            button("移出末尾"){
                action{
                    dataList.removeLast()
                }
            }
            button("下标3插入数据"){
                action{
                    dataList.add(3, Person("hx", "xxx"))
                }
            }
            button("添加一组数据"){
                action{
                    dataList.addAll(Person("hx", "xx0x"), Person("hx", "xx1x"))
                }
            }
            button("移除所有"){
                action{
                    dataList.clear()
                }
            }
            button("更新下标3"){
                action{
                    dataList.update(3, Person("xxsw", "13"))
                }
            }
            button("改变下标为0的数据") {
                action{
                    val temp = dataList.beanObList[0]
                    temp.name = "12wx"
                    dataList.beanObList[0] =temp
                }
            }
            button("弹窗"){
                action{
                    showLoadingDialog(currentStage,"提示","加载中","取消"){}
                }
            }
            button{
                action{
                    val beanList = dataList.beanObList
                    println(beanList)
                }
            }
        }

        val adapter = object : RvAdapter<Person, MyItemView>(dataList) {

            override fun onBindData(itemView: MyItemView, bean: Person, position: Int) {
                itemView.bindData(dataList,position)
            }

            override fun onClick(itemView: MyItemView, position: Int) {
                println("单击$position")
            }

            override fun onRightClick(itemView: MyItemView, position: Int) {
                println("右击$position")
            }

            override fun onCreateView(bean: Person, position: Int): MyItemView {
                return MyItemView()
            }

        }

        val iv = resources.imageview("/nodata.png")
        iv.isPreserveRatio = true
        iv.fitWidth = 350.0
        this+= XRecyclerView<Person, MyItemView>()
                .setWidth(this.prefWidth)
                .setHeight(300.0)
                .setNoDataMsg(iv)
                .setRvAdapter(adapter, GridLayoutSetting(10.0, 5.0))

    }
}
