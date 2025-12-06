package com.example.messagelite.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.messagelite.R
import com.example.messagelite.domain.model.Message
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val onItemClick: (Message) -> Unit
) : ListAdapter<Message, MessageAdapter.VH>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return VH(v, onItemClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class VH(itemView: View, private val onItemClick: (Message) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val avatar = itemView.findViewById<ImageView>(R.id.avatar)
        private val nickname = itemView.findViewById<TextView>(R.id.nickname)
        private val summary = itemView.findViewById<TextView>(R.id.summary)
        private val time = itemView.findViewById<TextView>(R.id.time)
        private val unreadDot = itemView.findViewById<View>(R.id.unreadDot)
        private val fmt = SimpleDateFormat("HH:mm", Locale.getDefault())

        fun bind(m: Message) {
            nickname.text = m.nickname
            summary.text = if ((m.remark ?: "").isNotBlank()) m.remark else m.content
            time.text = fmt.format(Date(m.createdAt))
            unreadDot.visibility = if (m.isRead) View.INVISIBLE else View.VISIBLE
            avatar.setImageResource(R.mipmap.ic_launcher)
            itemView.setOnClickListener { onItemClick(m) }
        }
    }
}
