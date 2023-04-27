package com.starsone.controls.utils

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.apache.commons.lang3.StringUtils
import sun.font.FontDesignMetrics
import tornadofx.*
import java.awt.*
import java.awt.geom.RoundRectangle2D
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO

/**
 * 二维码生成工具类
 * Created by stars-one
 */
object QRCodeUtil {
    private var QRCODE_SIZE = 320 // 二维码尺寸，宽度和高度均是320
    private var LOGO_SIZE = 80 // 二维码里logo的尺寸，宽高一致 80*80
    private var BOTTOM_TEXT_SIZE = 20 // 底部文本的文字大小
    private var FORMAT_TYPE = "PNG" // 二维码图片类型

    /**
     * 初始化设置
     *
     * @param qrcodeSize 二维码尺寸，默认为320(即320*320)
     * @param logoSize logo图标尺寸,默认为80(即80*80)
     * @param bottomTextSize 底部文字大小,默认20px
     * @param qrcodeType 二维码图片格式,默认为png
     */
    fun initConfig(qrcodeSize: Int=320, logoSize: Int=80, bottomTextSize: Int=20, qrcodeType: String="PNG") {
        QRCODE_SIZE = qrcodeSize
        LOGO_SIZE = logoSize
        BOTTOM_TEXT_SIZE = bottomTextSize
        QRCODE_SIZE = qrcodeSize
    }

    /**
     * 默认需要logo,无底部文字
     * 返回 BufferedImage 可以使用ImageIO.write(BufferedImage, "png", outputStream);输出
     *
     * @param dataStr
     * @return 返回 BufferedImage 可以使用ImageIO.write(BufferedImage, "png", outputStream);输出
     */
    @Throws(Exception::class)
    fun getQRCodeImage(dataStr: String?): BufferedImage {
        return getQRCodeImage(dataStr, null, null)
    }

    /**
     * 默认需要logo,无底部文字
     *
     * @param dataStr
     * @return 返回字节数组
     */
    @Throws(Exception::class)
    fun getQRCodeByte(dataStr: String?): ByteArray {
        val bufferedImage = getQRCodeImage(dataStr, null, null)
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, FORMAT_TYPE, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * 默认需要logo，包含底部文字 文字为空则不显示文字
     * 返回 BufferedImage 可以使用ImageIO.write(BufferedImage, "png", outputStream);输出
     *
     * @param dataStr
     * @return
     */
    @Throws(Exception::class)
    fun getQRCodeImage(dataStr: String?, bottomText: String?): BufferedImage {
        return getQRCodeImage(dataStr, null, bottomText)
    }

    /**
     * 默认需要logo，包含底部文字 文字为空则不显示文字
     *
     * @param dataStr
     * @return 返回字节数组
     */
    @Throws(Exception::class)
    fun getQRCodeByte(dataStr: String?, bottomText: String?): ByteArray {
        val bufferedImage = getQRCodeImage(dataStr, null, bottomText)
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, FORMAT_TYPE, outputStream)
        return outputStream.toByteArray()
    }

    /**
     * 获取二维码图片
     *
     * @param dataStr    二维码内容
     * @param needLogo   是否需要添加logo
     * @param bottomText 底部文字       为空则不显示
     * @return
     */
    @Throws(Exception::class)
    fun getQRCodeImage(dataStr: String?, url: URL?, bottomText: String?): BufferedImage {
        if (dataStr == null) {
            throw RuntimeException("未包含任何信息")
        }
        val hints = HashMap<EncodeHintType, Any?>()
        hints[EncodeHintType.CHARACTER_SET] = "utf-8" //定义内容字符集的编码
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L //定义纠错等级
        hints[EncodeHintType.MARGIN] = 1
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix = qrCodeWriter.encode(dataStr, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        var tempHeight = height
        if (StringUtils.isNotBlank(bottomText)) {
            tempHeight = tempHeight + 12
        }
        val image = BufferedImage(width, tempHeight, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until width) {
            for (y in 0 until height) {
                image.setRGB(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
            }
        }
        // 判断是否添加logo
        if (url != null) {
            insertLogoImage(image, url)
        }
        // 判断是否添加底部文字
        if (StringUtils.isNotBlank(bottomText)) {
            addFontImage(image, bottomText)
        }
        return image
    }

    /**
     * 插入logo图片
     *
     * @param source 二维码图片
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun insertLogoImage(source: BufferedImage, url: URL) {
        var src: Image = ImageIO.read(url)
        val width = LOGO_SIZE
        val height = LOGO_SIZE
        val image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH)
        val tag = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val g = tag.graphics
        g.drawImage(image, 0, 0, null) // 绘制缩小后的图
        g.dispose()
        src = image

        // 插入LOGO
        val graph = source.createGraphics()
        val x = (QRCODE_SIZE - width) / 2
        val y = (QRCODE_SIZE - height) / 2
        graph.drawImage(src, x, y, width, height, null)
        val shape: Shape = RoundRectangle2D.Float(x.toFloat(), y.toFloat(), width.toFloat(), width.toFloat(), 6f, 6f)
        graph.stroke = BasicStroke(3f)
        graph.draw(shape)
        graph.dispose()
    }

    private fun addFontImage(source: BufferedImage, declareText: String?) {
        //生成image
        val defineWidth = QRCODE_SIZE
        val defineHeight = 20
        val textImage = BufferedImage(defineWidth, defineHeight, BufferedImage.TYPE_INT_RGB)
        val g2 = textImage.graphics as Graphics2D
        //开启文字抗锯齿
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        g2.background = Color.WHITE
        g2.clearRect(0, 0, defineWidth, defineHeight)
        g2.paint = Color.BLACK
        val context = g2.fontRenderContext
        //部署linux需要注意 linux无此字体会显示方块
        val font = Font("宋体", Font.BOLD, BOTTOM_TEXT_SIZE)
        g2.font = font
        val lineMetrics = font.getLineMetrics(declareText, context)
        val fontMetrics: FontMetrics = FontDesignMetrics.getMetrics(font)
        val offset = ((defineWidth - fontMetrics.stringWidth(declareText)) / 2).toFloat()
        val y = (defineHeight + lineMetrics.ascent - lineMetrics.descent - lineMetrics.leading) / 2
        g2.drawString(declareText, offset.toInt(), y.toInt())
        val graph = source.createGraphics()
        //开启文字抗锯齿
        graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        //添加image
        val width = textImage.getWidth(null)
        val height = textImage.getHeight(null)
        val src: Image = textImage
        graph.drawImage(src, 0, QRCODE_SIZE - 8, width, height, Color.WHITE, null)
        graph.dispose()
    }
}
