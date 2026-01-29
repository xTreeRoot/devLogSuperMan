package org.treeroot.devlog.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.treeroot.devlog.DevLog
import java.awt.Image
import java.io.File
import java.io.InputStream
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
     * 从输入流加载ImageBitmap
     */
    fun loadImageInputStream(file: String): InputStream? {
        return this::class.java.classLoader.getResourceAsStream(file)
    }

    /**
     * 从 classpath 资源路径加载 Image，失败时回退到 rollBack
     */
    fun loadImage(filePath: String, rollBack: Image): Image {
        return try {
            // 使用 use {} 确保 InputStream 在使用后自动关闭
            javaClass.classLoader.getResourceAsStream(filePath)
                ?.use { inputStream ->
                    ImageIO.read(inputStream) ?: rollBack
                }
            // 如果 getResourceAsStream 返回 null（资源不存在）
                ?: rollBack
        } catch (e: Exception) {
            DevLog.error("加载图像失败: $filePath, 错误: ${e.message}")
            rollBack
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