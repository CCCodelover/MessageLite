package com.example.messagelite.data.repository

import com.example.messagelite.data.local.MessageDao
import com.example.messagelite.data.local.entities.toDomain
import com.example.messagelite.data.local.entities.toEntity
import com.example.messagelite.data.remote.MessageRemoteDataSource
import com.example.messagelite.domain.model.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessageRepository(
    private val remote: MessageRemoteDataSource,
    private val messageDao: MessageDao
) {

    private val pageSize = 20

    suspend fun refreshMessages(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runCatching {
                val allMessages = mutableListOf<Message>()
                var page = 1
                while (true) {
                    val list = remote.fetchMessages(page = page, pageSize = pageSize)
                    if (list.isEmpty()) break
                    allMessages.addAll(list)
                    if (list.size < pageSize) break
                    page++
                }
                messageDao.clearAll()
                if (allMessages.isNotEmpty()) {
                    messageDao.insertMessages(allMessages.map { it.toEntity() })
                }
            }
        }
    }

    // 分页从本地读取（冷启动也能用）
    suspend fun loadMessages(page: Int): List<Message> {
        return withContext(Dispatchers.IO) {
            val offset = (page - 1) * pageSize
            messageDao.getMessages(limit = pageSize, offset = offset).map { it.toDomain() }
        }
    }

    suspend fun markAsRead(messageId: Long) {
        withContext(Dispatchers.IO) {
            messageDao.updateReadState(messageId, true)
        }
    }

    suspend fun updateRemark(messageId: Long, remark: String) {
        withContext(Dispatchers.IO) {
            messageDao.updateRemark(messageId, remark)
        }
    }

    suspend fun getMessageById(id: Long): Message? {
        return withContext(Dispatchers.IO) {
            messageDao.getMessageById(id)?.toDomain()
        }
    }
}
