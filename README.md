# DevLog SuperMan

一款基于 Kotlin Multiplatform 的开发者专用工具，支持 SQL 日志解析与格式化、Elasticsearch DSL 美化、语法高亮等功能，提升开发调试效率。

## 功能特性

- **SQL 格式化**：支持 MyBatis SQL 日志解析与格式化，可检测MyBatis日志格式并提取SQL，替换参数占位符，提供多种格式化样式
- **ES DSL 处理**：Elasticsearch DSL 美化与可视化，支持从文本中提取JSON、分离DSL和响应、处理curl命令等
- **MySQL 数据库操作**：支持数据库连接、查询、更新、参数化操作及连接测试功能
- **AI 集成服务**：集成多种AI模型（如智谱AI等），提供对话和批量处理功能
- **自定义界面**：支持自定义背景图片和透明度调节
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

1. 在"MyBatis SQL解析"标签页
2. 点击"粘贴并解析"按钮导入 SQL 日志，或使用"普通格式化"进行基础格式化
3. 查看格式化后的 SQL 结果，系统会自动检测SQL语法并显示状态
4. 点击"复制"按钮将格式化后的SQL复制到剪贴板

### ES DSL 处理功能

1. 在"Elasticsearch DSL"标签页
2. 点击"粘贴并解析"按钮导入 DSL
3. 查看格式化后的 DSL 结果

### MySQL 数据库功能

1. 通过代码配置数据库连接信息（主机、端口、数据库名、用户名、密码等）
2. 使用 MySqlDatabaseService 进行数据库操作，支持查询、更新、参数化操作等
3. 可使用 testConnection 方法测试数据库连接

### AI 服务功能

1. 配置AI服务（如智谱AI等）的API密钥
2. 通过 AiService 接口进行对话，支持单条消息和批量消息处理
3. 可切换不同AI提供商和模型

### 界面个性化设置

1. 在"设置"标签页可以启用/禁用剪贴板监控功能
2. 调整界面背景透明度
3. 选择自定义背景图片
4. 设置将在应用重启后保持

## 技术架构

- **Kotlin Multiplatform**：跨平台开发框架
- **Compose Multiplatform**：UI 框架
- **SLF4J**：日志框架
- **Gradle**：构建工具
- **HikariCP**：高性能 JDBC 连接池
- **MySQL Connector/J**：MySQL 数据库驱动
- **Gson**：JSON 解析库
- **JetBrains Annotations**：代码注解支持

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