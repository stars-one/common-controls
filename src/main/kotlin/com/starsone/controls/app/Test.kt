package com.starsone.controls.app

import com.starsone.controls.download.HttpDownloader
import com.starsone.controls.download.LanzouParse
import java.io.IOException


/**
 *
 * @author StarsOne
 * @date Create in  2020/7/16 0016 18:15
 * @description
 *
 */
fun main(args: Array<String>) {
//    val url = URL("https://img2018.cnblogs.com/blog/1210268/201912/1210268-20191222171424590-132491536.gif")
//    val url = URL("http://gdown.baidu.com/data/wisegame/01946d7d87cedf3e/cacd01946d7d87cedf3e3f030165d2c5.apk")
//    val url = "http://gdown.baidu.com/data/wisegame/01946d7d87cedf3e/cacd01946d7d87cedf3e3f030165d2c5.apk"
    val url = "https://stars-one.lanzous.com/b0cpwdmrc"
    val parseUrl = LanzouParse().parseUrl(url, "")
    println(parseUrl)

//    println(speedToString(120000))
    HttpDownloader("https://img2018.cnblogs.com/blog/1210268/201912/1210268-20191222171424590-132491536.gif", "Q:\\doc\\test.gif")
            .startDownload(object : HttpDownloader.OnDownloading {
                override fun onProgress(progress: Double, percent: Int, speed: String) {
                    println("progress:$progress")
                    println("percent:$percent")
                    println("speed:$speed")
                    println("---")
                }


                override fun onFinish() {
                    println("完成")
                }

                override fun onError(e: IOException) {
                    println(e.message)
                }
            })

}


