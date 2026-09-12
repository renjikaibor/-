package com.aicodeassistant.ui.tools

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolType
import com.aicodeassistant.ui.tools.components.ToolCard
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun ToolsScreen(navController: androidx.navigation.NavController) {
    val viewModel: ToolsViewModel = viewModel()
    val installedTools by viewModel.installedTools.collectAsStateWithLifecycle(emptyList())
    val availableTools by viewModel.availableTools.collectAsStateWithLifecycle(emptyList())
    val showInstallDialog by viewModel.showInstallDialog.collectAsStateWithLifecycle(false)
    val selectedTool by viewModel.selectedTool.collectAsStateWithLifecycle<Tool?>(null)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = { Text("工具", fontWeight = FontWeight.Medium) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AICodeAssistant.colorScheme.surfaceContainer,
                titleContentColor = AICodeAssistant.colorScheme.onSurface
            ),
            actions = {
                IconButton(onClick = { viewModel.showAvailableTools() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Add,
                        contentDescription = "安装工具"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (installedTools.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.BuildCircle,
                            contentDescription = "无工具",
                            tint = AICodeAssistant.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            "暂无已安装工具",
                            color = AICodeAssistant.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp
                        )
                        Text(
                            "点击右上角 + 号安装新工具",
                            color = AICodeAssistant.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 14.sp
                        )
                        Button(onClick = { viewModel.showAvailableTools() }) {
                            Text("浏览可用工具")
                        }
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(installedTools) { tool ->
                        ToolCard(
                            tool = tool,
                            onUninstall = { viewModel.confirmUninstall(it) },
                            onRun = { viewModel.runTool(it) },
                            onViewLogs = { viewModel.viewToolLogs(it) }
                        )
                    }
                }
            }

            // Install dialog
            if (showInstallDialog) {
                AvailableToolsDialog(
                    tools = availableTools,
                    onInstall = { tool -> viewModel.installTool(tool) },
                    onDismiss = { viewModel.showInstallDialog.value = false }
                )
            }

            // Uninstall confirmation
            selectedTool?.let { tool ->
                UninstallConfirmDialog(
                    tool = tool,
                    onConfirm = { viewModel.uninstallTool(tool) },
                    onDismiss = { viewModel.selectedTool.value = null }
                )
            }
        }
    }
}

@Composable
fun AvailableToolsDialog(
    tools: List<Tool>,
    onInstall: (Tool) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        sheetState = remember { androidx.compose.material3.ModalBottomSheetState(0.8f) },
        onDismissRequest = onDismiss,
        sheetContent = {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("可用工具", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface)
                    IconButton(onClick = onDismiss) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.Close, contentDescription = "关闭")
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
                
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tools) { tool ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = androidx.compose.material3.CardDefaults.cardColors(
                                containerColor = AICodeAssistant.colorScheme.surfaceContainer
                            )
                        ) {
                            androidx.compose.foundation.layout.Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(tool.displayName, fontWeight = FontWeight.Medium, color = AICodeAssistant.colorScheme.onSurface)
                                        Text("v${tool.version}", fontSize = 12.sp, color = AICodeAssistant.colorScheme.onSurfaceVariant)
                                    }
                                    Button(onClick = { onInstall(tool) }) {
                                        Text("安装")
                                    }
                                }
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                                Text(tool.description, color = AICodeAssistant.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                            }
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun UninstallConfirmDialog(
    tool: Tool,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("卸载工具", color = AICodeAssistant.colorScheme.onSurface) },
        text = { Text("确定要卸载 ${tool.displayName} 吗？此操作不可撤销。", color = AICodeAssistant.colorScheme.onSurfaceVariant) },
        confirmButton = {
            Button(onClick = { onConfirm(); onDismiss() }, colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AICodeAssistant.colorScheme.error)) {
                Text("卸载", color = AICodeAssistant.colorScheme.onError)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
