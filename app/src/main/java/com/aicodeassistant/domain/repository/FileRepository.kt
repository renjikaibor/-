package com.aicodeassistant.domain.repository

import com.aicodeassistant.domain.model.FileItem
import com.aicodeassistant.domain.model.FileType
import kotlinx.coroutines.flow.Flow

interface FileRepository {
    suspend fun createFile(file: FileItem): FileItem
    suspend fun createFiles(files: List<FileItem>): List<FileItem>
    suspend fun updateFile(file: FileItem): FileItem
    suspend fun deleteFile(id: Long): Boolean
    suspend fun deleteFilesByParent(parentId: Long?): Int
    suspend fun getFile(id: Long): FileItem?
    suspend fun getFileByPath(path: String): FileItem?
    fun observeChildren(parentId: Long?, type: FileType): Flow<List<FileItem>>
    fun observeAllChildren(parentId: Long?): Flow<List<FileItem>>
    fun observeFilesByType(type: FileType): Flow<List<FileItem>>
    fun observeOpenFiles(): Flow<List<FileItem>>
    fun observeDirtyFiles(): Flow<List<FileItem>>
    suspend fun setFileOpen(id: Long, isOpen: Boolean): Boolean
    suspend fun updateFileContent(id: Long, content: String?, isDirty: Boolean): Boolean
    suspend fun updateCursorPosition(id: Long, cursor: Int, scroll: Int): Boolean
    suspend fun renameFile(id: Long, name: String, path: String): Boolean
    suspend fun getChildrenCount(parentId: Long?): Int
}
