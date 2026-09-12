package com.aicodeassistant.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    fun getAppFilesDir(context: Context): File {
        return context.getExternalFilesDir(null) ?: context.filesDir
    }

    fun getAppCacheDir(context: Context): File {
        return context.getExternalCacheDir() ?: context.cacheDir
    }

    fun getCodeDirectory(context: Context): File {
        val dir = File(getAppFilesDir(context), "code")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getProjectsDirectory(context: Context): File {
        val dir = File(getCodeDirectory(context), "projects")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timeStamp"
        val storageDir = getAppCacheDir(context)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    fun createTempFile(context: Context, prefix: String, extension: String): File {
        val storageDir = getAppCacheDir(context)
        return File.createTempFile(prefix, extension, storageDir)
    }

    fun copyFile(src: File, dest: File): Boolean {
        return try {
            dest.parentFile?.mkdirs()
            FileInputStream(src).use { input ->
                FileOutputStream(dest).use { output ->
                    input.channel.transferTo(0, input.channel.size(), output.channel)
                }
            }
            true
        } catch (e: Exception) {
            Log.e("FileUtils", "Copy failed", e)
            false
        }
    }

    fun deleteRecursively(file: File): Boolean {
        return if (file.isDirectory) {
            file.listFiles()?.all { deleteRecursively(it) } == true && file.delete()
        } else {
            file.delete()
        }
    }

    fun getFileExtension(fileName: String): String {
        return fileName.substringAfterLast(".", "").lowercase()
    }

    fun getMimeType(fileName: String): String {
        return when (getFileExtension(fileName)) {
            "py" -> "text/x-python"
            "js" -> "application/javascript"
            "ts" -> "application/typescript"
            "html", "htm" -> "text/html"
            "css" -> "text/css"
            "json" -> "application/json"
            "md" -> "text/markdown"
            "kt" -> "text/x-kotlin"
            "java" -> "text/x-java"
            "cpp", "cc", "cxx", "hpp", "h" -> "text/x-c++src"
            "rs" -> "text/x-rust"
            "go" -> "text/x-go"
            "sh" -> "application/x-shellscript"
            "yaml", "yml" -> "application/yaml"
            "xml" -> "application/xml"
            "sql" -> "application/sql"
            "txt" -> "text/plain"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "svg" -> "image/svg+xml"
            else -> "application/octet-stream"
        }
    }

    fun formatFileSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f KB", size / 1024.0)
            size < 1024 * 1024 * 1024 -> String.format(Locale.getDefault(), "%.1f MB", size / (1024.0 * 1024))
            else -> String.format(Locale.getDefault(), "%.1f GB", size / (1024.0 * 1024 * 1024))
        }
    }

    fun isValidFileName(fileName: String): Boolean {
        return fileName.isNotBlank() && !fileName.contains("/") && !fileName.contains("\\") && fileName != "." && fileName != ".."
    }

    fun sanitizeFileName(fileName: String): String {
        return fileName.replace(Regex("[/\\\\:*?\"<>|]"), "_").trim()
    }
}
