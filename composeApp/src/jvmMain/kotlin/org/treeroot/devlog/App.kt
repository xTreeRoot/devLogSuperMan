package org.treeroot.devlog

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.treeroot.devlog.theme.DevLogTheme
import org.treeroot.devlog.ui.MainApp

@Composable
fun App() {
    DevLogTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            MainApp()
        }
    }
}
