# AI Code Assistant

一个功能完整的 Android 手机端 AI 编程助手应用，类似 Trae 手机端，支持 AI 聊天、本地代码编程、工具调用、手机权限操作、联网搜索等功能。

## ✨ 核心功能

### 🤖 AI 聊天
- 支持流式输出（SSE），逐字显示
- Markdown 渲染、代码块高亮、复制代码
- 多轮对话，上下文管理，历史记录本地保存
- 支持图片上传，多模态模型可分析图片
- 支持选择内置模型或自定义模型

### 💻 本地代码编程
- 内置代码编辑器（WebView + CodeMirror 6）
- 文件管理：新建、打开、保存、删除、重命名
- 支持本地运行代码：
  - WebView + Pyodide 运行 Python
  - WebView + JS 沙箱运行 JavaScript
  - 通过 Intent 调用 Termux 运行命令（备选）
- AI 可生成、修改、解释代码，并直接写入编辑器

### 🔧 AI 工具调用系统
- AI 可通过 Function Calling 请求安装工具
- 安装前必须弹出用户授权对话框
- 安装成功后，工具显示在"工具栏"页面
- 每个工具卡片显示：名称、版本、描述、调用次数、卸载按钮
- 卸载需二次确认
- 工具调用需记录日志，支持查看历史

### 📱 手机权限调用
AI 可通过函数调用请求权限并执行操作：
- 相机：拍照、扫描
- 麦克风：录音、语音识别
- 定位：获取经纬度
- 通讯录：读取、搜索联系人
- 短信：发送、读取
- 文件：读写本地文件
- 剪贴板：复制、粘贴
- 通知：发送通知
- 所有敏感权限必须动态申请，并给用户明确授权弹窗

### 🔍 上网搜索
- 集成搜索 API（Serper.dev 或 Bing Search）
- AI 可自动调用 `web_search(query)` 获取实时信息
- 支持结果引用来源

### ⚙️ 自定义添加 AI 模型
- 模型管理界面：添加、编辑、删除、设为默认
- 可配置：模型名称、Base URL、API Key、max_tokens、temperature、top_p、是否流式、是否支持视觉、是否开启思考
- 支持 OpenAI 兼容接口

## 🔧 内置固定 AI 模型

| 模型 | Base URL | Model ID | 特性 |
|-----|----------|----------|------|
| Kimi K3 (视觉) | `https://integrate.api.nvidia.com/v1` | `moonshotai/kimi-k3` | 支持图片输入，reasoning_effort=max |
| DeepSeek V4 Pro | `https://integrate.api.nvidia.com/v1` | `deepseek-ai/deepseek-v4-pro-0813` | 高性能推理 |
| DeepSeek V4 Flash | `https://integrate.api.nvidia.com/v1` | `deepseek-ai/deepseek-v4-flash-0731` | 支持 reasoning 字段 |
| Nemotron 3 Super 120B | `https://integrate.api.nvidia.com/v1` | `nvidia/nemotron-3-super-120b-a12b` | enable_thinking=true |
| Nemotron 3 Ultra 550B | `https://integrate.api.nvidia.com/v1` | `nvidia/nemotron-3-ultra-550b-a55b` | 超大参数模型 |

