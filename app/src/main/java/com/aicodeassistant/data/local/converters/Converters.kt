package com.aicodeassistant.data.local.converters

import androidx.room.TypeConverter
import com.aicodeassistant.data.local.entity.MessageRole
import com.aicodeassistant.data.local.entity.ToolCallStatus
import com.aicodeassistant.data.local.entity.ToolType
import com.aicodeassistant.data.local.entity.PermissionStatus
import com.aicodeassistant.domain.model.ChatMessage
import com.aicodeassistant.domain.model.ToolCall
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*

class Converters {

    @TypeConverter
    fun fromMessageRole(role: MessageRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toMessageRole(role: String?): MessageRole? {
        return role?.let { MessageRole.valueOf(it) }
    }

    @TypeConverter
    fun fromToolCallStatus(status: ToolCallStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toToolCallStatus(status: String?): ToolCallStatus? {
        return status?.let { ToolCallStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromToolType(type: ToolType?): String? {
        return type?.name
    }

    @TypeConverter
    fun toToolType(type: String?): ToolType? {
        return type?.let { ToolType.valueOf(it) }
    }

    @TypeConverter
    fun fromPermissionStatus(status: PermissionStatus?): String? {
        return status?.name
    }

    @TypeConverter
    fun toPermissionStatus(status: String?): PermissionStatus? {
        return status?.let { PermissionStatus.valueOf(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromMap(map: Map<String, String>?): String? {
        return if (map == null || map.isEmpty()) null else Gson().toJson(map)
    }

    @TypeConverter
    fun toMap(json: String?): Map<String, String>? {
        return json?.let {
            val type: Type = object : TypeToken<Map<String, String>>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return if (list == null || list.isEmpty()) null else Gson().toJson(list)
    }

    @TypeConverter
    fun toList(json: String?): List<String>? {
        return json?.let {
            val type: Type = object : TypeToken<List<String>>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromChatMessageList(messages: List<ChatMessage>?): String? {
        return if (messages == null || messages.isEmpty()) null else Gson().toJson(messages)
    }

    @TypeConverter
    fun toChatMessageList(json: String?): List<ChatMessage>? {
        return json?.let {
            val type: Type = object : TypeToken<List<ChatMessage>>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromToolCallList(calls: List<ToolCall>?): String? {
        return if (calls == null || calls.isEmpty()) null else Gson().toJson(calls)
    }

    @TypeConverter
    fun toToolCallList(json: String?): List<ToolCall>? {
        return json?.let {
            val type: Type = object : TypeToken<List<ToolCall>>() {}.type
            Gson().fromJson(it, type)
        }
    }

    @TypeConverter
    fun fromMapAny(map: Map<String, Any>?): String? {
        return if (map == null || map.isEmpty()) null else Gson().toJson(map)
    }

    @TypeConverter
    fun toMapAny(json: String?): Map<String, Any>? {
        return json?.let {
            val type: Type = object : TypeToken<Map<String, Any>>() {}.type
            Gson().fromJson(it, type)
        }
    }
}
