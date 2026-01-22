package org.treeroot.devlog.util

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * 剪贴板辅助工具类
 */
object ClipboardHelper {
    
    /**
     * 将文本复制到剪贴板
     * @param text 要复制的文本
     * @return 是否成功复制
     */
    fun copyToClipboard(text: String): Boolean {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val selection = StringSelection(text)
            clipboard.setContents(selection, null)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * 从剪贴板获取文本
     * @return 剪贴板中的文本，如果获取失败则返回null
     */
    fun getTextFromClipboard(): String? {
        return try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val contents = clipboard.getContents(null)
            if (contents.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.stringFlavor)) {
                contents.getTransferData(java.awt.datatransfer.DataFlavor.stringFlavor) as? String
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}