> ⚠️ **重要提醒**：内置 API 密钥仅用于演示，可能随时失效。建议自行到 [NVIDIA 平台](https://integrate.api.nvidia.com/) 申请并替换。所有密钥已加密存储。

## 🛠 技术栈

- **语言**：Kotlin
- **UI**：Jetpack Compose (Material 3)
- **构建**：Gradle Kotlin DSL
- **最低 SDK**：26 (Android 8.0)
- **目标 SDK**：35 (Android 15)
- **编译 SDK**：35
- **JDK**：17
- **网络**：OkHttp + Retrofit + SSE
- **JSON**：Moshi
- **本地存储**：Room Database
- **图片加载**：Coil
- **Markdown**：Markwon + Highlight.js
- **代码编辑器**：WebView + CodeMirror 6
- **权限**：AndroidX ActivityResult API
- **依赖注入**：Hilt
- **加密存储**：EncryptedSharedPreferences + Android Keystore

## 📦 依赖版本

```kotlin
// 核心
kotlin = 2.0.0
agp = 8.4.2
composeBom = 2024.06.00

// 网络
retrofit = 2.11.0
okhttp = 4.12.0
moshi = 1.15.1

// 数据库
room = 2.6.1

// UI
coil = 2.6.0
markwon = 4.6.4
material3 = 1.2.1

// 安全
security-crypto = 1.1.0-alpha06
datastore = 1.1.1
```

## 🚀 快速开始

### 环境要求
- Android Studio Ladybug (2024.2.1) 或更高版本
- JDK 17
- Android SDK 35
- Gradle 8.7+

### 1. 克隆项目
```bash
git clone <repository-url>
cd AICodeAssistant
```

### 2. 配置签名（发布版本需要）
创建 `keystore.properties` 文件（参考 `keystore.properties.example`）：
```properties
storeFile=../release.jks
storePassword=your_store_password
keyAlias=release
keyPassword=your_key_password
```

生成 keystore：
```bash
keytool -genkeypair -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias release
```

### 3. 替换内置 API 密钥（可选）
在 `ModelRepositoryImpl.kt` 中修改内置模型的 `apiKey` 字段，或在应用设置中添加自定义模型。

### 4. 编译运行
```bash
# 调试版
./gradlew assembleDebug
# 输出: app/build/outputs/apk/debug/app-debug.apk

# 发布版
./gradlew assembleRelease
# 输出: app/build/outputs/apk/release/app-release.apk

# 清理
./gradlew clean
```

### 5. 安装测试
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 📁 项目结构

```
AICodeAssistant/
├── app/
│   ├── src/main/
│   │   ├── java/com/aicodeassistant/
│   │   │   ├── AICodeApplication.kt          # Application 类
│   │   │   ├── data/
│   │   │   │   ├── local/                    # Room 数据库
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/                  # DAO 接口
│   │   │   │   │   ├── entity/               # 实体类
│   │   │   │   │   └── converters/           # TypeConverters
│   │   │   │   ├── remote/                   # 网络层
│   │   │   │   │   ├── ApiModels.kt          # API 数据模型
│   │   │   │   │   └── ApiService.kt         # Retrofit 接口
│   │   │   │   └── repository/               # Repository 实现
│   │   │   ├── domain/
│   │   │   │   ├── model/                    # 领域模型
│   │   │   │   ├── repository/               # Repository 接口
│   │   │   │   └── usecase/                  # UseCase (可扩展)
│   │   │   ├── ui/
│   │   │   │   ├── main/                     # 主界面
│   │   │   │   ├── chat/                     # 聊天界面
│   │   │   │   ├── code/                     # 代码编辑器
│   │   │   │   ├── tools/                    # 工具管理
│   │   │   │   ├── settings/                 # 设置界面
│   │   │   │   └── theme/                    # 主题
│   │   │   ├── services/                     # 后台服务
│   │   │   ├── receivers/                    # 广播接收器
│   │   │   ├── utils/                        # 工具类
│   │   │   └── di/                           # Hilt 依赖注入
│   │   ├── res/
│   │   │   ├── values/                       # 字符串、颜色、主题
│   │   │   ├── xml/                          # FileProvider、网络安全配置
│   │   │   └── drawable/                     # 图标资源
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts
│   └── proguard-rules.pro
├── gradle/wrapper/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── keystore.properties.example
└── README.md
```

## 🔐 权限说明

应用需要以下权限（均为动态申请）：

| 权限 | 用途 |
|------|------|
| `INTERNET` | 网络请求 |
| `CAMERA` | 拍照、扫描 |
| `RECORD_AUDIO` | 录音 |
| `ACCESS_FINE_LOCATION` | 精确定位 |
| `ACCESS_COARSE_LOCATION` | 大概定位 |
| `READ_CONTACTS` | 读取通讯录 |
| `WRITE_CONTACTS` | 写入通讯录 |
| `SEND_SMS` | 发送短信 |
| `READ_SMS` | 读取短信 |
| `READ_EXTERNAL_STORAGE` | 读取文件 |
| `WRITE_EXTERNAL_STORAGE` | 写入文件 |
| `POST_NOTIFICATIONS` | 发送通知 |
| `FOREGROUND_SERVICE` | 后台服务 |

## 🔧 函数调用 API

AI 可调用以下函数（JSON Schema 定义在 `ToolEntity.parameters` 中）：

```json
{
  "install_tool": { "tool_name": "string", "install_command": "string", "description": "string" },
  "uninstall_tool": { "tool_name": "string" },
  "list_tools": {},
  "run_tool": { "tool_name": "string", "args": "object" },
  "request_permission": { "permission_name": "string" },
  "take_photo": {},
  "record_audio": { "duration": "number" },
  "get_location": {},
  "search_contacts": { "query": "string" },
  "send_sms": { "phone": "string", "message": "string" },
  "web_search": { "query": "string" },
  "read_file": { "path": "string" },
  "write_file": { "path": "string", "content": "string" },
  "run_code": { "language": "string", "code": "string" }
}
```

## 🛡 安全与隐私

- API 密钥存储在 **EncryptedSharedPreferences** (Android Keystore 加密)
- 工具安装、权限调用、发送短信等敏感操作必须二次确认
- 提供操作日志，用户可查看 AI 的所有操作历史
- 内置模型密钥仅用于演示，提醒用户可自行更换
- 无任何数据上传到第三方服务器（除 AI API 调用外）

## 📝 代码运行方案说明

由于标准 APK 无法直接运行 Python/Node.js，本应用提供以下方案：

1. **WebView + Pyodide** - 在 WebView 中加载 Pyodide WASM 运行 Python（推荐）
2. **WebView + JS 沙箱** - 使用 CodeMirror + eval/Function 运行 JavaScript
3. **Termux Intent** - 通过 Intent 调用 Termux 执行命令（需用户安装 Termux）
4. **Runtime.exec()** - 尝试直接调用系统 Python/Node（部分设备/ROM 支持）

主项目不依赖 Termux，**可独立编译成 APK 运行**。

## 📄 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

- GitHub: [项目地址]
- 反馈邮箱: feedback@example.com
