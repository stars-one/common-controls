
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.input.MouseButton
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import tornadofx.*

/**
 * XRecyclerView
 * @author StarsOne
 * @url <a href="http://stars-one.site">http://stars-one.site</a>
 * @date create in  2020/11/19 10:21
 */
class XRecyclerView<beanT : Any, itemViewT : View> : View() {

    override val root = scrollpane(true)

    lateinit var adapter: RvAdapter<beanT, itemViewT>

    var noDataVBox: VBox? = null

    /**
     * 设置adapter
     *
     * @param adapter
     * @param isGrid 是否网格排列
     * @return
     */
    fun setRvAdapter(adapter: RvAdapter<beanT, itemViewT>, gridLayoutSetting: GridLayoutSetting? = null): XRecyclerView<beanT, itemViewT> {
        this.adapter = adapter
        val beanObList = adapter.rvDataObservableList.beanObList
        val itemViewObList = adapter.rvDataObservableList.itemViewObList

        beanObList.onChange { change ->
            while (change.next()) {
                val from = change.from
                val to = change.to

                when {
                    change.wasPermutated() -> println("permutated (${change.from} ,${change.to})")
                    change.wasUpdated() -> println("replace (${change.from} ,${change.to})")
                    change.wasReplaced() -> {
                        val itemView = itemViewObList[from] as itemViewT
                        //重新绑定数据
                        adapter.onBindData(itemView, beanObList[from], from)
                        itemViewObList[from] = itemView
                    }
                    else -> {
                        when {
                            change.wasAdded() -> {
                                for (i in from until to) {
                                    itemViewObList.add(from, createItemView(beanObList[from], from))
                                }
                            }
                            change.wasRemoved() -> {
                                //clear对应的判断
                                if (beanObList.size == 0 && from == 0 && to == 0) {
                                    itemViewObList.clear()
                                } else {
                                    itemViewObList.remove(from, from + 1)
                                }
                            }
                        }
                    }
                }
            }

        }


        val container = if (gridLayoutSetting != null) {
            val pane = FlowPane(Orientation.HORIZONTAL)
            pane.hgap = gridLayoutSetting.hspace
            pane.vgap = gridLayoutSetting.vspace
            pane.padding = insets(10)
            pane
        } else {
            VBox()
        }

        container.fitToParentHeight()
        container.fitToParentWidth()
        root.add(container)
        root.fitToParentHeight()
        root.fitToParentWidth()


        itemViewObList.onChange { change ->

            val nodeList = container.children

            //如果存在暂无数据视图,则清除
            if (nodeList.contains(noDataVBox)) {
                nodeList.clear()
                container.setAlignment(gridLayoutSetting!=null, Pos.TOP_LEFT)
            }

            while (change.next()) {
                val from = change.from
                val to = change.to
                when {
                    change.wasPermutated() -> println("itemViewList permutated (${change.from} ,${change.to})")
                    change.wasUpdated() -> println("itemViewList replace (${change.from} ,${change.to})")
                    change.wasReplaced() -> {
                        nodeList[from] = itemViewObList[from].root
                    }
                    else -> {
                        when {
                            change.wasAdded() -> {
                                for (i in from until to) {
                                    nodeList.add(from, itemViewObList[from].root)
                                }
                            }
                            change.wasRemoved() -> {

                                if (beanObList.size == 0 && from == 0 && to == 0) {
                                    nodeList.clear()

                                    //当数据为0时,显示暂无数据占位图
                                    noDataVBox?.let {
                                        container.fitToParentWidth()
                                        container.fitToParentHeight()
                                        container.setAlignment(gridLayoutSetting!=null, Pos.CENTER)
                                        nodeList.add(it)
                                    }
                                } else {
                                    nodeList.remove(from, from + 1)
                                }
                            }
                        }

                    }
                }
            }
        }
        //当设置的时候有数据,需要进行初始化操作
        if (beanObList.size > 0) {
            beanObList.forEachIndexed { index, bean ->
                itemViewObList.add(createItemView(bean, index))
            }
        } else {
            noDataVBox?.let {
                //减少20,保持scrollpane不显示滚动条
                container.prefHeight = root.prefHeight - 20
                container.prefWidth = root.prefWidth - 20
                container.setAlignment(gridLayoutSetting!=null, Pos.CENTER)
                container.children.add(it)
            }
        }

        return this
    }

    /**
     * 根据bean类创建itemView
     *
     * @param bean
     * @param index
     * @return
     */
    private fun createItemView(bean: beanT, index: Int): itemViewT {
        adapter.let { ada ->
            val itemView = ada.onCreateView(bean,index)
            //绑定bean数据到itemView
            ada.onBindData(itemView, bean, index)
            //设置监听器
            itemView.root.setOnMouseClicked {
                if (it.button == MouseButton.PRIMARY) {
                    //单击事件回调
                    ada.onClick(itemView, index)
                }
                if (it.button == MouseButton.SECONDARY) {
                    //右击事件回调
                    ada.onRightClick(itemView, index)
                }
            }
            return itemView
        }

    }


    /**
     * 设置宽度
     */
    fun setWidth(width: Double): XRecyclerView<beanT, itemViewT> {
        root.prefWidth = width
        return this
    }

    /**
     * 设置[height]
     */
    fun setHeight(height: Double): XRecyclerView<beanT, itemViewT> {
        root.prefHeight = height
        return this
    }

