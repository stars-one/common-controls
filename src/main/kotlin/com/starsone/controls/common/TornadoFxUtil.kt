package com.starsone.demo.util

import com.starsone.controls.common.DialogBuilder
import com.starsone.controls.model.UpdateInfo
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.stage.Stage
import org.jsoup.Jsoup
import tornadofx.*
import java.io.File
import java.net.URL
import java.util.zip.GZIPInputStream


/**
 * tornadofx的工具类
 * @author StarsOne
 * @date Create in  2020/2/24 0024 17:04
 */
class TornadoFxUtil {
    companion object {

        /**
         * 将[text]文本复制到剪切板
         */
        fun copyTextToClipboard(text: String) {
            val clipboard = Clipboard.getSystemClipboard()
            val clipboardContent = ClipboardContent()
            clipboardContent.putString(text)
            clipboard.setContent(clipboardContent)
        }

        /**
         * 从[url]地址中下载文件，保存在当前目录下，文件名（包括扩展名）为[fileName]
         */
        fun downloadFile(url: String, fileName: String): File {
            return downloadFile(url, File(fileName))
        }

        /**
         * 从[url]地址中下载文件，保存在[dir]目录下，文件名（包括扩展名）为[fileName]
         */
        fun downloadFile(url: String, dir: String, fileName: String): File {
            return downloadFile(url, File(dir + fileName))
        }

        /**
         * 读取[url]输入流，并将流输出到文件[file]中
         */
        fun downloadFile(url: String, file: File): File {
            val openConnection = URL(url).openConnection()
            //防止某些网站跳转到验证界面
            openConnection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
            val bytes = openConnection.getInputStream().readBytes()
            file.writeBytes(bytes)
            return file
        }

        /**
         * 读取[url]输入流，将流输出到文件[file]中
         */
        fun downloadImage(url: String, file: File): File {
            val openConnection = URL(url).openConnection()
            //防止某些网站跳转到验证界面
            openConnection.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36")
            //如果图片是采用gzip压缩
            val bytes = if (openConnection.contentEncoding == "gzip") {
                GZIPInputStream(openConnection.getInputStream()).readBytes()
            } else {
                openConnection.getInputStream().readBytes()
            }
            file.writeBytes(bytes)
            return file
        }

        /**
         * 从图片地址[url]下载图片，保存在当前目录，图片名(包括扩展名）为[imageName]
         * 如果未指定[imageName]，则截取[url]末尾作为[imageName]
         */
        fun downloadImage(url: String, imageName: String = ""): File {
            return downloadImage(url, File(imageName))
        }

        /**
         * 从图片地址[url]下载图片，保存在[dir]目录，图片名(包括扩展名）为[imageName]
         * 如果未指定[imageName]，则截取[url]末尾作为[imageName]
         */
        fun downloadImage(url: String, dir: String, imageName: String = ""): File {
            val file = if (imageName.isBlank()) {
                File(dir, url.substringAfterLast("/"))
            } else {
                File(dir, imageName)
            }
            return downloadImage(url, file)
        }

        /**
         * 处理[url],返回正确的网址,防止系统无法识别为网址而无法调用浏览器的报错
         */
        fun completeUrl(url: String): String {
            if (url.startsWith("https://") || url.startsWith("http://") || url.startsWith("www")) {
                return url
            }
            return "https://$url"
        }
        /**
         * 检测版本更新
         */
        fun checkVersion(stage: Stage?, url: String, currentVersionCode: Int, currentVersion: String) {
            DialogBuilder(stage)
                    .setTitle("检测更新")
                    .setLoadingMessage("新版本检测中") { alert ->
                        var info: com.starsone.controls.model.UpdateInfo? = null
                        runAsync {
                            val doc = Jsoup.connect(url)
                                    .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                                    .get()
                            val tds = doc.getElementsByTag("tr")[1].getElementsByTag("td")
                            info = UpdateInfo(tds[0].text(), tds[1].text(), tds[2].text(), tds[3].text(), tds[4].text())
                        } ui {
                            alert.close()
                            //对比版本号
                            if (info!!.versionCode.toInt() > currentVersionCode) {
                                DialogBuilder(stage)
                                        .setTitle("发现新版本")
                                        .setMessage(info!!, currentVersion)
                                        .setNegativeBtn("取消")
                                        .setPositiveBtn("升级") {
//                                            DownloadDialogView(stage, info!!.updateUrl)
                                            DownloadDialogView(stage, "https://stars-one.lanzous.com/b0cpr90ti")
                                                    .show()
                                        }
                                        .create()
                            }
                        }
                    }
                    .setPositiveBtn("取消")
                    .create()
        }

    }
}

