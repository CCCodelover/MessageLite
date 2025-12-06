package com.example.messagelite.domain.model

data class Message(
    val id: Long,
    val senderId: Long,
    val nickname: String,
    val avatarUrl: String?,
    val content: String,
    val type: MessageType,
    val createdAt: Long,      // 时间戳（ms）
    val isRead: Boolean,
    val remark: String? = null
)
