package com.starsone.controls.utils.snutil

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

/**
 * @author StarsOne
 * @url [http://stars-one.site](http://stars-one.site)
 * @date Create in  2022/11/28 23:47
 */
object Hardware4Win {
     val sn: String by lazy{
         var tempSn = ""

         var os: OutputStream? = null
         var `is`: InputStream? = null
         val runtime = Runtime.getRuntime()
         var process: Process? = null
         process = try {
             runtime.exec(arrayOf("wmic", "bios", "get", "serialnumber"))
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
         val sc = Scanner(`is`)
         try {
             while (sc.hasNext()) {
                 val next = sc.next()
                 if ("SerialNumber" == next) {
                     tempSn = sc.next().trim { it <= ' ' }
                     break
                 }
             }
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
