package com.aicodeassistant.ui.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.aicodeassistant.domain.model.AIModel
import com.aicodeassistant.domain.model.ModelType
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun ModelCard(
    model: AIModel,
    isDefault: Boolean,
    onSetDefault: (AIModel) -> Unit,
    onEdit: (AIModel) -> Unit,
    onDelete: (AIModel) -> Unit
) {
    val typeColor = when (model.type) {
        ModelType.BUILTIN -> AICodeAssistant.colorScheme.primary
        ModelType.CUSTOM -> AICodeAssistant.colorScheme.tertiary
    }

    val typeLabel = when (model.type) {
        ModelType.BUILTIN -> "内置"
        ModelType.CUSTOM -> "自定义"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = AICodeAssistant.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        imageVector = if (model.supportsVision)
                            androidx.compose.material.icons.Icons.Outlined.Image
                        else
                            androidx.compose.material.icons.Icons.Outlined.SmartToy,
                        contentDescription = model.name,
                        tint = typeColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Column {
                        Text(
                            text = model.displayName,
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
                                text = model.modelId,
                                fontSize = 12.sp,
                                color = AICodeAssistant.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isDefault) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .background(AICodeAssistant.colorScheme.primary.copy(alpha = 0.2f))
                                .clip(RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "默认",
                                fontSize = 10.sp,
                                color = AICodeAssistant.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Button(onClick = { onSetDefault(model) }) {
                            Text("设为默认")
                        }
                    }

                    if (model.type == ModelType.CUSTOM) {
                        IconButton(onClick = { onEdit(model) }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Edit,
                                contentDescription = "编辑",
                                tint = AICodeAssistant.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { onDelete(model) }) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Outlined.Delete,
                                contentDescription = "删除",
                                tint = AICodeAssistant.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Capabilities
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (model.supportsVision) {
                    CapabilityBadge(label = "视觉", color = AICodeAssistant.colorScheme.primary)
                }
                if (model.supportsThinking) {
                    CapabilityBadge(label = "思考", color = AICodeAssistant.colorScheme.tertiary)
                }
                CapabilityBadge(label = "流式", color = AICodeAssistant.colorScheme.secondary)
                CapabilityBadge(label = "${model.maxTokens} tokens", color = AICodeAssistant.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                CapabilityBadge(label = "Temp: ${model.temperature}", color = AICodeAssistant.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun CapabilityBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .background(color.copy(alpha = 0.15f))
            .clip(RoundedCornerShape(4.dp))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = color,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
