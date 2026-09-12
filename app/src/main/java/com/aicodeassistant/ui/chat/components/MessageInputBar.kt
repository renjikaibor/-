package com.aicodeassistant.ui.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.R
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun MessageInputBar(
    onSendMessage: (String, List<String>) -> Unit,
    onAttachImage: () -> Unit,
    isLoading: Boolean,
    onStopGeneration: () -> Unit
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }

    val keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Send,
        keyboardType = androidx.compose.ui.text.input.KeyboardType.Text
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(AICodeAssistant.colorScheme.surface)
    ) {
        // Selected images preview
        if (selectedImages.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedImages.forEach { imageUri ->
                    Box(
                        modifier = Modifier.size(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Gray)
                    ) {
                        // Image preview
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Attach image button
            IconButton(
                onClick = onAttachImage,
                modifier = Modifier
                    .size(40.dp)
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Outlined.AddAPhoto,
                    contentDescription = "添加图片",
                    tint = AICodeAssistant.colorScheme.onSurfaceVariant
                )
            }

            // Text field
            androidx.compose.material3.TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .height(IntrinsicSize.Min)
                    .minimumHeight(48.dp),
                singleLine = false,
                maxLines = 5,
                keyboardOptions = keyboardOptions,
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onDone = { if (text.isNotBlank()) onSendMessage(text, selectedImages) }
                ),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                    focusedContainerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                    unfocusedContainerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                    textColor = AICodeAssistant.colorScheme.onSurface,
                    hintTextColor = AICodeAssistant.colorScheme.onSurfaceVariant,
                    placeholderColor = AICodeAssistant.colorScheme.onSurfaceVariant,
                    leadingIconColor = AICodeAssistant.colorScheme.onSurfaceVariant,
                    trailingIconColor = AICodeAssistant.colorScheme.onSurfaceVariant,
                    focusedIndicatorColor = AICodeAssistant.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = AICodeAssistant.colorScheme.error
                ),
                placeholder = { Text("输入消息...", color = AICodeAssistant.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(24.dp),
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
            )

            // Send/Stop button
            if (isLoading) {
                Button(
                    onClick = onStopGeneration,
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AICodeAssistant.colorScheme.errorContainer,
                        contentColor = AICodeAssistant.colorScheme.onErrorContainer
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Stop,
                        contentDescription = "停止生成",
                        tint = AICodeAssistant.colorScheme.onErrorContainer
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (text.isNotBlank() || selectedImages.isNotEmpty()) {
                            onSendMessage(text, selectedImages)
                            text = ""
                            selectedImages = emptyList()
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .padding(bottom = 8.dp),
                    enabled = text.isNotBlank() || selectedImages.isNotEmpty(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = AICodeAssistant.colorScheme.primary,
                        contentColor = AICodeAssistant.colorScheme.onPrimary,
                        disabledContainerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                        disabledContentColor = AICodeAssistant.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Send,
                        contentDescription = "发送",
                        tint = AICodeAssistant.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
