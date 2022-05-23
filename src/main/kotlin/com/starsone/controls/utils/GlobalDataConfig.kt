package com.starsone.controls.utils

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import java.nio.charset.Charset
import java.nio.file.Path



class GlobalDataConfig<T>(
        val key: String,
        val defaultValue: T,
        var currentValue: T = defaultValue,
        val initLbd: (GlobalDataConfig<T>) -> Unit
) {

    val storageSave = TornadoFxStorageSave<T>()

    private val callBackList = arrayListOf<(() -> Unit)>()

    init {
        run {
            initLbd.invoke(this)
        }
    }

    /**
     * 添加回调
     *
     * @param callBack
     * @receiver
     */
    fun addCallBack(callBack: (() -> Unit)) {
        callBackList.add(callBack)
    }

    /**
     * 重置当前值为默认值
     */
    fun resetValue() {
        setValue(defaultValue)
    }

    /**
     * 更改数值
     */
    fun setValue(value: T) {
        //更新内存的
        currentValue = value

        //更新本地存储的数据
        updateLocalStorage(value)

        //数值发生变更,执行回调方法
        callBackList.forEach {
            it.invoke()
        }
    }

    /**
     * 更新本地存储
     */
    private fun updateLocalStorage(value: T) {
        storageSave.updateLocalStorage(key, value)
    }

}

class TornadoFxStorageSave<T> : InterfaceStorageSave<T>, Configurable {
    private val app: App get() = FX.application as App

    /**
     * Path to component specific configuration settings. Defaults to javaClass.properties inside
     * the configured configBasePath of the application (By default conf in the current directory).
     */
    override val configPath: Path get() = app.configBasePath.resolve("globalConfig.properties")
    override val config: ConfigProperties by lazy { loadConfig() }

    override val configCharset: Charset = Charset.defaultCharset()

    override fun updateLocalStorage(key: String, value: T) {
        config[key] = value.toString()
        config.save()
    }
}

interface InterfaceStorageSave<T> {
    fun updateLocalStorage(key: String, value: T)
}

class GlobalDataConfigUtil {
    companion object {
        fun getSimpleBooleanProperty(config: GlobalDataConfig<Boolean>): SimpleBooleanProperty {
            return SimpleBooleanProperty(config.currentValue).apply {
                onChange {
                    config.setValue(it)
                }
            }
        }

        fun getSimpleStringProperty(config: GlobalDataConfig<String>): SimpleStringProperty {
            return SimpleStringProperty(config.currentValue).apply {
                onChange {
                    it?.let {
                        config.setValue(it)
                    }
                }
            }
        }

        fun getSimpleDoubleProperty(config: GlobalDataConfig<Double>): SimpleDoubleProperty {
            return SimpleDoubleProperty(config.currentValue).apply {
                onChange {
                    config.setValue(it)
                }
            }
        }

        fun getSimpleIntegerProperty(config: GlobalDataConfig<Int>): SimpleIntegerProperty {
            return SimpleIntegerProperty(config.currentValue).apply {
                onChange {
                    config.setValue(it)
                }
            }
        }
    }
}

