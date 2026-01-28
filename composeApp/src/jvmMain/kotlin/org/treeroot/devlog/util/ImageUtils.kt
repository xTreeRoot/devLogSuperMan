package org.treeroot.devlog.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import java.io.File
import javax.imageio.ImageIO

/**
 * 图像处理工具类
 */
object ImageUtils {

    /**
     * 从文件路径加载ImageBitmap
     */
    fun loadImageBitmap(filePath: String): ImageBitmap? {
        return try {
            val bufferedImage = ImageIO.read(File(filePath))
            bufferedImage.toComposeImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 验证图片文件是否存在且有效
     */
    fun isValidImageFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (!file.exists()) return false

            // 尝试读取图片以验证其有效性
            val bufferedImage = ImageIO.read(file)
            bufferedImage != null
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}