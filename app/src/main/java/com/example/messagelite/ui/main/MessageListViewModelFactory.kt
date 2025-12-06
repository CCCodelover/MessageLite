package com.example.messagelite.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.messagelite.data.repository.MessageRepository

class MessageListViewModelFactory(
    private val repository: MessageRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
