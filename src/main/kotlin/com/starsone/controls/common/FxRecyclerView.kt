package com.starsone.controls.common

import javafx.scene.control.ScrollPane
import javafx.scene.input.MouseButton
import javafx.scene.layout.VBox
import tornadofx.*
import java.util.*

/**
 *
 * @author StarsOne
 * @date Create in  2020/1/20 0020 21:19
 * @description
 *
 */
class FxRecyclerView<beanT : Any, itemViewT : View> : View {
    var adapter: RVAdapter<beanT, itemViewT>? = null
        set(value) {
            field = value
            val adapter = value as RVAdapter<beanT, itemViewT>
            val beanList = adapter.beanList
            val itemViewList = adapter.itemViewList
            for (index in 0 until beanList.size) {
                val itemView = adapter.onCreateView()
                //绑定bean数据到itemView
                adapter.onBindData(itemView, beanList[index], index)
                //itemView添加到列表中
                itemViewList.add(itemView)
                //添加到RecyclerView的主容器中
                container.add(itemView)
                itemView.root.setOnMouseClicked {
                    if (it.button == MouseButton.PRIMARY) {
                        //单击事件回调
                        adapter.onClick(itemView, index)
                    }
                    if (it.button == MouseButton.SECONDARY) {
                        //右击事件回调
                        adapter.onRightClick(itemView, index)
                    }
                }
            }
        }

    var container = vbox { }

    constructor() {
        root.add(container)
    }

    constructor(vBox: VBox) {
        container = vBox
        root.add(container)
    }

    override val root = scrollpane {
        vbox { }
    }

    /**
     * 设置宽度
     */
    fun setWidth(width: Double) {
        root.prefWidth = width
    }

    /**
     * 设置[height]
     */
    fun setHeight(height: Double) {
        root.prefHeight = height
    }

    /**
     * 设置水平滚动条的显示方式
     * @param way 显示方式，never(不显示） always（一直显示） asneed（自动根据需要显示）
     */
    fun setIsShowHorizontalBar(way: String) {
        when (way) {
            "never" -> root.hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            "always" -> root.hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            "asneed" -> root.hbarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
        }
    }

    /**
     * 添加一个列表的数据(arraylist)
     */
    fun addList(beanList: ArrayList<beanT>) {
        for (bean in beanList) {
            add(bean)
        }
    }

    /**
     * 添加一个列表的数据(list)
     */
    fun addList(beanList: List<beanT>) {
        for (bean in beanList) {
            add(bean)
        }
    }

