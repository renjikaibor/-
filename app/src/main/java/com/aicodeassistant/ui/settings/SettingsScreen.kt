package com.aicodeassistant.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aicodeassistant.R
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType
import com.aicodeassistant.ui.settings.components.ModelCard
import com.aicodeassistant.ui.settings.components.PermissionItem
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun SettingsScreen(navController: androidx.navigation.NavController) {
    val viewModel: SettingsViewModel = viewModel()
    val selectedSection by viewModel.selectedSection.collectAsStateWithLifecycle(SettingsSection.Models)
    val showAddModelDialog by viewModel.showAddModelDialog.collectAsStateWithLifecycle(false)
    val editingModel by viewModel.editingModel.collectAsStateWithLifecycle<AIModel?>(null)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = { Text("设置", fontWeight = FontWeight.Medium) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AICodeAssistant.colorScheme.surfaceContainer,
                titleContentColor = AICodeAssistant.colorScheme.onSurface
            )
        )

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Models section
                SettingsSectionCard(
                    title = "模型管理",
                    icon = androidx.compose.material.icons.Icons.Outlined.SmartToy,
                    isSelected = selectedSection == SettingsSection.Models,
                    onClick = { viewModel.selectedSection.value = SettingsSection.Models }
                ) {
                    if (selectedSection == SettingsSection.Models) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Built-in models
                            Text("内置模型", fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface, fontSize = 16.sp)
                            viewModel.builtinModels.collectAsStateWithLifecycle(emptyList()).value.forEach { model ->
                                ModelCard(
                                    model = model,
                                    isDefault = viewModel.defaultModelId.value == model.id,
                                    onSetDefault = { viewModel.setDefaultModel(it) },
                                    onEdit = { viewModel.editModel(it) },
                                    onDelete = { /* Built-in models cannot be deleted */ }
                                )
                            }

                            Divider(color = AICodeAssistant.colorScheme.outlineVariant)

                            // Custom models
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("自定义模型", fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface, fontSize = 16.sp)
                                Button(onClick = { viewModel.showAddModelDialog.value = true }) {
                                    Icon(androidx.compose.material.icons.Icons.Outlined.Add, contentDescription = "添加")
                                }
                            }
                            
                            viewModel.customModels.collectAsStateWithLifecycle(emptyList()).value.forEach { model ->
                                ModelCard(
                                    model = model,
                                    isDefault = viewModel.defaultModelId.value == model.id,
                                    onSetDefault = { viewModel.setDefaultModel(it) },
                                    onEdit = { viewModel.editModel(it) },
                                    onDelete = { viewModel.deleteModel(it) }
                                )
                            }
                        }
                    }
                }

                // Permissions section
                SettingsSectionCard(
                    title = "权限管理",
                    icon = androidx.compose.material.icons.Icons.Outlined.Security,
                    isSelected = selectedSection == SettingsSection.Permissions,
                    onClick = { viewModel.selectedSection.value = SettingsSection.Permissions }
                ) {
                    if (selectedSection == SettingsSection.Permissions) {
                        viewModel.permissions.collectAsStateWithLifecycle(emptyList()).value.forEach { permission ->
                            PermissionItem(
                                permission = permission,
                                onRequest = { viewModel.requestPermission(it) }
                            )
                        }
                    }
                }

                // Search API section
                SettingsSectionCard(
                    title = "搜索 API 配置",
                    icon = androidx.compose.material.icons.Icons.Outlined.Search,
                    isSelected = selectedSection == SettingsSection.SearchApi,
                    onClick = { viewModel.selectedSection.value = SettingsSection.SearchApi }
                ) {
                    if (selectedSection == SettingsSection.SearchApi) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ApiKeyInput(
                                label = "Serper.dev API Key",
                                value = viewModel.serperApiKey.value,
                                onValueChange = { viewModel.serperApiKey.value = it },
                                placeholder = "输入 Serper.dev API Key"
                            )
                            ApiKeyInput(
                                label = "Bing Search API Key",
                                value = viewModel.bingApiKey.value,
                                onValueChange = { viewModel.bingApiKey.value = it },
                                placeholder = "输入 Bing Search API Key"
                            )
                            Button(onClick = { viewModel.saveSearchApiKeys() }) {
                                Text("保存")
                            }
                        }
                    }
                }

                // Data management section
                SettingsSectionCard(
                    title = "数据管理",
                    icon = androidx.compose.material.icons.Icons.Outlined.Storage,
                    isSelected = selectedSection == SettingsSection.Data,
                    onClick = { viewModel.selectedSection.value = SettingsSection.Data }
                ) {
                    if (selectedSection == SettingsSection.Data) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ListItem(
                                headContent = { Icon(androidx.compose.material.icons.Icons.Outlined.DeleteSweep, contentDescription = "清理") },
                                leadingContent = {
                                    Column {
                                        Text("清理缓存", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("清理临时文件和缓存", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                trailingContent = { Text("执行", color = AICodeAssistant.colorScheme.primary) },
                                onClick = { viewModel.clearCache() }
                            )
                            ListItem(
                                headContent = { Icon(androidx.compose.material.icons.Icons.Outlined.Download, contentDescription = "导出") },
                                leadingContent = {
                                    Column {
                                        Text("导出数据", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("导出聊天记录、设置等数据", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                trailingContent = { Text("导出", color = AICodeAssistant.colorScheme.primary) },
                                onClick = { viewModel.exportData() }
                            )
                            ListItem(
                                headContent = { Icon(androidx.compose.material.icons.Icons.Outlined.Upload, contentDescription = "导入") },
                                leadingContent = {
                                    Column {
                                        Text("导入数据", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("从备份文件恢复数据", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                trailingContent = { Text("导入", color = AICodeAssistant.colorScheme.primary) },
                                onClick = { viewModel.importData() }
                            )
                        }
                    }
                }

                // About section
                SettingsSectionCard(
                    title = "关于",
                    icon = androidx.compose.material.icons.Icons.Outlined.Info,
                    isSelected = selectedSection == SettingsSection.About,
                    onClick = { viewModel.selectedSection.value = SettingsSection.About }
                ) {
                    if (selectedSection == SettingsSection.About) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ListItem(
                                leadingContent = {
                                    Column {
                                        Text("版本", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("1.0.0", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                }
                            )
                            ListItem(
                                leadingContent = {
                                    Column {
                                        Text("隐私政策", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("查看隐私政策", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = { /* Open privacy policy */ }
                            )
                            ListItem(
                                leadingContent = {
                                    Column {
                                        Text("GitHub", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("查看源代码", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = { /* Open GitHub */ }
                            )
                            ListItem(
                                leadingContent = {
                                    Column {
                                        Text("反馈建议", color = AICodeAssistant.colorScheme.onSurface)
                                        Text("发送反馈", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                },
                                onClick = { /* Open feedback */ }
                            )
                        }
                    }
                }
            }

            // Add/Edit Model Dialog
            if (showAddModelDialog || editingModel != null) {
                AddEditModelDialog(
                    model = editingModel,
                    onSave = { model ->
                        if (editingModel != null) {
                            viewModel.updateModel(model)
                        } else {
                            viewModel.addModel(model)
                        }
                        viewModel.showAddModelDialog.value = false
                        viewModel.editingModel.value = null
                    },
                    onDismiss = {
                        viewModel.showAddModelDialog.value = false
                        viewModel.editingModel.value = null
                    }
                )
            }
        }
    }
}

enum class SettingsSection {
    Models, Permissions, SearchApi, Data, About
}

@Composable
fun SettingsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (isSelected) AICodeAssistant.colorScheme.primaryContainer.copy(alpha = 0.3f) else AICodeAssistant.colorScheme.surfaceContainer
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headContent = { Icon(imageVector = icon, contentDescription = title, tint = AICodeAssistant.colorScheme.primary) },
                leadingContent = { Text(title, fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface) },
                trailingContent = {
                    Icon(
                        imageVector = if (isSelected) androidx.compose.material.icons.Icons.Outlined.ExpandLess else androidx.compose.material.icons.Icons.Outlined.ExpandMore,
                        contentDescription = if (isSelected) "折叠" else "展开",
                        tint = AICodeAssistant.colorScheme.onSurfaceVariant
                    )
                },
                onClick = onClick
            )
            if (isSelected) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
                content()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}

@Composable
fun ApiKeyInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = AICodeAssistant.colorScheme.onSurface)
        androidx.compose.material3.TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text(placeholder, color = AICodeAssistant.colorScheme.onSurfaceVariant) },
            colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                textColor = AICodeAssistant.colorScheme.onSurface,
                placeholderColor = AICodeAssistant.colorScheme.onSurfaceVariant
            )
        )
    }
}
