# DevLog SuperMan

一款基于 Kotlin Multiplatform 的开发者专用工具，支持 SQL 日志解析与格式化、Elasticsearch DSL 美化、语法高亮等功能，提升开发调试效率。

## 功能特性

- **SQL 格式化**：支持 MyBatis SQL 日志解析与格式化
- **ES DSL 处理**：Elasticsearch DSL 美化与可视化
- **语法高亮**：支持 SQL 语法高亮显示
- **剪贴板监控**：自动监控剪贴板内容并处理
- **跨平台支持**：基于 Compose Multiplatform 构建

## 快速开始

### 环境要求

- JDK 11 或更高版本
- Gradle

### 构建项目

```bash
# 克隆项目
git clone https://github.com/xTreeRoot/DevLog_SuperMan.git

# 进入项目目录
cd DevLog_SuperMan

# 构建项目
./gradlew build
```

### 运行桌面应用

```bash
./gradlew :composeApp:run
```

## 使用说明

### SQL 格式化功能

1. 在"MyBatis SQL"标签页
2. 点击"粘贴并解析"按钮导入 SQL 日志
3. 查看格式化后的 SQL 结果

### ES DSL 处理功能

1. 在"Elasticsearch DSL"标签页
2. 点击"粘贴并解析"按钮导入 DSL
3. 查看格式化后的 DSL 结果

## 技术架构

- **Kotlin Multiplatform**：跨平台开发框架
- **Compose Multiplatform**：UI 框架
- **SLF4J**：日志框架
- **Gradle**：构建工具

## 文件结构

```
DevLog_SuperMan/
├── composeApp/           # Compose Multiplatform 应用
│   └── src/
│       └── jvmMain/
│           ├── kotlin/   # Kotlin 源码
│           └── resources/ # 资源文件
├── gradle/              # Gradle 配置
├── 样本数据/             # 示例数据文件
└── build.gradle.kts     # 构建脚本
```

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目！

## 许可证

[MIT License](LICENSE)