package com.aicodeassistant.data.local.entity

enum class MessageRole {
    SYSTEM,
    USER,
    ASSISTANT,
    TOOL
}

enum class ToolCallStatus {
    PENDING,
    RUNNING,
    SUCCESS,
    FAILED,
    CANCELLED
}

enum class ToolType {
    BUILTIN,
    INSTALLED,
    CUSTOM
}

enum class PermissionStatus {
    GRANTED,
    DENIED,
    REQUESTED,
    PERMANENTLY_DENIED
}

enum class ModelType {
    BUILTIN,
    CUSTOM
}

enum class FileType {
    FILE,
    DIRECTORY
}

enum class CodeLanguage {
    PYTHON("python", "Python", ".py"),
    JAVASCRIPT("javascript", "JavaScript", ".js"),
    TYPESCRIPT("typescript", "TypeScript", ".ts"),
    HTML("html", "HTML", ".html"),
    CSS("css", "CSS", ".css"),
    JSON("json", "JSON", ".json"),
    MARKDOWN("markdown", "Markdown", ".md"),
    KOTLIN("kotlin", "Kotlin", ".kt"),
    JAVA("java", "Java", ".java"),
    CPP("cpp", "C++", ".cpp"),
    RUST("rust", "Rust", ".rs"),
    GO("go", "Go", ".go"),
    SHELL("shell", "Shell", ".sh"),
    DOCKERFILE("dockerfile", "Dockerfile", "Dockerfile"),
    YAML("yaml", "YAML", ".yaml"),
    XML("xml", "XML", ".xml"),
    SQL("sql", "SQL", ".sql"),
    TEXT("text", "Text", ".txt");

    val id: String
    val displayName: String
    val extension: String

    constructor(id: String, displayName: String, extension: String) {
        this.id = id
        this.displayName = displayName
        this.extension = extension
    }

    companion object {
        fun fromFileName(fileName: String): CodeLanguage {
            val ext = fileName.substringAfterLast(".", "").lowercase()
            return values().firstOrNull { it.extension == ".$ext" } ?: TEXT
        }

        fun fromId(id: String): CodeLanguage {
            return values().firstOrNull { it.id == id } ?: TEXT
        }
    }
}
