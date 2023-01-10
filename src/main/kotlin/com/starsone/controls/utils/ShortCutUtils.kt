package com.starsone.controls.utils

import java.io.File
import javax.swing.filechooser.FileSystemView

/**
 * window系统的快捷方式工具类
 *
 * @constructor Create empty Short cut utils
 */
object ShortCutUtils {

    /**
     * 创建快捷方式
     *
     * @param lnkFile 快捷文件
     * @param targetFile 源文件
     */
    fun createShortCut(lnkFile: File, targetFile: File) {
        if (!System.getProperties().getProperty("os.name").toUpperCase().contains("WINDOWS")) {
            println("当前系统不是window系统,无法创建快捷方式!!")
            return
        }

        val targetPath = targetFile.path
        if (!lnkFile.parentFile.exists()) {
            lnkFile.mkdirs()
        }
        //原快捷方式存在,则删除
        if (lnkFile.exists()) {
            lnkFile.delete()
        }

        lnkFile.appendBytes(headFile)
        lnkFile.appendBytes(fileAttributes)
        lnkFile.appendBytes(fixedValueOne)
        lnkFile.appendBytes(targetPath.toCharArray()[0].toString().toByteArray())
        lnkFile.appendBytes(fixedValueTwo)
        lnkFile.appendBytes(targetPath.substring(3).toByteArray(charset("gbk")))
    }

    /**
     * 设置软件开机启动
     *
     * @param targetFile 源文件
     */
    fun setAppStartup(targetFile: File) {
        val lnkFile = File(targetFile.parentFile, "temp.lnk")
        createShortCut(lnkFile, targetFile)
        val startUpFile = File(startup, "${targetFile.nameWithoutExtension}.lnk")
        //复制到启动文件夹,若快捷方式已存在则覆盖原来的
        lnkFile.copyTo(startUpFile, true)
        //删除缓存的快捷方式
        lnkFile.delete()
    }

    /**
     * 设置软件开机启动
     *
     * @param targetFile 源文件路径
     */
    fun setAppStartup(targetFilePath: String) {
        setAppStartup(File(targetFilePath))
    }

    /**
     * 创建快捷方式
     *
     * @param lnkFilePath 快捷方式文件生成路径
     * @param targetFilePath 源文件路径
     */
    fun createShortCut(lnkFilePath: String, targetFilePath: String) {
        createShortCut(File(lnkFilePath), File(targetFilePath))
    }

    /**
     * 开机启动目录
     */
    val startup = "${System.getProperty("user.home")}\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\"

    /**
     * 桌面目录
     */
    val desktop = FileSystemView.getFileSystemView().homeDirectory.absolutePath + "\\"

    /**
     * 文件头，固定字段
     */
    private val headFile = byteArrayOf(
            0x4c, 0x00, 0x00, 0x00, 0x01, 0x14, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00,
            0xc0.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46
    )

    /**
     * 文件头属性
     */
    private val fileAttributes = byteArrayOf(
            0x93.toByte(), 0x00, 0x08, 0x00,  //可选文件属性
            0x20, 0x00, 0x00, 0x00,  //目标文件属性
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //文件创建时间
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //文件修改时间
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,  //文件最后一次访问时间
            0x00, 0x00, 0x00, 0x00,  //文件长度
            0x00, 0x00, 0x00, 0x00,  //自定义图标个数
            0x01, 0x00, 0x00, 0x00,  //打开时窗口状态
            0x00, 0x00, 0x00, 0x00,  //热键
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 //未知
    )

    private val fixedValueOne = byteArrayOf(
            0x83.toByte(), 0x00, 0x14, 0x00, 0x1F, 0x50, 0xE0.toByte(), 0x4F, 0xD0.toByte(),
            0x20, 0xEA.toByte(), 0x3A, 0x69, 0x10, 0xA2.toByte(),
            0xD8.toByte(), 0x08, 0x00, 0x2B, 0x30, 0x30, 0x9D.toByte(), 0x19, 0x00, 0x2f
    )

    /**
     * 固定字段2
     */
    private val fixedValueTwo = byteArrayOf(
            0x3A, 0x5C, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x54, 0x00,
            0x32, 0x00, 0x04, 0x00, 0x00, 0x00, 0x67, 0x50, 0x91.toByte(), 0x3C, 0x20, 0x00
    )

}
