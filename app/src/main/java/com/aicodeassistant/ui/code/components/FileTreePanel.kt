package com.aicodeassistant.ui.code.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aicodeassistant.domain.model.FileItem
import com.aicodeassistant.domain.model.FileType
import com.aicodeassistant.ui.theme.Theme.AICodeAssistant

@Composable
fun FileTreePanel(
    files: List<FileItem>,
    onFileClick: (FileItem) -> Unit,
    onCreateFile: (Long?, String) -> Unit,
    onCreateFolder: (Long?, String) -> Unit,
    onRename: (FileItem, String) -> Unit,
    onDelete: (FileItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedFolders by remember { mutableStateOf<MutableSet<Long>>(mutableSetOf()) }

    Box(modifier = modifier.fillMaxHeight().background(AICodeAssistant.colorScheme.surface)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("文件", fontWeight = FontWeight.Bold, color = AICodeAssistant.colorScheme.onSurface, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = { /* Refresh */ }) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.Refresh, contentDescription = "刷新")
                    }
                    IconButton(onClick = { /* New folder */ }) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.CreateNewFolder, contentDescription = "新建文件夹")
                    }
                    IconButton(onClick = { /* New file */ }) {
                        Icon(androidx.compose.material.icons.Icons.Outlined.NoteAdd, contentDescription = "新建文件")
                    }
                }
            }

            // File tree
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(files) { file ->
                    FileTreeItem(
                        file = file,
                        depth = 0,
                        expandedFolders = expandedFolders,
                        onClick = onFileClick,
                        onCreateFile = onCreateFile,
                        onCreateFolder = onCreateFolder,
                        onRename = onRename,
                        onDelete = onDelete
                    )
                }
            }
        }
    }
}

@Composable
fun FileTreeItem(
    file: FileItem,
    depth: Int,
    expandedFolders: MutableSet<Long>,
    onClick: (FileItem) -> Unit,
    onCreateFile: (Long?, String) -> Unit,
    onCreateFolder: (Long?, String) -> Unit,
    onRename: (FileItem, String) -> Unit,
    onDelete: (FileItem) -> Unit
) {
    val isExpanded = expandedFolders.contains(file.id)
    val isDirectory = file.type == FileType.DIRECTORY
    val hasChildren = file.metadata["hasChildren"]?.toBoolean() == true

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (8 + depth * 16).dp, vertical = 4.dp)
            .background(
                if (file.isOpen) AICodeAssistant.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isDirectory && hasChildren) {
            IconButton(onClick = { expandedFolders.add(file.id) }) {
                Icon(
                    imageVector = if (isExpanded) 
                        androidx.compose.material.icons.Icons.Outlined.ExpandMore 
                    else 
                        androidx.compose.material.icons.Icons.Outlined.ChevronRight,
                    contentDescription = if (isExpanded) "折叠" else "展开",
                    tint = AICodeAssistant.colorScheme.onSurfaceVariant
                )
            }
        } else {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(24.dp))
        }

        Icon(
            imageVector = if (isDirectory) {
                if (isExpanded) androidx.compose.material.icons.Icons.Outlined.FolderOpen else androidx.compose.material.icons.Icons.Outlined.Folder
            } else {
                getFileIcon(file)
            },
            contentDescription = file.name,
            tint = if (isDirectory) AICodeAssistant.colorScheme.primary else AICodeAssistant.colorScheme.onSurfaceVariant
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = file.name,
            color = AICodeAssistant.colorScheme.onSurface,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        if (file.isDirty) {
            Box(
                modifier = Modifier.size(8.dp)
                    .background(AICodeAssistant.colorScheme.error)
            )
        }
    }
        .clickable { if (!isDirectory) onClick(file) }
        .onClickLabel = file.name

    if (isDirectory && isExpanded && hasChildren) {
        // Children would be loaded here
        // For now, we show a placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (8 + (depth + 1) * 16).dp)
                .height(1.dp)
                .background(AICodeAssistant.colorScheme.outlineVariant.copy(alpha = 0.3f))
        )
    }
}

fun getFileIcon(file: FileItem): androidx.compose.ui.graphics.vector.ImageVector {
    return when (file.language) {
        com.aicodeassistant.domain.model.CodeLanguage.PYTHON -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.JAVASCRIPT,
        com.aicodeassistant.domain.model.CodeLanguage.TYPESCRIPT -> androidx.compose.material.icons.Icons.Outlined.Javascript
        com.aicodeassistant.domain.model.CodeLanguage.HTML -> androidx.compose.material.icons.Icons.Outlined.Html
        com.aicodeassistant.domain.model.CodeLanguage.CSS -> androidx.compose.material.icons.Icons.Outlined.Style
        com.aicodeassistant.domain.model.CodeLanguage.JSON -> androidx.compose.material.icons.Icons.Outlined.DataObject
        com.aicodeassistant.domain.model.CodeLanguage.MARKDOWN -> androidx.compose.material.icons.Icons.Outlined.Description
        com.aicodeassistant.domain.model.CodeLanguage.KOTLIN -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.JAVA -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.CPP -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.RUST -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.GO -> androidx.compose.material.icons.Icons.Outlined.Code
        com.aicodeassistant.domain.model.CodeLanguage.SHELL -> androidx.compose.material.icons.Icons.Outlined.Terminal
        else -> androidx.compose.material.icons.Icons.Outlined.InsertDriveFile
    }
}
