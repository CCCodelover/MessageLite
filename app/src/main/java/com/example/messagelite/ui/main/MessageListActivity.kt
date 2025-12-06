package com.example.messagelite.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.messagelite.R
import com.example.messagelite.data.local.AppDatabase
import com.example.messagelite.data.remote.MessageRemoteDataSource
import com.example.messagelite.data.repository.MessageRepository
import com.example.messagelite.ui.remark.RemarkActivity
import com.example.messagelite.domain.model.Message
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageListActivity : AppCompatActivity() {

    private lateinit var viewModel: MessageListViewModel
    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val emptyView = findViewById<LinearLayout>(R.id.emptyView)
        val emptyText = findViewById<TextView>(R.id.emptyText)

        val db = AppDatabase.getInstance(this)
        val repository = MessageRepository(
            remote = MessageRemoteDataSource(this),
            messageDao = db.messageDao()
        )
        viewModel = ViewModelProvider(this, MessageListViewModelFactory(repository))[MessageListViewModel::class.java]

        adapter = MessageAdapter { message: Message ->
            viewModel.onMessageClicked(message)
            val intent = Intent(this, RemarkActivity::class.java)
            intent.putExtra("message_id", message.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val lm = rv.layoutManager as LinearLayoutManager
                val last = lm.findLastVisibleItemPosition()
                if (adapter.itemCount > 0 && last >= adapter.itemCount - 5) {
                    viewModel.loadMore()
                }
            }
        })

        swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.submitList(state.messages)
                swipeRefresh.isRefreshing = state.isRefreshing
                emptyView.visibility = if (state.messages.isEmpty()) View.VISIBLE else View.GONE
                emptyText.text = state.errorMessage ?: "暂无消息"
            }
        }
    }
}