package com.aicodeassistant.ui.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun AddEditModelDialog(
    model: AIModel?,
    onSave: (AIModel) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(model?.name ?: "") }
    var modelId by remember { mutableStateOf(model?.modelId ?: "") }
    var baseUrl by remember { mutableStateOf(model?.baseUrl ?: "https://api.openai.com/v1") }
    var apiKey by remember { mutableStateOf(model?.apiKey ?: "") }
    var maxTokens by remember { mutableStateOf(model?.maxTokens ?: 16384) }
    var temperature by remember { mutableStateOf(model?.temperature ?: 1.0f) }
    var topP by remember { mutableStateOf(model?.topP ?: 0.95f) }
    var isStream by remember { mutableStateOf(model?.isStream ?: true) }
    var supportsVision by remember { mutableStateOf(model?.supportsVision ?: false) }
    var supportsThinking by remember { mutableStateOf(model?.supportsThinking ?: false) }
    var isEnabled by remember { mutableStateOf(model?.isEnabled ?: true) }

    val isEditing = model != null

    ModalBottomSheet(
        sheetState = remember { androidx.compose.material3.ModalBottomSheetState(0.9f) },
        onDismissRequest = onDismiss,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEditing) "编辑模型" else "添加模型",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AICodeAssistant.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        androidx.compose.material.Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Close,
                            contentDescription = "关闭"
                        )
                    }
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

                // Name
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("模型名称", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    )
                )

                // Model ID
                TextField(
                    value = modelId,
                    onValueChange = { modelId = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Model ID (如: gpt-4, deepseek-chat)", color = AICodeAssistant.colorScheme.onSurfaceVariant) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    )
                )

                // Base URL
                TextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Base URL", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    )
                )

                // API Key
                TextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("API Key", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    ),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )

                // Max Tokens
                TextField(
                    value = maxTokens.toString(),
                    onValueChange = { maxTokens = it.toIntOrNull() ?: 16384 },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Max Tokens", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    ),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                // Temperature
                TextField(
                    value = temperature.toString(),
                    onValueChange = { temperature = it.toFloatOrNull() ?: 1.0f },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Temperature (0-2)", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    ),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                // Top P
                TextField(
                    value = topP.toString(),
                    onValueChange = { topP = it.toFloatOrNull() ?: 0.95f },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    singleLine = true,
                    label = { Text("Top P (0-1)", color = AICodeAssistant.colorScheme.onSurface) },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        textColor = AICodeAssistant.colorScheme.onSurface
                    ),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    )
                )

                // Checkboxes
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CheckboxRow(label = "流式输出", checked = isStream, onCheckedChange = { isStream = it })
                    CheckboxRow(label = "支持视觉", checked = supportsVision, onCheckedChange = { supportsVision = it })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CheckboxRow(label = "支持思考", checked = supportsThinking, onCheckedChange = { supportsThinking = it })
                    CheckboxRow(label = "启用", checked = isEnabled, onCheckedChange = { isEnabled = it })
                }

                // Save button
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(onClick = onDismiss) {
                        Text("取消")
                    }
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val newModel = AIModel(
                            id = model?.id ?: 0,
                            name = name,
                            modelId = modelId,
                            baseUrl = baseUrl,
                            apiKey = apiKey,
                            type = ModelType.CUSTOM,
                            maxTokens = maxTokens,
                            temperature = temperature,
                            topP = topP,
                            isStream = isStream,
                            supportsVision = supportsVision,
                            supportsThinking = supportsThinking,
                            isEnabled = isEnabled
                        )
                        onSave(newModel)
                    }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AICodeAssistant.colorScheme.primary
                    )) {
                        Text(if (isEditing) "保存" else "添加")
                    }
                }
            }
        }
    )
}

@Composable
fun CheckboxRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text = label, color = AICodeAssistant.colorScheme.onSurface)
    }
}