    /**
     * 设置暂无数据的占位图
     *
     * @param iv
     * @return
     */
    fun setNoDataMsg(iv: ImageView, tip: String = ""): XRecyclerView<beanT, itemViewT> {
        noDataVBox = vbox {
            alignment = Pos.CENTER
            this += iv
            text(tip)
        }

        return this
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
}

fun Pane.setAlignment(isFlowPane: Boolean, alignment: Pos) {
    if (isFlowPane) {
        val tempPane = this as FlowPane
        tempPane.alignment = alignment
    } else {
        val tempPane = this as VBox
        tempPane.alignment = alignment
    }
}

class RvDataObservableList<beanT : Any, itemViewT : View> {
    val beanObList = observableListOf<beanT>()
    val itemViewObList = observableListOf<itemViewT>()

    constructor()

    constructor(bean: beanT) {
        beanObList.add(bean)
    }

    constructor(list: List<beanT>) {
        beanObList.addAll(list)
    }

    /**
     * 根据bean对象获取itemView
     *
     * @param bean bean对象
     * @return
     */
    fun getItemView(bean: beanT): itemViewT {
        val index = beanObList.indexOf(bean)
        return itemViewObList[index]
    }

    /**
     * 获取指定下标的itemView
     *
     * @param index 指定下标
     */
    fun getItemView(index: Int): itemViewT {
        return itemViewObList[index]
    }

    fun add(bean: beanT) {
        beanObList.add(bean)
    }

    /**
     * 指定坐标插入新数据
     *
     * @param index
     * @param bean
     */
    fun add(index: Int, bean: beanT) {
        beanObList.add(index, bean)
    }

    fun addAll(list: List<beanT>) {
        //使用addAll方法,onBindData回调会有问题,只会回调首个
        //beanObList.addAll(list)
        list.forEach {
            beanObList.add(it)
        }
    }

    fun addAll(vararg bean: beanT) {
        //使用addAll方法,onBindData回调会有问题,只会回调首个
        //beanObList.addAll(bean)
        bean.forEach {
            beanObList.add(it)
        }
    }

    /**
     * 更新指定索引的数据
     *
     * @param index
     * @param bean
     */
    fun update(index: Int, bean: beanT) {
        beanObList[index] = bean
    }

    /**
     * 更新某个对象
     *
     * @param bean
     * @param newBean
     */
    fun update(bean: beanT, newBean: beanT) {
        val index = beanObList.indexOf(bean)
        beanObList[index] = newBean
    }

    /**
     * 移除某个数据
     *
     * @param bean
     */
    fun remove(bean: beanT) {
        val index = beanObList.indexOf(bean)
        beanObList.remove(index, index + 1)
    }

    /**
     * 移除最后一个数据
     *
     */
    fun removeLast() {
        val lastIndex = beanObList.lastIndex
        beanObList.remove(lastIndex, lastIndex + 1)
    }

    /**
     * 移除指定坐标
     *
     * @param index 指定坐标
     */
    fun remove(index: Int) {
        beanObList.remove(index, index + 1)
        itemViewObList.remove(index, index + 1)
    }

    /**
     * 移除指定坐标列表
     *
     * @param indexList 存放下标的列表
     */
    fun remove(indexList: List<Int>) {
        indexList.forEach {
            remove(it)
        }
    }

    fun remove(vararg indexList: Int) {
        indexList.forEach {
            remove(it)
        }
    }

    /**
     * 移除[from,to)的数据,包含from,不包含to
     */
    fun remove(from: Int, to: Int) {
        beanObList.remove(from, to)
        itemViewObList.remove(from, to)
    }

    /**
     * 移除所有数据
     */
    fun clear() {
        beanObList.clear()
    }


    private fun checkIndex(index: Int): Boolean {
        if (index <= beanObList.lastIndex && index > 0) {
            return true
        } else {
            println("错误!!传入的index为$index,数据列表的长度为${beanObList.size}")
            return false
        }
    }

}

abstract class RvAdapter<beanT : Any, itemViewT : View>(val rvDataObservableList: RvDataObservableList<beanT, itemViewT>) {

    /**
     * 设置返回ItemView
     */
    abstract fun onCreateView(bean: beanT, position: Int): itemViewT

    abstract fun onBindData(itemView: itemViewT, bean: beanT, position: Int)

    abstract fun onClick(itemView: itemViewT, position: Int)//单击

//    abstract fun onDoubleClick(itemView: itemViewT, position: Int)//双击

    abstract fun onRightClick(itemView: itemViewT, position: Int)//右击
}

abstract class ItemViewBase<beanT : Any, itemViewT : View>(title: String?, icon: Node?) : View(title, icon) {
    var isFirst = true

    lateinit var bean: beanT
    lateinit var obList: RvDataObservableList<beanT, itemViewT>

    abstract fun inputChange()

    abstract fun bindData(beanT: beanT)

    fun bindData(obList: RvDataObservableList<beanT, itemViewT>, index: Int) {
        this.obList = obList
        this.bean = obList.beanObList[index]
        bindData(bean)
        isFirst = false
    }

    private fun dataChange() {
        if (!isFirst) {
            inputChange()
        }
    }

    fun setDataChange(vararg fields: Control) {
        fields.forEach {
            when {
                it is TextField -> it.textProperty().onChange { dataChange() }
                it is Button -> it.textProperty().onChange { dataChange() }
                it is Label -> it.textProperty().onChange { dataChange() }
            }
        }

    }
}



/**
 *
 * @property vspace 每个item的垂直间距
 * @property hspace 每个item的水平间距
 * @constructor Create empty Grid layout setting
 */
data class GridLayoutSetting(var vspace: Double, var hspace: Double)
