package org.treeroot.devlog.util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.treeroot.devlog.DevLog
import java.awt.Image
import java.io.File
import java.io.InputStream
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

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
     * 从 classpath 资源路径加载 Image
     * @param filePath 资源路径（如 "images/icon.png"）
     * @return 加载的 Image，若失败则返回 null
     */
    fun loadImage(filePath: String): Image? {
        return try {
            javaClass.classLoader.getResourceAsStream(filePath)?.use { inputStream ->
                ImageIO.read(inputStream) // BufferedImage 是 Image 的子类
            } ?: run {
                DevLog.error("资源未找到: $filePath")
                null
            }
        } catch (e: Exception) {
            DevLog.error("加载图像失败: $filePath, 错误: ${e.message}", e)
            null
        }
    }

    /**
     * 将 Image 转换为 Icon
     */
    fun imageToIcon(filePath: String): Icon {
        return ImageIcon(loadImage(filePath))
    }
    
    /**
     * 将 Image 转换为指定尺寸的 Icon
     * @param filePath 资源路径
     * @param width 图标宽度
     * @param height 图标高度
     * @return 指定尺寸的 ImageIcon
     */
    fun imageToIcon(filePath: String, width: Int, height: Int): Icon {
        val image = loadImage(filePath)
        return if (image != null) {
            ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH))
        } else {
            ImageIcon()
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