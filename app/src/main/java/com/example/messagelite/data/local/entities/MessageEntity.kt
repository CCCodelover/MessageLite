package com.example.messagelite.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.messagelite.domain.model.Message
import com.example.messagelite.domain.model.MessageType

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: Long,
    val senderId: Long,
    val nickname: String,
    val avatarUrl: String?,
    val content: String,
    val type: String,
    val createdAt: Long,
    val isRead: Boolean,
    val remark: String?
)

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = id,
        senderId = senderId,
        nickname = nickname,
        avatarUrl = avatarUrl,
        content = content,
        type = type.name,
        createdAt = createdAt,
        isRead = isRead,
        remark = remark
    )
}

fun MessageEntity.toDomain(): Message {
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
        isRead = isRead,
        remark = remark
    )
}
