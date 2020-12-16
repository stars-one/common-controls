package com.starsone.controls.download


import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import sun.net.www.protocol.http.HttpURLConnection.userAgent
import tornadofx.*
import java.net.ConnectException
import java.util.*
import kotlin.collections.set


/**
 *  解析单条蓝奏云分享地址的真实下载链接
 * @author StarsOne
 * @date Create in  2020/3/10 0010 17:04
 */
class LanzouParse {

    //解析匹配的正则表达式
    private var signRegex: String

    init {
        val doc = Jsoup.connect("https://www.cnblogs.com/stars-one/articles/13156594.html")
                .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                .get()
        signRegex = doc.body().getElementsByTag("h3")[0].text()
    }

    /**
     * 获得请求体参数
     */
    private fun getParam(url: String, pageCount: Int, password: String): LinkedHashMap<String, String> {
        Thread.sleep(200)
        //获取fid，uid,t,k的数值
        val header = HashMap<String, String>()
        header["Accept-Language"] = "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2"
        header["referer"] = url
        val params = LinkedHashMap<String, String>()
        val body = Jsoup.connect(url)
                .headers(header)
                .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                .get()
                .body()
        val jsCode = body.getElementsByTag("script")

        var lines = jsCode[0].toString().split(",")
        var temp1 = ""
        var temp2 = ""
        var fid = ""
        var uid = ""
        var lx = ""
        for (line in lines) {
            if (line.contains("lx")) {
                lx = line.substring(line.length - 1, line.length)
            }
            if (line.contains("fid")) {
                fid = line.substringAfter(":")
            }
            if (line.contains("uid")) {
                uid = line.substringAfter(":").replace("'", "")
            }
            if (line.contains("'t'")) {
                temp1 = line.substringAfter(":")
            }
            if (line.contains("'k'")) {
                temp2 = line.substringAfter(":")
            }
        }
        lines = jsCode[0].toString().split(";")
        var t = ""
        var k = ""
        for (line in lines) {
            if (line.contains(temp1)) {
                if (t.isBlank()) {
                    t = line.substringAfter("'").replace("'", "")
                } else {
                    break
                }
            }
            if (line.contains(temp2)) {
                if (k.isBlank()) {
                    k = line.substringAfter("'").replace("'", "")
                } else {
                    break
                }
            }
        }
        params["lx"] = lx
        params["fid"] = fid
        params["uid"] = uid
        params["pg"] = pageCount.toString()
        params["rep"] = "0"
        params["t"] = t
        params["k"] = k
        params["up"] = "1"
        if (password.isNotBlank()) {
            params["pwd"] = password
        }

        return params
    }


    /**
     * 获得真实下载地址
     */
    private fun getDownloadLink(url: String): String {
        //先获得iframe的src连接（包含电信下载，联通下载和普通下载的一个页面）
        val iframeUrl = getFrameUrl(url)
        //获得伪直链
        var i = 0
        var link = ""
        try {
            link = getLink(iframeUrl)
        } catch (e: ConnectException) {
            println(e.message)
            //重试3次,若是还是报错,就返回空白,由用户自己去手动下载
            i++
            if (i != 3) {
                getLink(iframeUrl)
            }
            return link
        } catch (e: HttpStatusException) {
            //502异常
            println(e.message)
            i++
            if (i != 3) {
                getLink(iframeUrl)
            }
            return link
        }
        return link
    }

    /**
     * 获取第一个的item的蓝奏云真实下载地址
     */
    fun parseUrl(url: String, password: String): String {

        val header = HashMap<String, String>()
        header["Accept-Language"] = "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2"
        header["referer"] = url
        //请求参数
        val params = getParam(url,1, password)
        //重复请求,实现翻页
        params["pg"] = 1.toString()
        Thread.sleep(800)
        val result = Jsoup.connect("https://lanzous.com/filemoreajax.php")
                .headers(header)
                .data(params)
                .post()
                .body().text()
        val jsonObject = loadJsonObject(result)
        val zt = jsonObject.getInt("zt")
        if (zt == 1) {
            val itemId = jsonObject.getJsonArray("text")[0].asJsonObject().getString("id")
            val lastestUrl = "https://lanzous.com/${itemId}"
            return getDownloadLink(lastestUrl)
        }
        return ""
    }

