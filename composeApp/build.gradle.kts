import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.sqldelight)
}

kotlin {
    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation("ch.qos.logback:logback-classic:1.4.11")
            implementation("ai.z.openapi:zai-sdk:0.3.0")
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
            implementation("com.google.code.gson:gson:2.10.1")
            // 添加Jackson YAML处理依赖
            implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
            implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
            implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
            // 移除JDBC依赖，使用纯Kotlin文件存储
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.treeroot.devlog.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.treeroot.devlog"
            packageVersion = "1.0.0"
        }
    }
}