package com.starsone.controls.utils.snutil

import java.io.*

/**
 * @author StarsOne
 * @url [http://stars-one.site](http://stars-one.site)
 * @date Create in  2022/11/28 23:52
 */
internal object Hardware4Mac {
    val sn: String by lazy {
        var tempSn = ""

        var os: OutputStream? = null
        var `is`: InputStream? = null
        val runtime = Runtime.getRuntime()
        var process: Process? = null
        process = try {
            runtime.exec(arrayOf("/usr/sbin/system_profiler", "SPHardwareDataType"))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        os = process.outputStream
        `is` = process.inputStream
        try {
            os.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        val br = BufferedReader(InputStreamReader(`is`))
        var line: String? = null
        val marker = "Serial Number"
        try {
            while (br.readLine().also { line = it } != null) {
                if (line!!.contains(marker)) {
                    tempSn = line!!.split(":".toRegex()).toTypedArray()[1].trim { it <= ' ' }
                    break
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }

        tempSn
    }
}