    private fun getLink(iframeUrl: String): String {

        val doc = Jsoup.connect(iframeUrl)
                .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                .get()
        val scriptCodeText = doc.body().getElementsByTag("script").toString()
        val lines = scriptCodeText.split("\n")
        var sign = ""
        for (line in lines) {
            //正则匹配，获得sign（之后post请求需要此数据）
            //todo 蓝奏云官方请求参数会改变,需要修改
            if (line.contains(Regex(signRegex))) {
                sign = line.substring(line.indexOf("'") + 1, line.lastIndex - 1)
                break
            }

            if (line.contains("_")) {
                val split = line.split("'")
                sign = split.find { it.contains("_") }?: ""
                if (sign.isNotBlank()) {
                    break
                }
            }
            if (line.contains("data") && !line.contains("//") && line.contains("sign")) {
                val splitList = line.split("'").sortedByDescending { it.length }
                sign = splitList[0]
                break
            }
        }

        val postUrl = "https://lanzous.com/ajaxm.php"
        //请求头参数
        val header = HashMap<String, String>()
        header["Accept-Language"] = "zh-CN,zh;q=0.8,zh-TW;q=0.7,zh-HK;q=0.5,en-US;q=0.3,en;q=0.2"
        header["referer"] = "https://lanzous.com"
        //请求参数
        val params = LinkedHashMap<String, String>()
        params["action"] = "downprocess"
        params["sign"] = sign
        params["ves"] = "1"

        //获得伪链接（此处若没有请求头，会导致下一步访问获得伪链出现400错误）
        val result = Jsoup.connect(postUrl)
                .headers(header)
                .data(params)
                .post()
                .body()
                .text()

        //json转为实体类
        val jsonObject = loadJsonObject(result)

        val lanzouData = LanzouData(jsonObject.getString("dom"),
                jsonObject.getInt("inf").toString(),
                jsonObject.getString("url"), jsonObject.getInt("zt"))
        //若请求结果错误,则重新请求
        if (lanzouData.zt != 1) {
            Thread.sleep(200)
            return getLink(iframeUrl)
        }
        //拼接得到伪链
        val link = lanzouData.dom + "/file/" + lanzouData.url

        //从请求头得到Location重定向地址(即真实下载地址）
        val reqHeads = Jsoup.connect(link)
                .headers(header)
                .ignoreContentType(true)
                .userAgent(userAgent)
                .followRedirects(false)
                .execute()
                .headers()
        return reqHeads["Location"] as String

    }


    private fun getFrameUrl(url: String): String {
        val doc = Jsoup.connect(url)
                .userAgent("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)")
                .get()
        val result = doc.getElementsByTag("iframe")[0].attr("src")
        return "https://lanzous.com$result"
    }


    /**
     * 获得真实下载地址，并存入itemData的downloadLink属性中
     * @param itemData 单个文件对象
     */
    public fun getDownloadLink(itemData: ItemData) {
        val url = itemData.url
        itemData.downloadLink = getDownloadLink(url)
        Thread.sleep(100)
    }
}

data class LanzouData(
        val dom: String,
        val inf: String,
        val url: String,
        val zt: Int
)

data class mydata(
        val info: String,
        val text: List<Text>,
        val zt: Int
)

data class Text(
        val duan: String,
        val icon: String,
        val id: String,
        val name_all: String,
        val p_ico: Int,
        val size: String,
        val t: Int,
        val time: String
)

data class ItemData(var fileName: String, var url: String, var downloadLink: String, var fileSize: String, var time: String)