    fun add(bean: beanT) {
        val beanList = adapter?.beanList
        val itemViewList = adapter?.itemViewList
        val index = beanList?.size as Int - 1
        beanList.add(bean)
        val itemView = adapter?.onCreateView() as itemViewT
        //invoke onBindData method to bind the bean data to te item view
        adapter?.onBindData(itemView, bean, index)
        //add the item view in the item view list
        itemViewList?.add(itemView)
        //add to the recyclerview container
        container.add(itemView)
        itemView.root.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                //单击
                adapter?.onClick(itemView, index)
            }
            if (it.button == MouseButton.SECONDARY) {
                //右击
                adapter?.onRightClick(itemView, index)
            }

        }
    }

    /**
     * 在列表的指定位置插入指定bean数据对应的itemView。 将当前位于该位置的itemView（如果有）和任何后续的itemView（向其索引添加一个）移动。
     * @param bean bean数据
     * @param index 要插入的下标
     */
    fun add(bean: beanT, index: Int) {
        val beanList = adapter?.beanList
        val itemViewList = adapter?.itemViewList
        beanList?.add(index, bean)
        val itemView = adapter?.onCreateView() as itemViewT
        //invoke onBindData method to bind the bean data to te item view
        adapter?.onBindData(itemView, bean, index)
        //add the item view in the item view list
        itemViewList?.add(index, itemView)
        //add to the recyclerview container
        container.addChildIfPossible(itemView.root, index)
        itemView.root.setOnMouseClicked {
            if (it.button == MouseButton.PRIMARY) {
                //单击
                adapter?.onClick(itemView, index)
            }
            if (it.button == MouseButton.SECONDARY) {
                //右击
                adapter?.onRightClick(itemView, index)
            }
        }
        //更新点击事件的回调
        for (i in index + 1 until itemViewList?.size as Int) {
            val itemView1 = itemViewList[i]
            adapter?.onBindData(itemView1, beanList!![i], i)
            itemView1.root.setOnMouseClicked {
                if (it.button == MouseButton.PRIMARY) {
                    //单击
                    adapter?.onClick(itemView1, i)
                }
                if (it.button == MouseButton.SECONDARY) {
                    //右击
                    adapter?.onRightClick(itemView1, i)
                }
            }
        }
    }

    /**
     * 更新指定位置的itemView
     */
    fun update(bean: beanT, index: Int) {
        remove(index)
        add(bean, index)
    }

    /**
     * 寻找列表中与oldBean相同的第一个元素，将其内容进行修改，同时更新界面的显示
     * @param bean 新的数据
     * @param oldBean 列表中已存在的数据
     */
    fun update(bean: beanT, oldBean: beanT) {
        val beanList = adapter?.beanList
        val index = beanList?.indexOf(oldBean) as Int
        if (index != -1) {
            update(bean, index)
        } else {
            println("列表中不存在该元素")
        }
    }

    fun remove(bean: beanT) {
        val beanList = adapter?.beanList
        val index = beanList?.indexOf(bean) as Int
        remove(index)
    }

    /**
     * 移出指定下标的itemview
     * @param index 下标
     */
    fun remove(index: Int) {
        val beanList = adapter?.beanList
        val itemViewList = adapter?.itemViewList
        beanList?.removeAt(index)
        val itemView = itemViewList!![index]
        itemView.removeFromParent()
        itemViewList.remove(itemView)
        for (i in index until beanList?.size as Int) {
            adapter?.onBindData(itemViewList[i], beanList[i], i)
            val itemView = itemViewList[i]
            itemView.root.setOnMouseClicked {
                if (it.button == MouseButton.PRIMARY) {
                    //单击
                    adapter?.onClick(itemView, i)
                }
                if (it.button == MouseButton.SECONDARY) {
                    //右击
                    adapter?.onRightClick(itemView, i)
                }
            }
        }
    }

    /**
     * 移出所有控件
     */
    fun removeAll() {
        val itemViewList = adapter?.itemViewList as ArrayList<itemViewT>
        val beanList = adapter?.beanList as ArrayList<beanT>
        for (itemView in itemViewList) {
            itemView.removeFromParent()
        }
        itemViewList.removeAll(itemViewList)
        beanList.removeAll(beanList)
    }
}

/**
 * 适配器
 * @author StarsOne
 * @date Create in  2020/1/20 0020 21:51
 * @description
 *
 */
abstract class RVAdapter<beanT : Any, itemViewT : View> {
    val beanList = arrayListOf<beanT>()
    val itemViewList = arrayListOf<itemViewT>()

    constructor(bean: beanT) {
        beanList.add(bean)
    }

    constructor(beanList: List<beanT>) {
        this.beanList.addAll(beanList)
    }

    constructor(beanList: ArrayList<beanT>) {
        this.beanList.addAll(beanList)
    }

    /**
     * 设置返回ItemView
     */
    abstract fun onCreateView(): itemViewT

    abstract fun onBindData(itemView: itemViewT, bean: beanT, position: Int)

    abstract fun onClick(itemView: itemViewT, position: Int)//单击

//    abstract fun onDoubleClick(itemView: itemViewT, position: Int)//双击

    abstract fun onRightClick(itemView: itemViewT, position: Int)//右击
}

