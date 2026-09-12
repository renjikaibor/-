package com.aicodeassistant.ui.code.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun TerminalPanel(
    output: String,
    onCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var commandInput by remember { mutableStateOf("") }
    val scrollState = remember { androidx.compose.foundation.lazy.rememberLazyListState() }

    Column(modifier = modifier.fillMaxSize().background(AICodeAssistant.colorScheme.surfaceContainer)) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("终端", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium, color = AICodeAssistant.colorScheme.onSurface)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { /* Clear */ }) {
                    Icon(androidx.compose.material.icons.Icons.Outlined.DeleteSweep, contentDescription = "清空")
                }
                IconButton(onClick = { /* Copy */ }) {
                    Icon(androidx.compose.material.icons.Icons.Outlined.ContentCopy, contentDescription = "复制")
                }
            }
        }

        // Output area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .background(AICodeAssistant.colorScheme.surface)
                .clip(RoundedCornerShape(8.dp))
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom
            ) {
                androidx.compose.foundation.Text(
                    text = output,
                    color = AICodeAssistant.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    style = androidx.compose.ui.text.TextStyle(
                        lineHeight = 18.sp,
                        overflow = TextOverflow.Visible
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$", color = AICodeAssistant.colorScheme.primary, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
            androidx.compose.material3.TextField(
                value = commandInput,
                onValueChange = { commandInput = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = androidx.compose.ui.text.input.KeyboardActions(
                    onDone = {
                        if (commandInput.isNotBlank()) {
                            onCommand(commandInput)
                            commandInput = ""
                        }
                    }
                ),
                colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
                    containerColor = AICodeAssistant.colorScheme.surfaceContainerHighest,
                    textColor = AICodeAssistant.colorScheme.onSurface,
                    cursorColor = AICodeAssistant.colorScheme.primary,
                    focusedIndicatorColor = AICodeAssistant.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                placeholder = { Text("输入命令...", color = AICodeAssistant.colorScheme.onSurfaceVariant) },
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}
