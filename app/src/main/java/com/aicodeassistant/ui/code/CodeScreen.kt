package com.aicodeassistant.ui.code

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
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
import com.aicodeassistant.ui.code.components.CodeEditorWebView
import com.aicodeassistant.ui.code.components.FileTreePanel
import com.aicodeassistant.ui.code.components.TerminalPanel
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun CodeScreen(navController: androidx.navigation.NavController) {
    val viewModel: CodeViewModel = viewModel()
    val showFileTree by viewModel.showFileTree.collectAsStateWithLifecycle(true)
    val showTerminal by viewModel.showTerminal.collectAsStateWithLifecycle(false)
    val currentFile by viewModel.currentFile.collectAsStateWithLifecycle(null)
    val terminalOutput by viewModel.terminalOutput.collectAsStateWithLifecycle("")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Text(
                    currentFile?.name ?? "无文件",
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = AICodeAssistant.colorScheme.surfaceContainer,
                titleContentColor = AICodeAssistant.colorScheme.onSurface
            ),
            navigationIcon = {
                IconButton(onClick = { viewModel.showFileTree.value = !viewModel.showFileTree.value }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Menu,
                        contentDescription = "文件树"
                    )
                }
            },
            actions = {
                IconButton(onClick = { viewModel.newFile() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Add,
                        contentDescription = "新建文件"
                    )
                }
                IconButton(onClick = { viewModel.saveFile() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Save,
                        contentDescription = "保存"
                    )
                }
                IconButton(onClick = { viewModel.runCode() }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.PlayArrow,
                        contentDescription = "运行"
                    )
                }
                IconButton(onClick = { viewModel.showTerminal.value = !viewModel.showTerminal.value }) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Outlined.Terminal,
                        contentDescription = "终端"
                    )
                }
            }
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                // File tree panel
                if (showFileTree) {
                    FileTreePanel(
                        files = viewModel.fileTree.value,
                        onFileClick = { file -> viewModel.openFile(file) },
                        onCreateFile = { parentId, name -> viewModel.createFile(parentId, name) },
                        onCreateFolder = { parentId, name -> viewModel.createFolder(parentId, name) },
                        onRename = { file, newName -> viewModel.renameFile(file, newName) },
                        onDelete = { file -> viewModel.deleteFile(file) },
                        modifier = Modifier.width(280.dp)
                    )
                    
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(AICodeAssistant.colorScheme.outlineVariant)
                    )
                }

                // Main editor area
                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        currentFile?.let { file ->
                            CodeEditorWebView(
                                file = file,
                                onContentChange = { content -> viewModel.updateFileContent(file.id, content) },
                                onCursorChange = { cursor, scroll -> viewModel.updateCursorPosition(file.id, cursor, scroll) }
                            )
                        }
                    }

                    // Terminal panel
                    if (showTerminal) {
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(AICodeAssistant.colorScheme.surfaceContainer)
                        ) {
                            TerminalPanel(output = terminalOutput)
                        }
                    }
                }
            }
        }
    }
}
