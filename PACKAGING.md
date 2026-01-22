# DevLog_SuperMan 应用打包指南

本文档介绍了如何构建和打包 DevLog_SuperMan 桌面应用。

## 打包脚本

项目包含两个打包脚本：

### 1. `package-app.sh` - 基础打包脚本

快速构建和打包应用的简单脚本。

```bash
# 运行基础打包脚本
./package-app.sh
```

### 2. `build-and-package.sh` - 高级打包脚本

提供更多的构建选项和控制。

```bash
# 基础构建
./build-and-package.sh

# 清理并构建
./build-and-package.sh -c

# 为特定平台构建
./build-and-package.sh -t macos-x64

# 构建调试版本
./build-and-package.sh -b debug

# 查看所有选项
./build-and-package.sh -h
```

## 参数说明

### 高级打包脚本参数

- `-t, --target PLATFORM` - 指定目标平台
  - `current` - 当前操作系统 (默认)
  - `macos-x64`, `macos-arm64` - macOS 不同架构
  - `windows-x64` - Windows 64位
  - `linux-x64` - Linux 64位

- `-b, --build-type TYPE` - 构建类型
  - `release` - 发布版本 (默认)
  - `debug` - 调试版本

- `-c, --clean` - 构建前清理之前的文件

- `-v, --verbose` - 启用详细输出

- `-h, --help` - 显示帮助信息

## 构建输出

构建完成后，输出文件位于：

- `composeApp/build/compose/app/` - 应用程序文件夹
- `composeApp/build/compose/binaries/` - 发行版安装包

根据操作系统不同，会生成不同类型的安装包：

- **macOS**: `.dmg` 文件
- **Windows**: `.msi` 文件
- **Linux**: `.deb` 或 `.rpm` 文件

## 手动构建命令

如果不想使用脚本，也可以直接使用 Gradle 命令：

```bash
# 构建并创建可分发的应用程序
./gradlew :composeApp:createDistributable

# 构建特定操作系统的安装包
./gradlew :composeApp:packageDistributionForCurrentOS

# 清理构建文件
./gradlew clean

# 运行应用
./gradlew :composeApp:run
```

## 故障排除

### 构建失败

1. 确保 Java 和 Gradle 环境正确配置
2. 检查项目依赖是否完整
3. 查看详细的构建日志

### 找不到安装包

某些情况下，可能只生成应用程序文件夹而没有安装包，这取决于您的操作系统和配置。

### 权限问题

确保脚本具有执行权限：

```bash
chmod +x package-app.sh
chmod +x build-and-package.sh
```

## 依赖项要求

- Java 11 或更高版本
- Gradle Wrapper (项目已包含)
- 构建工具链适用于目标平台

## 自定义打包配置

打包配置在 `composeApp/build.gradle.kts` 的 `nativeDistributions` 部分定义：

```kotlin
compose.desktop {
    application {
        // ... 其他配置
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.treeroot.devlog"
            packageVersion = "1.0.0"
        }
    }
}
```