package com.example.messagelite.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.example.messagelite.domain.model.Message

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean =
        oldItem == newItem
}
