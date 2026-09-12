package com.aicodeassistant.ui.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aicodeassistant.domain.model.FileItem
import com.aicodeassistant.domain.repository.FileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CodeViewModel @Inject constructor(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _fileTree = MutableStateFlow<List<FileItem>>(emptyList())
    val fileTree = _fileTree.asStateFlow()

    private val _currentFile = MutableStateFlow<FileItem?>(null)
    val currentFile = _currentFile.asStateFlow()

    private val _showFileTree = MutableStateFlow(true)
    val showFileTree = _showFileTree.asStateFlow()

    private val _showTerminal = MutableStateFlow(false)
    val showTerminal = _showTerminal.asStateFlow()

    private val _terminalOutput = MutableStateFlow("")
    val terminalOutput = _terminalOutput.asStateFlow()

    init {
        loadFileTree()
    }

    private fun loadFileTree() {
        viewModelScope.launch {
            fileRepository.observeAllChildren(null).collect { files ->
                _fileTree.value = files
            }
        }
    }

    fun openFile(file: FileItem) {
        if (file.type == com.aicodeassistant.domain.model.FileType.FILE) {
            _currentFile.value = file
            viewModelScope.launch {
                fileRepository.setFileOpen(file.id, true)
            }
        }
    }

    fun newFile() {
        // TODO: Show dialog for new file name and location
    }

    fun saveFile() {
        _currentFile.value?.let { file ->
            if (file.isDirty) {
                viewModelScope.launch {
                    fileRepository.updateFileContent(file.id, file.content ?: "", false)
                }
            }
        }
    }

    fun runCode() {
        _currentFile.value?.let { file ->
            val language = file.language?.id ?: "text"
            val code = file.content ?: ""
            viewModelScope.launch {
                _terminalOutput.value += "\n$ Running ${file.name}...\n"
                // TODO: Execute code via ToolExecutionService
                _terminalOutput.value += "Code execution not yet implemented\n"
            }
        }
    }

    fun createFile(parentId: Long?, name: String) {
        viewModelScope.launch {
            val file = FileItem.createFile(parentId, name, "")
            fileRepository.createFile(file)
        }
    }

    fun createFolder(parentId: Long?, name: String) {
        viewModelScope.launch {
            val folder = FileItem.createDirectory(parentId, name, "")
            fileRepository.createFile(folder)
        }
    }

    fun renameFile(file: FileItem, newName: String) {
        viewModelScope.launch {
            fileRepository.renameFile(file.id, newName, file.path)
        }
    }

    fun deleteFile(file: FileItem) {
        viewModelScope.launch {
            fileRepository.deleteFile(file.id)
        }
    }

    fun updateFileContent(fileId: Long, content: String) {
        _currentFile.value?.let { file ->
            if (file.id == fileId) {
                _currentFile.value = file.copy(content = content, isDirty = true)
            }
        }
    }

    fun updateCursorPosition(fileId: Long, cursor: Int, scroll: Int) {
        _currentFile.value?.let { file ->
            if (file.id == fileId) {
                _currentFile.value = file.copy(cursorPosition = cursor, scrollPosition = scroll)
            }
        }
    }
}
