package org.treeroot.devlog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.treeroot.devlog.page.DevLogTheme
import org.treeroot.devlog.page.MainApp
import org.treeroot.devlog.util.GlobalErrorHandlingWrapper

@Composable
fun App() {
    DevLogTheme {
        GlobalErrorHandlingWrapper {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                MainApp()
            }
        }
    }
}
