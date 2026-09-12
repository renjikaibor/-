package com.aicodeassistant.ui.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.aicodeassistant.data.remote.ApiModels.ContentPart
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.MessageRole
import com.aicodeassistant.ui.chat.components.ChatMessageItem
import com.aicodeassistant.ui.chat.components.MessageInputBar
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant
import kotlinx.coroutines.flow.collectAsStateWithLifecycle

@Composable
fun ChatScreen(navController: androidx.navigation.NavController) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsStateWithLifecycle(emptyList())
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle(false)
    val currentModel by viewModel.currentModel.collectAsStateWithLifecycle(null)
    val showModelSelector by viewModel.showModelSelector.collectAsStateWithLifecycle(false)

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = { Text("AI Code Assistant", fontWeight = FontWeight.Medium) },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AICodeAssistant.colorScheme.surfaceContainer,
                titleContentColor = AICodeAssistant.colorScheme.onSurface
            ),
            navigationIcon = {
                IconButton(onClick = { /* Open drawer */ }) {
                    androidx.compose.material.Icon(
                        androidx.compose.material.icons.Icons.Outlined.Menu,
                        contentDescription = "Menu"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.showModelSelector.value = true }) {
                    androidx.compose.material.Icon(
                        androidx.compose.material.icons.Icons.Outlined.SmartToy,
                        contentDescription = "Switch Model"
                    )
                }
                IconButton(onClick = { viewModel.newChat() }) {
                    androidx.compose.material.Icon(
                        androidx.compose.material.icons.Icons.Outlined.Add,
                        contentDescription = "New Chat"
                    )
                }
            }
        )

        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                reverseLayout = true,
                autoScroll = true
            ) {
                items(messages.reversed()) { message ->
                    ChatMessageItem(message = message)
                }
                if (isLoading) {
                    item {
                        LoadingMessageItem()
                    }
                }
            }

            if (showModelSelector) {
                ModelSelectorBottomSheet(
                    currentModel = currentModel,
                    onModelSelected = { model ->
                        viewModel.selectModel(model)
                        viewModel.showModelSelector.value = false
                    },
                    onDismiss = { viewModel.showModelSelector.value = false }
                )
            }
        }

        MessageInputBar(
            onSendMessage = { content, images ->
                viewModel.sendMessage(content, images)
            },
            onAttachImage = { viewModel.pickImage() },
            isLoading = isLoading,
            onStopGeneration = { viewModel.stopGeneration() }
        )
    }
}

@Composable
fun LoadingMessageItem() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = androidx.compose.foundation.layout.padding(16.dp),
            color = AICodeAssistant.colorScheme.primary
        )
        Text(
            text = "AI 正在思考...",
            color = AICodeAssistant.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ModelSelectorBottomSheet(
    currentModel: com.aicodeassistant.domain.model.AIModel?,
    onModelSelected: (com.aicodeassistant.domain.model.AIModel) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.ModalBottomSheet(
        sheetState = remember { androidx.compose.material3.ModalBottomSheetState(0.5f) },
        onDismissRequest = onDismiss,
        sheetContent = {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("选择模型", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))
                
                viewModel.availableModels.collectAsStateWithLifecycle(emptyList()).value.forEach { model ->
                    androidx.compose.material3.ListItem(
                        modifier = Modifier.fillMaxWidth(),
                        headContent = {
                            androidx.compose.material.Icon(
                                if (model.supportsVision) 
                                    androidx.compose.material.icons.Icons.Outlined.Image 
                                else 
                                    androidx.compose.material.icons.Icons.Outlined.SmartToy,
                                contentDescription = model.name,
                                tint = if (currentModel?.id == model.id) AICodeAssistant.colorScheme.primary else AICodeAssistant.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingContent = {
                            androidx.compose.foundation.layout.Column {
                                Text(model.name, color = AICodeAssistant.colorScheme.onSurface)
                                if (model.supportsVision) {
                                    Text("支持视觉", fontSize = 12.sp, color = AICodeAssistant.colorScheme.primary)
                                }
                                if (model.supportsThinking) {
                                    Text("支持思考", fontSize = 12.sp, color = AICodeAssistant.colorScheme.tertiary)
                                }
                            }
                        },
                        onClick = { onModelSelected(model) }
                    )
                }
            }
        }
    )
}
