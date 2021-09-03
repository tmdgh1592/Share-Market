package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.ChatRoomLayoutBinding
import com.app.buna.sharingmarket.model.items.chat.ChatRoom

class ChatRoomRecyclerAdapter : RecyclerView.Adapter<ChatRoomRecyclerAdapter.ChatRoomViewHolder>(){
    var chatRoomList: ArrayList<ChatRoom> = ArrayList()

    inner class ChatRoomViewHolder(val binding: ChatRoomLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.model = chatRoomList[position]
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = ChatRoomLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        if(chatRoomList != null) return chatRoomList.size
        return 0
    }

    fun update(chatRoomList: ArrayList<ChatRoom>) {
        this.chatRoomList = chatRoomList
        notifyDataSetChanged()
    }
}