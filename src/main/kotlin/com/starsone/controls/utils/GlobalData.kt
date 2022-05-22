package com.starsone.controls.common

import tornadofx.*
import java.nio.charset.Charset
import java.nio.file.Path


class Constants {
    companion object {
        const val SP_USER_STATUS = "sp_user_status"
    }
}

/**
 * 主要是仿写不可变的常量
 */
class GlobalData {
    companion object {

        //
        val userStatus = GlobalDataConfig(Constants.SP_USER_STATUS, false) {
            it.setValue(it.storageSave.config.boolean(it.key,it.defaultValue))
        }

    }
}

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


