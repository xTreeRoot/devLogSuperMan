package org.treeroot.devlog.page.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import java.util.*

/**
 * 图片选择器组件
 * 用于选择背景图片并自动保存到配置
 *
 * @param currentImagePath 当前图片路径
 * @param onImageSelected 图片选择回调
 * @param buttonText 按钮显示文本
 * @param modifier 组件修饰符
 */
@Composable
fun ImagePicker(
    currentImagePath: String,
    onImageSelected: (String) -> Unit,
    buttonText: String = if (currentImagePath.isEmpty()) "选择图片" else "更改图片",
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = {
                val fileDialog = FileDialog((null as Frame?), "选择图片", FileDialog.LOAD)
                // 设置过滤器只显示图片文件
                fileDialog.setFilenameFilter { _, name ->
                    val lowerName = name.lowercase(Locale.getDefault())
                    lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                            lowerName.endsWith(".png") || lowerName.endsWith(".gif") ||
                            lowerName.endsWith(".bmp")
                }
                fileDialog.isVisible = true
                if (fileDialog.file != null && fileDialog.directory != null) {
                    val selectedFilePath = File(fileDialog.directory, fileDialog.file).absolutePath
                    onImageSelected(selectedFilePath)
                }
            },
            modifier = Modifier.weight(1f)
                .height(48.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(buttonText)
        }

        OutlinedButton(
            onClick = {
                // 清除图片路径
                onImageSelected("")
            },
            modifier = Modifier.width(120.dp)
                .height(48.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("恢复默认")
        }
    }

    // 显示当前选择的图片路径
    if (currentImagePath.isNotEmpty()) {
        val file = File(currentImagePath)
        if (file.exists()) {
            Text(
                text = "已选择: ${file.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Text(
                text = "图片文件不存在，请重新选择",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}