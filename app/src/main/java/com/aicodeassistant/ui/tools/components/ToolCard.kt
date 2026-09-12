package com.aicodeassistant.ui.tools.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.domain.model.Tool
import com.aicodeassistant.domain.model.ToolType
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun ToolCard(
    tool: Tool,
    onUninstall: (Tool) -> Unit,
    onRun: (Tool) -> Unit,
    onViewLogs: (Tool) -> Unit,
    modifier: Modifier = Modifier
) {
    val typeColor = when (tool.type) {
        ToolType.BUILTIN -> AICodeAssistant.colorScheme.primary
        ToolType.INSTALLED -> AICodeAssistant.colorScheme.tertiary
        ToolType.CUSTOM -> AICodeAssistant.colorScheme.secondary
    }

    val typeLabel = when (tool.type) {
        ToolType.BUILTIN -> "内置"
        ToolType.INSTALLED -> "已安装"
        ToolType.CUSTOM -> "自定义"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = AICodeAssistant.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Build,
                        contentDescription = tool.displayName,
                        tint = typeColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = tool.displayName,
                            fontWeight = FontWeight.Medium,
                            color = AICodeAssistant.colorScheme.onSurface,
                            fontSize = 16.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .background(typeColor.copy(alpha = 0.2f))
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                Text(
                                    text = typeLabel,
                                    fontSize = 10.sp,
                                    color = typeColor,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "v${tool.version}",
                                fontSize = 12.sp,
                                color = AICodeAssistant.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { onRun(tool) }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.PlayArrow,
                            contentDescription = "运行",
                            tint = AICodeAssistant.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { onViewLogs(tool) }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.History,
                            contentDescription = "日志",
                            tint = AICodeAssistant.colorScheme.onSurfaceVariant
                        )
                    }
                    if (tool.type != ToolType.BUILTIN) {
                        IconButton(onClick = { onUninstall(tool) }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Delete,
                                contentDescription = "卸载",
                                tint = AICodeAssistant.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Description
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = tool.description,
                color = AICodeAssistant.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            // Stats
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Timer,
                        contentDescription = "调用次数",
                        tint = AICodeAssistant.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${tool.callCount} 次调用",
                        fontSize = 12.sp,
                        color = AICodeAssistant.colorScheme.onSurfaceVariant
                    )
                }

                tool.lastCalledAt?.let { lastCalled ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.AccessTime,
                            contentDescription = "上次调用",
                            tint = AICodeAssistant.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "上次: ${android.text.format.DateFormat.format("MM-dd HH:mm", lastCalled)}",
                            fontSize = 12.sp,
                            color = AICodeAssistant.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Permissions
                if (tool.permissions.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Outlined.Security,
                            contentDescription = "权限",
                            tint = AICodeAssistant.colorScheme.warning,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${tool.permissions.size} 项权限",
                            fontSize = 12.sp,
                            color = AICodeAssistant.colorScheme.warning
                        )
                    }
                }
            }
        }
    }
}
