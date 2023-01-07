package com.starsone.controls.download

import com.starsone.controls.utils.TornadoFxUtil
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.text.DecimalFormat
import java.util.*
import kotlin.concurrent.thread

class HttpDownloader(private val url: String, private val file: String) {

    private val timer = Timer()

    private var lastDownload = 0
    private var sizeRead = 0
    private var speedByte = 0

    /**
     * 计算速度
     */
    private fun startCalculateSpeed() {
        timer.schedule(object : TimerTask() {
            override fun run() {
                speedByte = sizeRead - lastDownload
                lastDownload = sizeRead
            }
        }, 0, 1000)

    }

    /**
     * 开始下载
     */
    fun startDownload(onDownloading: OnDownloading) {
        try {
            //保存的默认文件名
            val defaultFileName = url.substringAfterLast("/")
            val url = URL(url)
            val httpConnection = url.openConnection() as HttpURLConnection
            httpConnection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
            val file = if (file.isEmpty()) {
                //蓝奏云真实地址,直接获取真实的文件名
                if (httpConnection.headerFields.containsKey("Content-Disposition")) {
                    var fileName = httpConnection.headerFields["Content-Disposition"].toString().substringAfter("filename=").replace("filename=", "").replace("]", "")
                    fileName = URLDecoder.decode(fileName, "utf-8")
                    File(fileName)
                } else {
                    //使用默认名
                    File(defaultFileName)
                }
            } else {
                File(TornadoFxUtil.getCurrentJarDirPath(), file)
            }
            val inputStream = httpConnection.inputStream
            val completeFileSize = httpConnection.contentLength
            val bufferArray = ByteArray(10 * 1024)
            val fos = file.outputStream()
            var length = 0
            //开启计算下载速度的线程
            thread {
                startCalculateSpeed()
            }

            while (true) {
                length = inputStream.read(bufferArray)
                sizeRead += length
                if (length == -1) {
                    break
                }
                onDownloading.onProgress(sizeRead * 100.0 / completeFileSize, sizeRead * 100 / completeFileSize, speedToString(speedByte))
                println(sizeRead * 100 / completeFileSize)
                fos.write(bufferArray, 0, length)
            }
            fos.flush()
            fos.close()
            onDownloading.onFinish()
            timer.cancel()
        } catch (e: IOException) {
            //网络出现异常
            timer.cancel()
            onDownloading.onError(e)
        }
    }

    /**
     * 将字节(B)转为对应的单位
     */
    private fun speedToString(bytes: Int): String {
        val df = DecimalFormat("#.00")
        return when {
            bytes < 1024 -> df.format(bytes.toDouble()) + "B/s"
            bytes < 1048576 -> df.format(bytes.toDouble() / 1024) + "K/s"
            bytes < 1073741824 -> df.format(bytes.toDouble() / 1048576) + "M/s"
            else -> df.format(bytes.toDouble() / 1073741824) + "G/s"
        }
    }

    interface OnDownloading {

        fun onProgress(progress: Double, percent: Int, speed: String)

        fun onFinish()

        fun onError(e: IOException)
    }
}




