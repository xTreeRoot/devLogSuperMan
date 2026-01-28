# 配置说明

## 配置文件说明

本项目使用YAML格式的配置文件来管理应用配置和个人设置。

### 配置文件

- `config-template.yaml` - 配置文件模板，包含所有可用配置项
- `config.yaml` - 个人配置文件（已加入.gitignore，不会被提交）

### 使用说明

1. 将 `config-template.yaml` 复制为 `config.yaml`
2. 在 `config.yaml` 中填入您的个人配置信息
3. 运行应用时，系统会自动加载 `config.yaml` 中的配置

### 配置项说明

#### AI 配置

- `ai.zhipu.api_key` - 智谱AI的API密钥
- `ai.zhipu.default_model` - 智谱AI的默认模型名称
- `ai.zhipu.max_tokens` - 最大token数量
- `ai.zhipu.temperature` - 生成温度参数

- `ai.openai.api_key` - OpenAI的API密钥
- `ai.openai.default_model` - OpenAI的默认模型名称

- `ai.qwen.api_key` - 通义千问的API密钥
- `ai.qwen.default_model` - 通义千问的默认模型名称

#### 应用配置

- `app.name` - 应用名称
- `app.version` - 应用版本
- `app.debug` - 是否开启调试模式

#### 数据库配置

- `database.url` - 数据库连接地址
- `database.username` - 数据库用户名
- `database.password` - 数据库密码

#### 日志配置

- `logging.level` - 日志级别

### 安全提示

- 请勿将包含个人API密钥的 `config.yaml` 文件提交到版本控制系统
- 该文件已自动添加到 `.gitignore` 中，但仍请注意不要手动提交
- 分享代码时，请确保只分享 `config-template.yaml` 文件