package com.example.messagelite.data.remote

import android.content.Context
import com.example.messagelite.domain.model.Message
import com.example.messagelite.domain.model.MessageType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlin.math.min

class MessageRemoteDataSource(private val context: Context) {

    private val allMessages: List<Message> by lazy {
        loadMessagesFromAssets()
    }

    // 模拟分页接口
    suspend fun fetchMessages(page: Int, pageSize: Int): List<Message> {
        delay(800) // 模拟网络延时
        val fromIndex = (page - 1) * pageSize
        if (fromIndex >= allMessages.size) return emptyList()
        val toIndex = min(allMessages.size, fromIndex + pageSize)
        return allMessages.subList(fromIndex, toIndex)
    }

    private fun loadMessagesFromAssets(): List<Message> {
        val inputStream = context.assets.open("messages.json")
        val json = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val type = object : TypeToken<List<MessageJson>>() {}.type
        val jsonList: List<MessageJson> = gson.fromJson(json, type)
        return jsonList.map { it.toDomain() }
    }
}

// 用于解析 JSON 的数据类（可以放在此文件里）
data class MessageJson(
    val id: Long,
    val senderId: Long,
    val nickname: String,
    val avatarUrl: String?,
    val content: String,
    val type: String,
    val createdAt: Long,
    val isRead: Boolean
)

fun MessageJson.toDomain(): Message {
    return Message(
        id = id,
        senderId = senderId,
        nickname = nickname,
        avatarUrl = avatarUrl,
        content = content,
        type = try {
            MessageType.valueOf(type)
        } catch (e: Exception) {
            MessageType.SYSTEM_TEXT
        },
        createdAt = createdAt,
        isRead = isRead
    )
}
