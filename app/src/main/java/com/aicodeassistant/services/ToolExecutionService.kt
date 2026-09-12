package com.aicodeassistant.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolCall
import com.aicodeassistant.domain.model.ToolCallStatus
import com.aicodeassistant.domain.repository.ToolRepository
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

class ToolExecutionService : Service() {

    private var toolRepository: ToolRepository? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        val appComponent = com.aicodeassistant.AICodeApplication.getAppComponent(this)
        toolRepository = appComponent.toolRepository
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    suspend fun executeTool(tool: Tool, call: ToolCall): ToolCall {
        return try {
            val startTime = System.currentTimeMillis()
            
            // Update status to running
            val runningCall = call.copy(status = ToolCallStatus.RUNNING)
            toolRepository?.updateCallLog(runningCall.toLog())
            
            val result = when (tool.name) {
                "web_search" -> executeWebSearch(call.arguments)
                "take_photo" -> executeTakePhoto()
                "record_audio" -> executeRecordAudio(call.arguments)
                "get_location" -> executeGetLocation()
                "search_contacts" -> executeSearchContacts(call.arguments)
                "send_sms" -> executeSendSms(call.arguments)
                "read_file" -> executeReadFile(call.arguments)
                "write_file" -> executeWriteFile(call.arguments)
                "run_code" -> executeRunCode(call.arguments)
                "run_termux" -> executeTermuxCommand(call.arguments)
                "clipboard_copy" -> executeClipboardCopy(call.arguments)
                "clipboard_paste" -> executeClipboardPaste()
                "show_notification" -> executeShowNotification(call.arguments)
                else -> executeCustomTool(tool, call.arguments)
            }

            val duration = System.currentTimeMillis() - startTime
            val successCall = runningCall.copy(
                status = ToolCallStatus.SUCCESS,
                result = result,
                completedAt = java.util.Date()
            )
            toolRepository?.updateCallLog(successCall.toLog())
            toolRepository?.incrementCallCount(tool.id)
            successCall
        } catch (e: Exception) {
            val errorCall = call.copy(
                status = ToolCallStatus.FAILED,
                error = e.message,
                completedAt = java.util.Date()
            )
            toolRepository?.updateCallLog(errorCall.toLog())
            errorCall
        }
    }

    private suspend fun executeWebSearch(args: Map<String, Any>): String {
        val query = args["query"] as? String ?: return "Error: query parameter required"
        // TODO: Implement actual search via Serper.dev or Bing API
        return "Search results for: $query (not implemented yet)"
    }

    private suspend fun executeTakePhoto(): String {
        // TODO: Launch camera intent and return image path
        return "Photo taken (not implemented yet)"
    }

    private suspend fun executeRecordAudio(args: Map<String, Any>): String {
        val duration = (args["duration"] as? Int) ?: 5
        // TODO: Implement audio recording
        return "Recorded audio for ${duration}s (not implemented yet)"
    }

    private suspend fun executeGetLocation(): String {
        // TODO: Implement location retrieval
        return "Location: (not implemented yet)"
    }

    private suspend fun executeSearchContacts(args: Map<String, Any>): String {
        val query = args["query"] as? String ?: return "Error: query parameter required"
        // TODO: Implement contacts search
        return "Contacts matching '$query' (not implemented yet)"
    }

    private suspend fun executeSendSms(args: Map<String, Any>): String {
        val phone = args["phone"] as? String ?: return "Error: phone parameter required"
        val message = args["message"] as? String ?: return "Error: message parameter required"
        // TODO: Implement SMS sending
        return "SMS sent to $phone (not implemented yet)"
    }

    private suspend fun executeReadFile(args: Map<String, Any>): String {
        val path = args["path"] as? String ?: return "Error: path parameter required"
        return try {
            java.io.File(path).readText()
        } catch (e: Exception) {
            "Error reading file: ${e.message}"
        }
    }

    private suspend fun executeWriteFile(args: Map<String, Any>): String {
        val path = args["path"] as? String ?: return "Error: path parameter required"
        val content = args["content"] as? String ?: return "Error: content parameter required"
        return try {
            java.io.File(path).writeText(content)
            "File written successfully"
        } catch (e: Exception) {
            "Error writing file: ${e.message}"
        }
    }

    private suspend fun executeRunCode(args: Map<String, Any>): String {
        val language = args["language"] as? String ?: return "Error: language parameter required"
        val code = args["code"] as? String ?: return "Error: code parameter required"
        
        return when (language.lowercase()) {
            "python" -> runPythonCode(code)
            "javascript", "js" -> runJavaScriptCode(code)
            "shell", "bash", "sh" -> runShellCommand(code)
            else -> "Unsupported language: $language"
        }
    }

    private suspend fun runPythonCode(code: String): String {
        // Try to run via Python if available, otherwise use Pyodide in WebView
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("python3", "-c", code))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            process.waitFor()
            if (error.isNotBlank()) "Error: $error" else output
        } catch (e: Exception) {
            "Python execution failed: ${e.message} (try using WebView + Pyodide)"
        }
    }

    private suspend fun runJavaScriptCode(code: String): String {
        // Try Node.js if available
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("node", "-e", code))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            process.waitFor()
            if (error.isNotBlank()) "Error: $error" else output
        } catch (e: Exception) {
            "JavaScript execution failed: ${e.message} (try using WebView)"
        }
    }

    private suspend fun runShellCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
            process.waitFor()
            if (error.isNotBlank()) "Error: $error" else output
        } catch (e: Exception) {
            "Shell execution failed: ${e.message}"
        }
    }

    private suspend fun executeTermuxCommand(args: Map<String, Any>): String {
        val command = args["command"] as? String ?: return "Error: command parameter required"
        // TODO: Implement Termux intent execution
        return "Termux command: $command (not implemented yet)"
    }

    private suspend fun executeClipboardCopy(args: Map<String, Any>): String {
        val text = args["text"] as? String ?: return "Error: text parameter required"
        // TODO: Implement clipboard copy
        return "Copied to clipboard (not implemented yet)"
    }

    private suspend fun executeClipboardPaste(): String {
        // TODO: Implement clipboard paste
        return "Clipboard content (not implemented yet)"
    }

    private suspend fun executeShowNotification(args: Map<String, Any>): String {
        val title = args["title"] as? String ?: "Notification"
        val body = args["body"] as? String ?: ""
        // TODO: Implement notification
        return "Notification shown: $title (not implemented yet)"
    }

    private suspend fun executeCustomTool(tool: Tool, args: Map<String, Any>): String {
        // Execute custom installed tool
        tool.entryPoint?.let { entryPoint ->
            return try {
                val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", "$entryPoint ${gson.toJson(args)}"))
                val output = BufferedReader(InputStreamReader(process.inputStream)).readText()
                val error = BufferedReader(InputStreamReader(process.errorStream)).readText()
                process.waitFor()
                if (error.isNotBlank()) "Error: $error" else output
            } catch (e: Exception) {
                "Custom tool execution failed: ${e.message}"
            }
        }
        return "No entry point defined for tool: ${tool.name}"
    }

    private fun ToolCall.toLog() = com.aicodeassistant.domain.model.ToolCallLog(
        toolId = null, // Will be set by repository
        sessionId = null,
        toolName = this.name,
        inputArgs = this.arguments,
        outputResult = result?.let { mapOf("result" to it) },
        status = this.status,
        errorMessage = error,
        durationMs = 0,
        createdAt = java.util.Date()
    )
}
