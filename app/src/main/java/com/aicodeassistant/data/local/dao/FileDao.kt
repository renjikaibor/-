package com.aicodeassistant.data.local.dao

import androidx.room.*
import com.aicodeassistant.data.local.entity.FileEntity
import com.aicodeassistant.data.local.entity.FileType
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(file: FileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(files: List<FileEntity>): List<Long>

    @Update
    suspend fun update(file: FileEntity): Int

    @Delete
    suspend fun delete(file: FileEntity)

    @Query("DELETE FROM files WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM files WHERE parentId = :parentId")
    suspend fun deleteByParentId(parentId: Long?): Int

    @Query("SELECT * FROM files WHERE id = :id")
    suspend fun getById(id: Long): FileEntity?

    @Query("SELECT * FROM files WHERE path = :path")
    suspend fun getByPath(path: String): FileEntity?

    @Query("SELECT * FROM files WHERE parentId = :parentId AND type = :type ORDER BY name ASC")
    fun getChildren(parentId: Long?, type: FileType): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE parentId = :parentId ORDER BY type DESC, name ASC")
    fun getAllChildren(parentId: Long?): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE type = :type ORDER BY path ASC")
    fun getByType(type: FileType): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE isOpen = 1")
    fun getOpenFiles(): Flow<List<FileEntity>>

    @Query("SELECT * FROM files WHERE isDirty = 1")
    fun getDirtyFiles(): Flow<List<FileEntity>>

    @Query("UPDATE files SET isOpen = :isOpen WHERE id = :id")
    suspend fun setOpen(id: Long, isOpen: Boolean)

    @Query("UPDATE files SET isDirty = :isDirty, content = :content, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateContent(id: Long, content: String?, isDirty: Boolean, updatedAt: java.util.Date)

    @Query("UPDATE files SET cursorPosition = :cursor, scrollPosition = :scroll WHERE id = :id")
    suspend fun updateCursorPosition(id: Long, cursor: Int, scroll: Int)

    @Query("UPDATE files SET name = :name, path = :path, updatedAt = :updatedAt WHERE id = :id")
    suspend fun rename(id: Long, name: String, path: String, updatedAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM files WHERE parentId = :parentId")
    suspend fun getChildrenCount(parentId: Long?): Int
}
