package com.aicodeassistant.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolType
import com.aicodeassistant.domain.repository.ToolRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ToolsViewModel @Inject constructor(
    private val toolRepository: ToolRepository
) : ViewModel() {

    private val _installedTools = MutableStateFlow<List<Tool>>(emptyList())
    val installedTools = _installedTools.asStateFlow()

    private val _availableTools = MutableStateFlow<List<Tool>>(getBuiltinAvailableTools())
    val availableTools = _availableTools.asStateFlow()

    private val _showInstallDialog = MutableStateFlow(false)
    val showInstallDialog = _showInstallDialog.asStateFlow()

    private val _selectedTool = MutableStateFlow<Tool?>(null)
    val selectedTool = _selectedTool.asStateFlow()

    init {
        observeTools()
    }

    private fun observeTools() {
        viewModelScope.launch {
            toolRepository.observeAllEnabledTools().collect { tools ->
                _installedTools.value = tools
            }
        }
    }

    fun showAvailableTools() {
        _showInstallDialog.value = true
    }

    fun installTool(tool: Tool) {
        viewModelScope.launch {
            toolRepository.installTool(tool.copy(type = ToolType.INSTALLED))
            _showInstallDialog.value = false
        }
    }

    fun confirmUninstall(tool: Tool) {
        _selectedTool.value = tool
    }

    fun uninstallTool(tool: Tool) {
        viewModelScope.launch {
            toolRepository.uninstallTool(tool.id)
            _selectedTool.value = null
        }
    }

    fun runTool(tool: Tool) {
        // TODO: Navigate to tool execution screen or run directly
    }

    fun viewToolLogs(tool: Tool) {
        // TODO: Navigate to tool logs screen
    }

    private fun getBuiltinAvailableTools(): List<Tool> {
        return listOf(
            Tool(
                name = "web_search",
                displayName = "网络搜索",
                version = "1.0.0",
                description = "通过 Serper.dev 或 Bing API 进行网络搜索",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "query" to mapOf("type" to "string", "description" to "搜索关键词")
                    ),
                    "required" to listOf("query")
                ),
                permissions = listOf("android.permission.INTERNET")
            ),
            Tool(
                name = "take_photo",
                displayName = "拍照",
                version = "1.0.0",
                description = "调用相机拍照并返回图片路径",
                type = ToolType.BUILTIN,
                parameters = mapOf("type" to "object", "properties" to mapOf()),
                permissions = listOf("android.permission.CAMERA")
            ),
            Tool(
                name = "record_audio",
                displayName = "录音",
                version = "1.0.0",
                description = "录制音频",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "duration" to mapOf("type" to "integer", "description" to "录音时长(秒)", "default" to 5)
                    )
                ),
                permissions = listOf("android.permission.RECORD_AUDIO")
            ),
            Tool(
                name = "get_location",
                displayName = "获取位置",
                version = "1.0.0",
                description = "获取当前 GPS 位置",
                type = ToolType.BUILTIN,
                parameters = mapOf("type" to "object", "properties" to mapOf()),
                permissions = listOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION")
            ),
            Tool(
                name = "search_contacts",
                displayName = "搜索联系人",
                version = "1.0.0",
                description = "搜索通讯录联系人",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "query" to mapOf("type" to "string", "description" to "搜索关键词")
                    ),
                    "required" to listOf("query")
                ),
                permissions = listOf("android.permission.READ_CONTACTS")
            ),
            Tool(
                name = "send_sms",
                displayName = "发送短信",
                version = "1.0.0",
                description = "发送短信",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "phone" to mapOf("type" to "string", "description" to "手机号码"),
                        "message" to mapOf("type" to "string", "description" to "短信内容")
                    ),
                    "required" to listOf("phone", "message")
                ),
                permissions = listOf("android.permission.SEND_SMS")
            ),
            Tool(
                name = "read_file",
                displayName = "读取文件",
                version = "1.0.0",
                description = "读取本地文件内容",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "path" to mapOf("type" to "string", "description" to "文件路径")
                    ),
                    "required" to listOf("path")
                ),
                permissions = listOf("android.permission.READ_EXTERNAL_STORAGE")
            ),
            Tool(
                name = "write_file",
                displayName = "写入文件",
                version = "1.0.0",
                description = "写入内容到本地文件",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "path" to mapOf("type" to "string", "description" to "文件路径"),
                        "content" to mapOf("type" to "string", "description" to "文件内容")
                    ),
                    "required" to listOf("path", "content")
                ),
                permissions = listOf("android.permission.WRITE_EXTERNAL_STORAGE")
            ),
            Tool(
                name = "run_code",
                displayName = "运行代码",
                version = "1.0.0",
                description = "在本地运行 Python/JavaScript/Shell 代码",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "language" to mapOf("type" to "string", "description" to "编程语言"),
                        "code" to mapOf("type" to "string", "description" to "代码内容")
                    ),
                    "required" to listOf("language", "code")
                ),
                permissions = listOf()
            ),
            Tool(
                name = "clipboard_copy",
                displayName = "复制到剪贴板",
                version = "1.0.0",
                description = "复制文本到剪贴板",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "text" to mapOf("type" to "string", "description" to "要复制的文本")
                    ),
                    "required" to listOf("text")
                ),
                permissions = listOf()
            ),
            Tool(
                name = "show_notification",
                displayName = "显示通知",
                version = "1.0.0",
                description = "发送系统通知",
                type = ToolType.BUILTIN,
                parameters = mapOf(
                    "type" to "object",
                    "properties" to mapOf(
                        "title" to mapOf("type" to "string", "description" to "通知标题"),
                        "body" to mapOf("type" to "string", "description" to "通知内容")
                    ),
                    "required" to listOf("title")
                ),
                permissions = listOf("android.permission.POST_NOTIFICATIONS")
            )
        )
    }
}
