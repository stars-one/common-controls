package com.starsone.controls.utils

import com.starsone.controls.common.DialogBuilder
import com.starsone.controls.common.DownloadDialogView
import com.starsone.controls.common.showToast
import com.starsone.controls.model.UpdateInfo
import com.sun.xml.internal.messaging.saaj.util.TeeInputStream
import javafx.application.Platform
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.layout.Pane
import javafx.stage.Screen
import javafx.stage.Stage
import org.jsoup.Jsoup
import tornadofx.*
import java.awt.Desktop
import java.io.*
import java.net.URI
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.concurrent.thread


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
        fun checkVersion(stage: Stage?, url: String, appName: String, currentVersionCode: Int, currentVersion: String, pane: Pane? = null) {
            DialogBuilder(stage)
                    .setTitle("检测更新")
                    .setLoadingMessage("新版本检测中") { alert ->
                        var info: com.starsone.controls.model.UpdateInfo? = null
                        if (url.contains("cnblogs")) {
                            //博客园的更新地址
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
                                                DownloadDialogView(stage, info!!.updateUrl).show()
                                            }
                                            .create()
                                } else {
                                    if (pane != null) {
                                        showToast(pane, "当前已经是最新版本")
                                    }
                                }
                            }
                        } else {
                            //自己的更新系统
                            runAsync {
                                try {
                                    val doc = Jsoup.connect("${url}?versionCode=${currentVersionCode}")
                                            .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                                            .ignoreContentType(true)
                                            .get()
                                    val json = doc.body().text()

                                    val resJsonObject = loadJsonObject(json)
                                    val code = resJsonObject.getInt("code")
                                    if (code == 200) {
                                        val jsonObject = resJsonObject.getJsonObject("data")
                                        val forceUpdate = jsonObject.getBoolean("forceUpdate", false)

                                        info = UpdateInfo(jsonObject.getString("appVersionName"), jsonObject.getInt("appVersionCode").toString(), jsonObject.getString("createTime"), jsonObject.getString("updateMessage"), jsonObject.getString("fileDownloadUrl"))
                                        info?.let {
                                            alert.hideWithAnimation()
                                            runLater {
                                                val dialogBuilder = DialogBuilder(stage)
                                                        .setTitle("发现新版本")
                                                        .setMessage(it, currentVersion)
                                                        .setPositiveBtn("升级") {
                                                            DownloadDialogView(stage, it.updateUrl, "${appName}${it.version}.jar").show()
                                                        }
                                                if (forceUpdate) {
                                                    //强制更新,不允许取消,只能退出程序
                                                    dialogBuilder.setNegativeBtn("退出程序"){
                                                        Platform.exit()
                                                    }.create()
                                                } else {
                                                    dialogBuilder.setNegativeBtn("取消").create()
                                                }
                                            }
                                        }
                                    } else {
                                        alert.close()
                                        pane?.let {
                                            runLater {
                                                showToast(pane, "当前已经是最新版本")
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    runLater {
                                        alert.close()
                                        if (pane != null) {
                                            showToast(pane, "更新失败,网络异常")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .setPositiveBtn("取消")
                    .create()
        }

        /**
         * 打开网址(会自动加上http)
         *
         * @param url 网址
         */
        fun openUrl(url: String) {
            if (url.contains("http")) {
                Desktop.getDesktop().browse(URI(url));
            } else {
                Desktop.getDesktop().browse(URI("http://$url"));
            }
        }

        /**
         * 打开文件所在目录
         *
         * @param file
         */
        fun openFileDir(file: File) {
            if (isWin()) {
                Runtime.getRuntime().exec("explorer /select, ${file.path}");//打开资源管理器，选择该文件
            } else {
                Desktop.getDesktop().open(file.parentFile)
            }
        }

        /**
         * 打开文件(使用相关默认应用)
         *
         * @param file
         */
        fun openFile(file: File) {
            Desktop.getDesktop().open(file)
        }

        /**
         * 处理文件名 如将 test*.java 替换成test＊.java(后面和这个星号是中文的星号)
         *
         * **处理说明:**
         * - 星号替换为中文星号
         * - 问号替换为中文问号
         * - 冒号替换为中文呢冒号
         * - `\` `/` `|`替换为`-`
         * - 剩下则直接替换成空白字符串
         * @param fileName 文件名(不能是路径,单纯的文件名)
         * @return
         */
        fun handleFileName(fileName: String): String {
            //需要对文件名进行处理,否则会产生无法下载问题
            // \ / : * ? " < > |
            var result = fileName
            listOf("\\", "/", "*", "?", "<", ">", "|", ":").forEach {
                result = when (it) {
                    "*" -> result.replace(it, "＊")
                    "?" -> result.replace(it, "？")
                    ":" -> result.replace(it, "：")
                    "/", "\\", "|" -> result.replace(it, "&")
                    else -> result.replace(it, "")
                }
            }
            return result
        }

        /**
         * 获取当前显示器的宽度
         *
         * @return
         */
        fun getScreenWidth(): Double {
            return Screen.getPrimary().bounds.width
        }

        /**
         * 获取当前显示器的高度
         *
         * @return
         */
        fun getScreenHeight(): Double {
            return Screen.getPrimary().bounds.height
        }

        /**
         * 获取当前jar包的文件路径(如果不是jar包打开,则是获取当前项目的根路径)
         *
         * @param url 在View中使用resources.url("")获取的参数
         * @return
         */
        @Deprecated("过时的方法,可使用无参的方法",replaceWith = ReplaceWith("getCurrentJarPath()"))
        fun getCurrentJarPath(url: URL): File {
            val path = url.path
            val filePath = path.substringBeforeLast("!/")
            return File(URI.create(filePath))
        }

        /**
         * 获取当前jar包的文件夹路径(如果不是jar包打开,会出现异常!!)
         *
         * @param url 在View中使用resources.url("")获取的参数
         * @return
         */
        @Deprecated("过时的方法,可使用无参的方法",replaceWith = ReplaceWith("getCurrentJarDirPath()"))
        fun getCurrentJarDirPath(url: URL): File {
            val jarFlag = "!/"
            val path = url.path
            return if (path.contains(jarFlag)) {
                val filePath = path.substringBeforeLast("!/")
                File(URI.create(filePath)).parentFile
            } else {
                File("").absoluteFile
            }
        }

        /**
         * 重启当前应用
         *
         * @param url 在View中使用resources.url("")获取的参数
         */
        @Deprecated("过时的",replaceWith = ReplaceWith("restartApp()"))
        fun restartApp(url: URL) {
            val jarFile = getCurrentJarPath(url)
            //开启新应用
            Runtime.getRuntime().exec("cmd.exe /c javaw -jar ${jarFile.path}")
            //关闭当前应用
            Platform.exit()
        }

        /**
         * 获取当前jar包的文件路径(如果不是jar包打开,则是获取当前项目的根路径)
         *
         * @param url 在View中使用resources.url("")获取的参数
         * @return
         */
        fun getCurrentJarPath(): File {
            val url = ResourceLookup(this).url("/ttf/remixicon.ttf")
            val path = url.path
            val filePath = path.substringBeforeLast("!/")
            return File(URI.create(filePath))
        }

        /**
         * 获取当前jar包的文件夹路径
         *
         * @return
         */
        fun getCurrentJarDirPath(): File {
            return File("").absoluteFile
        }

        /**
         * 重启当前应用
         *
         */
        fun restartApp() {
            val jarFile = getCurrentJarPath()
            //开启新应用
            Runtime.getRuntime().exec("cmd.exe /c javaw -jar ${jarFile.path}")
            //关闭当前应用
            Platform.exit()
        }

        /**
         * 打开指定jar文件
         *
         * @param jarFile
         */
        fun openApp(jarFile: File) {
            if (jarFile.extension.toLowerCase() == "jar") {
                if (isWin()) {
                    Runtime.getRuntime().exec("cmd.exe /c javaw -jar ${jarFile.path}")
                } else {//linux或mac(未实践过)
                    Runtime.getRuntime().exec("javaw -jar ${jarFile.path}")
                }
            }
        }

        /**
         * 当前系统是否为window系统
         */
        fun isWin(): Boolean {
            val prop = System.getProperties()

            val os = prop.getProperty("os.name")
            return os.contains("win", true)
        }

        /**
         * 执行命令行,并等待命令执行完毕,同时将过程中的控制台输出日志写入日志文件中
         * - [cmd] 命令,window记得要使用cmd /c开头,如cmd /c ipconfig
         * - [dir] 命令行所在路径
         * - [logFile] 日志文件
         */
         fun execCmd(cmd: String, dir: File, logFile: File) {
            val process = Runtime.getRuntime().exec(cmd, null, dir)
            val inputStream = process.inputStream

            //开启两个线程用来读取流，否则会造成死锁问题
            thread {
                var fileOutputStream: FileOutputStream? = null
                var teeInputStream: TeeInputStream? = null
                var bufferedReader: BufferedReader? = null
                try {
                    fileOutputStream = FileOutputStream(logFile, true)
                    //使用分流器，日志文件和
                    teeInputStream = TeeInputStream(inputStream, fileOutputStream)
                    //区分不同平台
                    bufferedReader = if (isWin()) {
                        BufferedReader(InputStreamReader(teeInputStream, "gbk"))
                    } else {
                        BufferedReader(InputStreamReader(teeInputStream, "utf-8"))
                    }
                    var line: String?
                    while (bufferedReader.readLine().also { line = it } != null) {
                        println(line)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        bufferedReader!!.close()
                        teeInputStream!!.close()
                        fileOutputStream!!.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            thread {
                val err = InputStreamReader(process.errorStream)
                val bferr = BufferedReader(err)
                var errline = ""
                try {
                    while (bferr.readLine().also { errline = it } != null) {
                        println("流错误：$errline")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        bferr.close()
                        err.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            process.waitFor()
            process.destroy()
        }
    }
}

