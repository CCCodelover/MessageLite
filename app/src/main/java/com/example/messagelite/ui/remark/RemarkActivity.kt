package com.example.messagelite.ui.remark

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.messagelite.R
import com.example.messagelite.data.local.AppDatabase
import com.example.messagelite.data.remote.MessageRemoteDataSource
import com.example.messagelite.data.repository.MessageRepository

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class RemarkActivity : AppCompatActivity() {

    private lateinit var viewModel: RemarkViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remark)

        val messageId = intent.getLongExtra("message_id", -1)
        if (messageId == -1L) {
            finish()
            return
        }

        val db = AppDatabase.getInstance(this)
        val repository = MessageRepository(
            remote = MessageRemoteDataSource(this),
            messageDao = db.messageDao()
        )

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RemarkViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return RemarkViewModel(repository, messageId) as T
                }
                throw IllegalArgumentException("Unknown ViewModel")
            }
        }
        viewModel = ViewModelProvider(this, factory)[RemarkViewModel::class.java]

        val nicknameText = findViewById<TextView>(R.id.nickname)
        val remarkEdit = findViewById<EditText>(R.id.remarkEdit)
        val saveButton = findViewById<Button>(R.id.saveButton)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    nicknameText.text = state.message?.nickname ?: ""
                    if (remarkEdit.text.toString() != state.remarkText) {
                        remarkEdit.setText(state.remarkText)
                        remarkEdit.setSelection(state.remarkText.length)
                    }
                    saveButton.isEnabled = !state.isSaving
                }
            }
        }

        remarkEdit.addTextChangedListener {
            viewModel.onRemarkChanged(it.toString())
        }

        saveButton.setOnClickListener {
            viewModel.save()
            finish() // 保存后直接返回列表
        }
    }
}
