package com.example.messagelite.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagelite.data.repository.MessageRepository
import com.example.messagelite.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MessageListUiState(
    val messages: List<Message> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isEnd: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1
)

class MessageListViewModel(
    private val repository: MessageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessageListUiState())
    val uiState: StateFlow<MessageListUiState> = _uiState

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, errorMessage = null, currentPage = 1) }
            val result = repository.refreshMessages()
            result.onFailure {
                val firstPageFallback = repository.loadMessages(page = 1)
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        messages = firstPageFallback,
                        isEnd = firstPageFallback.isEmpty(),
                        errorMessage = "加载失败，请下拉重试",
                        currentPage = 1
                    )
                }
                return@launch
            }

            val firstPage = repository.loadMessages(page = 1)
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    messages = firstPage,
                    isEnd = firstPage.isEmpty(),
                    errorMessage = null,
                    currentPage = 1
                )
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoadingMore || state.isEnd) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = state.currentPage + 1
            val newMessages = repository.loadMessages(page = nextPage)
            _uiState.update {
                it.copy(
                    isLoadingMore = false,
                    currentPage = nextPage,
                    messages = it.messages + newMessages,
                    isEnd = newMessages.isEmpty()
                )
            }
        }
    }

    fun onMessageClicked(message: Message) {
        viewModelScope.launch {
            if (!message.isRead) {
                repository.markAsRead(message.id)
                // 本地 state 更新
                _uiState.update {
                    val updated = it.messages.map { msg ->
                        if (msg.id == message.id) msg.copy(isRead = true) else msg
                    }
                    it.copy(messages = updated)
                }
            }
        }
    }
}
