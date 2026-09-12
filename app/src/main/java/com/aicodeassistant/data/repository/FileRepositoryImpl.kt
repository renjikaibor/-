package com.aicodeassistant.data.repository

import com.aicodeassistant.data.local.AppDatabase
import com.aicodeassistant.data.local.dao.FileDao
import com.aicodeassistant.data.local.entity.FileEntity
import com.aicodeassistant.data.local.entity.FileType
import com.aicodeassistant.data.local.entity.CodeLanguage
import com.aicodeassistant.domain.model.FileItem
import com.aicodeassistant.domain.model.FileType as DomainFileType
import com.aicodeassistant.domain.repository.FileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : FileRepository {

    private val dao: FileDao = database.fileDao()

    private fun FileEntity.toDomain(): FileItem {
        return FileItem(
            id = id,
            parentId = parentId,
            name = name,
            path = path,
            type = when (type) {
                FileType.FILE -> DomainFileType.FILE
                else -> DomainFileType.DIRECTORY
            },
            language = language?.let { CodeLanguage.fromId(it) },
            content = content,
            size = size,
            isOpen = isOpen,
            isDirty = isDirty,
            cursorPosition = cursorPosition,
            scrollPosition = scrollPosition,
            createdAt = createdAt,
            updatedAt = updatedAt,
            metadata = com.google.gson.Gson().fromJson(metadata ?: "{}", Map::class.java) ?: emptyMap()
        )
    }

    private fun FileItem.toEntity(): FileEntity {
        return FileEntity(
            id = id,
            parentId = parentId,
            name = name,
            path = path,
            type = when (type) {
                DomainFileType.FILE -> FileType.FILE
                else -> FileType.DIRECTORY
            },
            language = language?.id,
            content = content,
            size = size,
            isOpen = isOpen,
            isDirty = isDirty,
            cursorPosition = cursorPosition,
            scrollPosition = scrollPosition,
            createdAt = createdAt,
            updatedAt = updatedAt,
            metadata = com.google.gson.Gson().toJson(metadata)
        )
    }

    override suspend fun createFile(file: FileItem): FileItem {
        val entity = file.toEntity()
        val id = dao.insert(entity)
        return file.copy(id = id)
    }

    override suspend fun createFiles(files: List<FileItem>): List<FileItem> {
        val entities = files.map { it.toEntity() }
        val ids = dao.insertAll(entities)
        return files.mapIndexed { index, f -> f.copy(id = ids[index]) }
    }

    override suspend fun updateFile(file: FileItem): FileItem {
        val entity = file.toEntity()
        dao.update(entity)
        return file
    }

    override suspend fun deleteFile(id: Long): Boolean {
        return dao.deleteById(id) > 0
    }

    override suspend fun deleteFilesByParent(parentId: Long?): Int {
        return dao.deleteByParentId(parentId)
    }

    override suspend fun getFile(id: Long): FileItem? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun getFileByPath(path: String): FileItem? {
        return dao.getByPath(path)?.toDomain()
    }

    override fun observeChildren(parentId: Long?, type: DomainFileType): Flow<List<FileItem>> {
        val entityType = when (type) {
            DomainFileType.FILE -> FileType.FILE
            else -> FileType.DIRECTORY
        }
        return dao.getChildren(parentId, entityType).map { it.map { it.toDomain() } }
    }

    override fun observeAllChildren(parentId: Long?): Flow<List<FileItem>> {
        return dao.getAllChildren(parentId).map { it.map { it.toDomain() } }
    }

    override fun observeFilesByType(type: DomainFileType): Flow<List<FileItem>> {
        val entityType = when (type) {
            DomainFileType.FILE -> FileType.FILE
            else -> FileType.DIRECTORY
        }
        return dao.getByType(entityType).map { it.map { it.toDomain() } }
    }

    override fun observeOpenFiles(): Flow<List<FileItem>> {
        return dao.getOpenFiles().map { it.map { it.toDomain() } }
    }

    override fun observeDirtyFiles(): Flow<List<FileItem>> {
        return dao.getDirtyFiles().map { it.map { it.toDomain() } }
    }

    override suspend fun setFileOpen(id: Long, isOpen: Boolean): Boolean {
        return dao.setOpen(id, isOpen) > 0
    }

    override suspend fun updateFileContent(id: Long, content: String?, isDirty: Boolean): Boolean {
        return dao.updateContent(id, content, isDirty, java.util.Date()) > 0
    }

    override suspend fun updateCursorPosition(id: Long, cursor: Int, scroll: Int): Boolean {
        return dao.updateCursorPosition(id, cursor, scroll) > 0
    }

    override suspend fun renameFile(id: Long, name: String, path: String): Boolean {
        return dao.rename(id, name, path, java.util.Date()) > 0
    }

    override suspend fun getChildrenCount(parentId: Long?): Int {
        return dao.getChildrenCount(parentId)
    }
}
