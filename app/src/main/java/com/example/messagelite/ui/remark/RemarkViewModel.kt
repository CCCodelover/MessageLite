package com.example.messagelite.ui.remark

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messagelite.data.repository.MessageRepository
import com.example.messagelite.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RemarkUiState(
    val message: Message? = null,
    val remarkText: String = "",
    val isSaving: Boolean = false
)

class RemarkViewModel(
    private val repository: MessageRepository,
    private val messageId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(RemarkUiState())
    val uiState: StateFlow<RemarkUiState> = _uiState

    init {
        viewModelScope.launch {
            val msg = repository.getMessageById(messageId)
            _uiState.value = RemarkUiState(
                message = msg,
                remarkText = msg?.remark ?: ""
            )
        }
    }

    fun onRemarkChanged(text: String) {
        _uiState.update { it.copy(remarkText = text) }
    }

    fun save() {
        val msg = _uiState.value.message ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            repository.updateRemark(msg.id, _uiState.value.remarkText)
            _uiState.update { it.copy(isSaving = false) }
        }
    }
}
