package com.aicodeassistant.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.max
import com.aicodeassistant.R
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.CodeLanguage
import com.aicodeassistant.domain.model.MessageRole
import com.aicodeassistant.domain.model.ToolCall
import com.aicodeassistant.domain.model.ToolCallStatus
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant
import io.noties.markwon.core.Markwon
import io.noties.markwon.ext.highlightjs.HighlightJsPlugin
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tables.TablesPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    onCopyCode: (String) -> Unit = {},
    onRegenerate: () -> Unit = {}
) {
    val isUser = message.role == MessageRole.USER
    val isTool = message.role == MessageRole.TOOL
    val isError = message.isError
    val isStreaming = message.isStreaming
    val showActions by remember { mutableStateOf(false) }

    val markwon = remember {
        Markwon.builder(androidx.compose.ui.platform.LocalContext.current)
            .usePlugin(TablesPlugin.create())
            .usePlugin(TaskListPlugin.create())
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(HtmlPlugin.create())
            .usePlugin(GlideImagesPlugin.create(androidx.compose.ui.platform.LocalContext.current))
            .usePlugin(HighlightJsPlugin.create())
            .build()
    }

    val messageColor = when {
        isUser -> AICodeAssistant.colorScheme.primaryContainer
        isTool -> AICodeAssistant.colorScheme.tertiaryContainer
        isError -> AICodeAssistant.colorScheme.errorContainer
        else -> AICodeAssistant.colorScheme.surfaceContainerHighest
    }

    val messageTextColor = when {
        isUser -> AICodeAssistant.colorScheme.onPrimaryContainer
        isTool -> AICodeAssistant.colorScheme.onTertiaryContainer
        isError -> AICodeAssistant.colorScheme.onErrorContainer
        else -> AICodeAssistant.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // AI Avatar
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.SmartToy,
                    contentDescription = "AI",
                    tint = AICodeAssistant.colorScheme.primary
                )
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(alignment = if (isUser) Alignment.End else Alignment.Start),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(alignment = if (isUser) Alignment.End else Alignment.Start)
                    .padding(vertical = 4.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = messageColor
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Images
                    if (message.images.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(message.images) { imageUri ->
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(R.drawable.ic_launcher),
                                    contentDescription = "Uploaded image",
                                    modifier = Modifier
                                        .width(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                    }

                    // Content
                    if (message.content.isNotBlank() || message.toolCalls.isNotEmpty()) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            // Render markdown for AI messages
                            if (!isUser && !isTool) {
                                MarkdownText(
                                    text = message.content,
                                    markwon = markwon,
                                    textColor = messageTextColor
                                )
                            } else {
                                Text(
                                    text = message.content,
                                    color = messageTextColor,
                                    style = TextStyle(fontSize = 15.sp, fontFamily = FontFamily.Monospace)
                                )
                            }

                            // Tool calls
                            if (message.toolCalls.isNotEmpty()) {
                                ToolCallsDisplay(
                                    toolCalls = message.toolCalls,
                                    textColor = messageTextColor
                                )
                            }
                        }
                    }

                    // Streaming indicator
                    if (isStreaming && !isUser) {
                        Row(
                            modifier = Modifier.padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = AICodeAssistant.colorScheme.primary
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "生成中...",
                                fontSize = 12.sp,
                                color = AICodeAssistant.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Actions
            if (showActions && !isUser && !isTool) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onCopyCode) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.ContentCopy,
                            contentDescription = "复制"
                        )
                    }
                    IconButton(onClick = onRegenerate) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Refresh,
                            contentDescription = "重新生成"
                        )
                    }
                }
            }
        }

        if (isUser) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.Person,
                    contentDescription = "User",
                    tint = AICodeAssistant.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun MarkdownText(
    text: String,
    markwon: Markwon,
    textColor: Color
) {
    // Use AndroidView to render markdown with Markwon
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context ->
            android.widget.TextView(context).apply {
                setTextColor(textColor.toArgb())
                setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 15f)
                setMovementMethod(android.text.method.LinkMovementMethod.getInstance())
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, text)
        }
    )
}

@Composable
fun ToolCallsDisplay(
    toolCalls: List<ToolCall>,
    textColor: Color
) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        toolCalls.forEach { toolCall ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = AICodeAssistant.colorScheme.surfaceContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                androidx.compose.foundation.layout.Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🔧 ${toolCall.name}",
                            fontWeight = FontWeight.Medium,
                            color = AICodeAssistant.colorScheme.primary
                        )
                        ToolCallStatusIcon(status = toolCall.status)
                    }
                    
                    if (toolCall.arguments.isNotEmpty()) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = "参数: ${com.google.gson.Gson().toJson(toolCall.arguments)}",
                            fontSize = 12.sp,
                            color = AICodeAssistant.colorScheme.onSurfaceVariant,
                            style = TextStyle(fontFamily = FontFamily.Monospace),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 3
                        )
                    }
                    
                    toolCall.result?.let { result ->
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = "结果: $result",
                            fontSize = 12.sp,
                            color = AICodeAssistant.colorScheme.onSurfaceVariant,
                            style = TextStyle(fontFamily = FontFamily.Monospace),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 5
                        )
                    }
                    
                    toolCall.error?.let { error ->
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = "错误: $error",
                            fontSize = 12.sp,
                            color = AICodeAssistant.colorScheme.error,
                            style = TextStyle(fontFamily = FontFamily.Monospace)
                        )
                    }
                }
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun ToolCallStatusIcon(status: ToolCallStatus) {
    val (icon, color) = when (status) {
        ToolCallStatus.PENDING -> androidx.compose.material.icons.Icons.Outlined.HourglassEmpty to AICodeAssistant.colorScheme.onSurfaceVariant
        ToolCallStatus.RUNNING -> androidx.compose.material.icons.Icons.Outlined.Sync to AICodeAssistant.colorScheme.primary
        ToolCallStatus.SUCCESS -> androidx.compose.material.icons.Icons.Outlined.CheckCircle to AICodeAssistant.colorScheme.primary
        ToolCallStatus.FAILED -> androidx.compose.material.icons.Icons.Outlined.Error to AICodeAssistant.colorScheme.error
        ToolCallStatus.CANCELLED -> androidx.compose.material.icons.Icons.Outlined.Cancel to AICodeAssistant.colorScheme.onSurfaceVariant
    }
    Icon(imageVector = icon, contentDescription = status.name, tint = color, modifier = Modifier.size(20.dp))
}
