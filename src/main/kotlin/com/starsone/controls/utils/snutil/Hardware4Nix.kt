package com.starsone.controls.utils.snutil

import java.io.*

/**
 * @author StarsOne
 * @url [http://stars-one.site](http://stars-one.site)
 * @date Create in  2022/11/28 23:54
 */
internal object Hardware4Nix {
    private var sn: String? = null

    val serialNumber: String?
        get() {
            if (sn == null) {
                readDmidecode()
            }
            if (sn == null) {
                readLshal()
            }
            if (sn == null) {
                throw RuntimeException("Cannot find computer SN")
            }
            return sn
        }

    private fun read(command: String): BufferedReader {
        var os: OutputStream? = null
        var `is`: InputStream? = null
        val runtime = Runtime.getRuntime()
        var process: Process? = null
        process = try {
            runtime.exec(command.split(" ".toRegex()).toTypedArray())
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
        return BufferedReader(InputStreamReader(`is`))
    }

    private fun readDmidecode() {
        var line: String? = null
        val marker = "Serial Number:"
        var br: BufferedReader? = null
        try {
            br = read("dmidecode -t system")
            while (br.readLine().also { line = it } != null) {
                if (line!!.indexOf(marker) != -1) {
                    sn = line!!.split(marker.toRegex()).toTypedArray()[1].trim { it <= ' ' }
                    break
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }

    private fun readLshal() {
        var line: String? = null
        val marker = "system.hardware.serial ="
        var br: BufferedReader? = null
        try {
            br = read("lshal")
            while (br.readLine().also { line = it } != null) {
                if (line!!.indexOf(marker) != -1) {
                    sn = line!!.split(marker.toRegex()).toTypedArray()[1].replace("\\(string\\)|(\\')".toRegex(), "").trim { it <= ' ' }
                    break
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
        }
    }
}
