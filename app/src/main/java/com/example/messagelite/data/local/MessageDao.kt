package com.example.messagelite.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messagelite.data.local.entities.MessageEntity

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessages(limit: Int, offset: Int): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("UPDATE messages SET isRead = :isRead WHERE id = :messageId")
    suspend fun updateReadState(messageId: Long, isRead: Boolean)

    @Query("UPDATE messages SET remark = :remark WHERE id = :messageId")
    suspend fun updateRemark(messageId: Long, remark: String)

    @Query("SELECT * FROM messages WHERE id = :id LIMIT 1")
    suspend fun getMessageById(id: Long): MessageEntity?
}
