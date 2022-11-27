# CommonControl
为TornadoFx的封装的常用控件与工具,基于[JFoenix](https://github.com/sshahine/JFoenix),借鉴[KFoenix](https://github.com/bkenn/KFoenix)

<meta name="referrer" content="no-referrer">

<img src="https://jitpack.io/v/Stars-One/common-controls.svg" />

这个开源库原本我也不想开源出来的,毕竟花费了自己很久的时间,但是想到TornadoFx国内实在没有多少人用,便是想开源出来了,国内TornadoFx资料较少,有些组件并没有,只能靠自己来造。

在这个前端为主的时代，可能只有我这种人还在独自坚持研究这种小众的技术吧（至少国内是这样的情况）

**希望你在使用之前可以根据自己的实际情况给予打赏，算是对我的鼓励，你的打赏是我后期维护并积极更新的动力！谢谢**

![](https://img2020.cnblogs.com/blog/1210268/202003/1210268-20200316120825333-1551152974.png)

TornadoFX交流群：1071184701

[TornadoFx中文文档](https://stars-one.gitee.io/tornadofx-guide-chinese) 目前还在翻译中

本库包含了之前的[IconText](https://github.com/Stars-One/IconTextFx)和[DialogBuilder](https://github.com/Stars-One/DialogBuilder)

- **[IconText](https://github.com/Stars-One/IconTextFx)** 5000+个Material Design字体图标库

- **[DialogBuilder](https://github.com/Stars-One/DialogBuilder)** 基于Jfoenix的对话框生成器


最新版本1.7

> PS:2.0版本进行了包名结构优化,出现报错,请重新导包即可,且2.0移除了`IconText`图标组件库,使用了`RemixIcon`图标库


## 目录

* [CommonControl](#commoncontrol)
  * [目录](#目录)
  * [引入依赖](#引入依赖)
    * [Maven引入](#maven引入)
    * [Gradle引入](#gradle引入)
  * [介绍](#介绍)
  * [1.常用对话框](#1常用对话框)
    * [普通对话框](#普通对话框)
    * [确认对话框](#确认对话框)
    * [输入对话框](#输入对话框)
    * [输出对话框](#输出对话框)
    * [下载对话框](#下载对话框)
    * [加载对话框](#加载对话框)
    * [关闭程序对话框](#关闭程序对话框)
    * [右下角弹窗](#右下角弹窗)
    * [检测更新对话框](#检测更新对话框)
    * [自定义对话框](#自定义对话框)
  * [2.检测更新功能](#2检测更新功能)
  * [3.常用方法](#3常用方法)
  * [4.常用控件](#4常用控件)
  * [5.下载框架](#5下载框架)
  * [6.XRecyclerView的用法](#6xrecyclerview的用法)
    * [1.创建bean类](#1创建bean类)
    * [2.创建ItemView](#2创建itemview)
    * [3.创建RvDataObservableList和RvAdapter](#3创建rvdataobservablelist和rvadapter)
    * [4.界面添加FxRecyclerView](#4界面添加fxrecyclerview)
    * [5.方法补充](#5方法补充)
  * [7.配置存储类](#7配置存储类)
    * [1.常量池创建](#1常量池创建)
    * [2.工具类的创建](#2工具类的创建)
    * [3.关于GlobalDataConfig方法补充](#3关于globaldataconfig方法补充)
  * [8.系统剪贴板监听](#8系统剪贴板监听)
  * [9.字体图标组件](#9字体图标组件)




## 引入依赖

### Maven引入

**1. 添加仓库**

由于jar包是上传在jitpack仓库中,所以得在项目的pom.xml添加仓库
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```

**2.添加依赖**
```
<dependency>
	<groupId>com.github.Stars-One</groupId>
	<artifactId>common-controls</artifactId>
	<version>1.6</version>
</dependency>
```

### Gradle引入

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

```
dependencies {
	implementation 'com.github.Stars-One:common-controls:1.6'
}
```
## 介绍

注意:考虑到查找的方便,1.6版本之后控件的名字发生了变化,**控件名多个前缀x,且采用了驼峰命名法**,如`jfxbutton`变为了`xJfxButton`**,其他依次类推

控件主要分为以下几个大类:

- 对话框
- 检测更新
- 常用方法
- 常用控件
- 下载框架(另有独立版,不过暂未发布,敬请期待)

## 1.常用对话框
对话框提供了整合了之前的DiglogBuilder,并新增加了加载对话框和自定义对话框内容,参考了Kf项目,我把有些对话框整合成了Kotlin中的DSL方式调用,有些对话框就没有
### 普通对话框

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718194948541-1031130858.png)

```
jfxbutton("测试消息") {
	action {
		jfxdialog(currentStage, "标题", "内容")
	}
}
```
### 确认对话框

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718200034445-114242192.png)

```
jfxbutton("测试确认对话框") {
	action {
		DialogBuilder(currentStage)
				.setTitle(title)
				.setMessage("hello")
				.setNegativeBtn("取消"){ println("点击了取消按钮")}
				.setPositiveBtn("确定") { println("点击了确定按钮")}
				.create()
	}
}
```

PS:提供了改变颜色,在对应的setBtn方法添加颜色即可,颜色支持**十六进制和文字**,如

```
jfxbutton("测试确认对话框") {
	action {
		DialogBuilder(currentStage)
				.setTitle(title)
				.setMessage("hello")
				.setNegativeBtn("取消","red"){ println("点击了取消按钮")}
				.setPositiveBtn("确定","#fafafa") { println("点击了确定按钮")}
				.create()
	}
}
```
### 输入对话框

弹出对话框,让用户输入内容

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718200607812-1708539890.png)

```
jfxbutton("测试输入对话框") {
	action {
		//text即为用户输入的内容
		jfxdialog(currentStage, "带输入框的对话框", "输入整数内容", { text -> println(text) })
	}
}
```

### 输出对话框
此对话框用来提示对应的网址或者是文件的位置,用户点击之后可以跳转浏览器或者是打开资源管理器并定位到文件

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718200353381-2010535738.png)

```
jfxbutton("测试输出对话框") {
	action {
		//文件
		jfxdialog(currentStage, "带链接的输入框", "输出目录为","D:\\text.txt"
				,false)
		//网址
		jfxdialog(currentStage, "带链接的输入框", "输出目录为","stars-one.site"
				,true)
	}
}
```
### 下载对话框

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718202317796-1270345515.png)

```
jfxbutton("下载"){
	action{
		DownloadDialogView(currentStage,"下载地址","D:\\test.txt")
			.show()
	}
}
```

PS:第三个参数路径可不填,则自动截取下载地址的末尾的作为文件名,并下载到与jar的同目录文件中

### 加载对话框
![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718200839449-112125409.png)

```
jfxbutton("测试加载对话框") {
	action {
		loadingDialog(currentStage,"标题","内容"){alert ->
			runAsync {
				//这里做网络请求或者其他耗时的操作
				for (i in 0..3) {
					Thread.sleep(200)
					println(i)
				}
			} ui{
				//调用close或者hideWithAnimation关闭对话框
				alert.hideWithAnimation()
			}

		}

	}
}
```

### 关闭程序对话框

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718204207727-1864873197.png)

```
jfxbutton("关闭程序对话框") {
	action {
		stopDialog(currentStage,"标题","点击确定结束当前程序")
	}
}
```
### 右下角弹窗

在右下角弹窗,且过3s后自动消失(3s是默认数值),目前还没有实现类似队列那种弹窗,新的弹窗会将之前的覆盖掉

![](https://img2020.cnblogs.com/blog/1210268/202112/1210268-20211222222421744-2108565821.png)

```kotlin
button {
	action {
		showDialogPopup(currentStage, "提示", "复制成功",3.0)
	}
}
```
### 单选框对话框

效果如下:

![](https://img2022.cnblogs.com/blog/1210268/202211/1210268-20221115234038321-933322303.png)

```kotlin
button("单选按钮对话框") {
    action {
      showDialogRadio(currentStage,"选择选项", listOf("txt","md")){label, index ->
          println("选中下标$index 内容:$label")
      }
    }
}
```

### 检测更新对话框
这里单独抽出来讲解使用,详见第2部分
### 自定义对话框
若是你不满足已有的对话框,你可以按照你的喜欢,自定义对话框的内容

```
//这里你可以自定义布局
val content = HBox(Text("自定义内容"))
val alert = DialogBuilder(currentStage)
                .setCustomContext(content)
                .setNegativeBtn("确定")
                .create()
```
## 2.检测更新功能

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718205016359-489175058.png)

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718205052584-1083320179.png)

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718205307205-1422140750.png)

![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200718205709613-1098344008.png)

基于上面的对话框,实现了自动检测更新的框架

我这里是使用了博客园和蓝奏云作为更新的平台,博客园用来发布新版本的信息,蓝奏云则用来上传更新文件

使用之前,你需要在博客园发布一篇随笔,并以下面的表格形式,这里给出对应的md格式

```
|版本名|版本号	|更新时间	|更新内容	|更新地址|
|--	|--	|--	|--	|--	|
|v1.1	|2	|2020-7-10	|1.更新xx\n2.更新xx	|	|
|v1.0	|1	|2020-7-10	|1.更新xx\n2.更新xx	|	|
```

更新内容一栏中,通过`\n`来实现换行 

更新地址,你可以使用蓝奏云的地址或者是其他的地址,**如果使用蓝奏云的话,一定要是整个文件夹的地址**

更新版本时,你需要将博客园上随笔的内容进行更新,并在新版本的信息放在表格第一行,并保证版本号比之前的要高,否则框架无法检测到新版本的升级(使用蓝奏云的话,则在之前的文件夹上传文件即可,框架会自动下载第一个的文件,即为最新版本的文件)

```
jfxbutton("检测更新") {
	action {
		//最重要的是版本号,记得是int类型的,版本名无关紧要,只是让之后的升级提示对话框显示可以好看点
		TornadoFxUtil.checkVersion(currentStage, "https://www.cnblogs.com/stars-one/p/13284015.html", "当前版本号", "当前版本名")
	}
}
```
## 3.常用方法
位于TornadoFxUtils.kt文件中

- 下载文件`downloadFile`
- 下载图片`downloadImage`
- 复制文字到粘贴板`copyTextToClipboard`
- 自动补全网址`completeUrl`
- 检测版本更新`checkVersion`
- 打开网址`openUrl`
- 打开文件所在文件夹 `openFileDir`
- 打开文件(使用默认关联应用程序) `openFile`
- 获取当前显示器的宽度 `getScreenWidth`
- 获取当前显示器的高度 `getScreenHeight`
- 处理文件名(替换掉文件名中非法的字符) `handleFileName`
- 打开指定jar文件 `openApp`
- 重启当前jar文件 `restartApp`
- 获取当前jar文件的根目录 `getCurrentJarPath`
- 判断是否是window平台 `isWin`
- 执行cmd命令(支持linux和macOs) `execCmd`

## 4.常用控件
以下的控件其实本质上都是一个方法,使用了TornadoFx内置的DSL语法进行书写,使用的时候和TornadoFx编写布局的代码是一样的

|控件	|控件名称	|例子|
|--	|--	|--	|
|xUrlLink	|可选择的超链接文本	|urlLink("博客地址","stars-one.site")|
|xImageView	|生成指定宽高的图片,正方形的图片可省略高度参数	|imageview("xx.jpg",50,50)	|
|xIconItem	|生成带图标的右键菜单(ContextMenu),每个控件都有setContextMenu方法	|![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200719114145732-112475743.png)|
|xSelectText|可选择的文本框|selectText("内容")|
|xFfxButton|指定宽高的扁平按钮,正方形可省略高度参数|jfxbutton("xx.jpg",50,50)|
|xCircleJfxbutton|圆形图标扁平按钮(鼠标滑过会有阴影),传递一个node参数|circlejfxbutton(imageview("xx.jpg",50))|
|xChooseFile|文件输入+选择| xChooseFile(viewModel.mdFilePath, "md,markdown", "markdown文件") |
|xChooseFileDirectory|文件夹输入+选择| xChooseFileDirectory("下载目录", dirPath)|
|xIconButton|圆形按钮| xIconButton(xImageView("/x5.jpg",30))|
|showToast|显示Toast|![](https://img2020.cnblogs.com/blog/1210268/202007/1210268-20200719122328168-1367451505.png)|
|remixIconLabel|显示字体图标,[点击查看使用说明](#9字体图标组件)|remixIconLabel("home-4-fill", c("red"),29)|
|remixIconText|显示字体图标,[点击查看使用说明](#9字体图标组件)|remixIconText("home-4-fill", c("red"),29)|
|remixIconButton|圆形图标按钮| remixIconButton("home-4-fill")|
|remixIconButtonWithBorder|圆形图标按钮(带边框)|remixIconButtonWithBorder("home-4-fill")|


## 5.下载框架
对应的HttpDownloader类

构造方法需要传入一个下载地址和保存文件路径,之后调用`startDownload`方法即可下载
```
HttpDownloader(downloadUrl, file).startDownload(object : HttpDownloader.OnDownloading {
	override fun onProgress(progress: Double, percent: Int, speed: String) {
		//回调的几个参数,progress是进度,percent是百分比,speed是下载速度
		//你可以使用在下载之前将相关得到控件绑定一个观察者,之后将这里的相关参数设置即可
		
	}

	override fun onFinish() {
		
	}

	override fun onError(e: IOException) {
		
	}
})
```

## 6.XRecyclerView的用法

仿造Android中的RecyclerView用法写的一个控件,主要实现将一个List数据以列表的方式显示出来

旧版本(及命名为FxRecyclerView)使用方法详见[Tornadofx控件库(2)——FxRecyclerView | Stars-One的杂货小窝](https://stars-one.site/2020/07/31/fxrecyclerview)

**XRecyclerView为使用MVVM模式重构的版本,使用起来更为的方便**

### 1.创建bean类
这个没啥好说的，就是一个存数据的bean类，如一个`Person`类，根据自己的情况与需求创建
```
data class Person(var name: String,var age:String)
```
### 2.创建ItemView
这个就是列表中的每一项View，**需要继承tornadofx中的View**，我这里就是显示Person的name和age属性，比较简单演示一下

**PS:我自己封装了一个抽象类ItemViewBase,可以直接实现此类即可**

```
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
```

### 3.创建RvDataObservableList和RvAdapter

RvDataObservableList里面包含了beanList和itemViewList,**后续只需要调用其相关的方法即可实现对列表数据进行的增删改查,且会同步itemVIew进行修改(即界面会同步根据数据改变)**

**PS:建议把此类RvDataObservableList声明为全局变量**

```
//Person是bean的实体类,MyItemView则是itemview的类
val dataList = RvDataObservableList<Person,MyItemView>()

val adapter = object : RvAdapter<Person, MyItemView>(dataList) {

    override fun onBindData(itemView: MyItemView, bean: Person, position: Int) {
        //这里bindData是ItemViewBase中定义的方法
        itemView.bindData(dataList,position)
    }

    //itemView的单击事件
    override fun onClick(itemView: MyItemView, position: Int) {
        println("单击$position")
    }

    //itemView的右击事件
    override fun onRightClick(itemView: MyItemView, position: Int) {
        println("右击$position")
    }

    override fun onCreateView(): MyItemView {
        return MyItemView()
    }

}
```

### 4.界面添加FxRecyclerView
```
class RvTestView : View("My View") {
    val dataList = RvDataObservableList<Person,MyItemView>()

    override val root = vbox {
        setPrefSize(1000.0, 400.0)       

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

            override fun onCreateView(): MyItemView {
                return MyItemView()
            }
        }

        this+=XRecyclerView<Person, MyItemView>().setRvAdapter(adapter)
    }
}
```

### 5.方法补充

**XRecyclerView:**

|方法名							|参数说明	|方法说明				|
|--								|--			|--						|
|setWidth(double)				|double类型的数值			|设置宽度				|
|setHegiht(double)				|double类型的数值			|设置高度				|
|setIsShowHorizontalBar(String)	|显示方式，never(不显示） always（一直显示） asneed（自动根据需要显示）			|设置是否显示水平滚动条	|

**RvDataObservableList:**

**注意:下面涉及到对beanList的修改,都会同步对界面进行修改**

|方法名							|参数说明	|方法说明				|
|--								|--			|--						|
|getItemView(bean: beanT)			|bean对象		|根据bean对象获取itemView				|
|getItemView(index: Int)				|double类型的数值			|设置高度				|
|add(beanT)						|			|添加一个数据			|
|add(index,beanT)						|			|指定坐标插入新数据		|
|addAll(list: List<beanT>)						|			|添加多个新数据		|
|addAll(vararg bean: beanT)					|			|添加多个新数据	|
|update(index: Int, bean: beanT)				|			|更新指定坐标数据		|
|update(bean: beanT,newBean:beanT)				|			|把某个对象更新为新对象newBean		|
|remove(bean: beanT)				|			|指定坐标插入新数据		|
|remove(index: Int)				|			|移除指定下标		|
|remove(vararg indexList:Int)			|			|移除指定的多个下标		|
|remove(indexList: List<Int>)			|			|移除指定的多个下标		|
|fun remove(from: Int, to: Int)			|			|移除连续的多个指定下标( 区间为[from,to) )		|
|removeLast()				|			|移除最后一个数据		|
|clear				|			|清除所有数据		|


## 7.配置存储类

在多数情况下,软件会有设置项的存储,需要本地存储下用户的设置

这里便是自己琢磨了下,写了个配置存储的功能(`GlobalDataConfig`类),但此类并不是能直接使用,所以下面介绍下使用方法

下面以一个开关设置来讲解使用
### 1.常量池创建
```
class Constants {
    companion object {
        const val FLAG_OPEN_OPTION = "flag_open_option"
    }
}
```
### 2.工具类的创建
```
class GlobalData {
    companion object {

        //某项功能开关
        val openFlag = GlobalDataConfig(Constants.FLAG_OPEN_OPTION, false) 

    }
}
```

这里说明下,`GlobalDataConfig`的构造函数为key和defaultValue,之后会以键值对的形式存储在本地的一个properties文件中

`defaultValue`即默认值,之后有个方法,主要是初始化会调用(从本地文件读取数值,若是没有数值,则取默认数值)

> 注意:目前框架**只支持4种类型**,`string`,`double`,`int`,`boolean`

### 3.关于GlobalDataConfig方法补充

经过上面的步骤,现在我们就可以使用`GlobalData.openFlag`来进行使用了,这里补充说明下`GlobalDataConfig`中含有的方法

```
//当前数值
GlobalData.openFlag.currentValue

//更改数值(更改会自动同步更新本地配置文件里的保存的数值)
GlobalData.openFlag.setValue()

//恢复默认值
GlobalData.openFlag.resetValue()
```

对于TornadoFx应用,一般推荐我们将TextField与ViewModel中的属性进行绑定,这里也是新增了工具类去快速实现使用

有四个类型的方法,各自获取不同类型的Property属性

- GlobalDataConfigUtil.getSimpleBooleanProperty
- GlobalDataConfigUtil.getSimpleStringProperty
- GlobalDataConfigUtil.getSimpleDoubleProperty
- GlobalDataConfigUtil.getSimpleIntegerProperty

具体的使用示例代码如下(包含四种类型的使用)

<details>
<summary>点击展开代码</summary>

```kotlin
package com.starsone.controls.test

import com.starsone.controls.utils.GlobalDataConfig
import com.starsone.controls.utils.GlobalDataConfigUtil
import tornadofx.*


class GlobalDataTestView : View("My View") {
    val viewModel by inject<GlobalDataTestViewModel>()

    override val root = vbox {

        prefWidth = 800.0
        prefHeight = 500.0

        checkbox("开启某功能", viewModel.booleanFlag)

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
```
</details>

## 8.系统剪贴板监听

**此功能仅在window平台上可用!!!**

使用:
```
val monitor = SystemClipboardMonitor()
monitor.addClipboardListener(object :GlobalClipBoardListener{
    override fun onCopy(text: String?, clipboard: Clipboard?, contents: Transferable?) {
        //这里可以加上相关的判断来测试内容是否是符合自己的定义的条件才触发对应的操作
        println("已监听到方法...")
        println(text)
    }
})
```

考虑到会有设置的选项,就定义了两个开关方法,可以在需要的时候进行开关的设置(默认是剪切板的监听就是开启的)

```
//开启监听
monitor.stopListen()

//停止监听
monitor.startListen()
```

不过测试的时候,发现还是会有些小问题...

## 9.字体图标组件

此组件是2.0.2以上版本新增的组件,库增加500KB左右的大小吧,应该还能接受,有2000+个图标

效果:

![](https://img2022.cnblogs.com/blog/1210268/202210/1210268-20221015144542106-504390180.png)

使用步骤:

```kotlin
hbox{
    //1.先进行初始化
    RemixIconData.init(resources)
    //2.使用,传入图标名称即可
    remixIconLabel("home-4-fill", c("red"),29)
    remixIconText("home-4-fill", c("green"),20)
}
```

`resources`是TornadoFx提供的一个内置对象

上述我初始化是直接在页面初始化,实际上,推荐在App里进行初始化更好,如下:

![](https://img2022.cnblogs.com/blog/1210268/202210/1210268-20221015145704475-1953839968.png)

- remixIconLabel() 本质上是Label组件
- remixIconText 本质上是Text组件

可以通过[Remix Icon - Open source icon library](https://remixicon.com/)来找到对应的图标名称,目前使用的Remix Icon版本为`2.5.0`

点开你需要用的图标即可看到名称,如下图所示

![](https://img2022.cnblogs.com/blog/1210268/202210/1210268-20221015145345751-980193051.png)
