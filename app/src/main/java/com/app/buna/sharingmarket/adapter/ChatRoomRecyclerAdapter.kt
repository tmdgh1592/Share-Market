package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.ChatRoomLayoutBinding
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.ChatTimeStampUtil
import com.app.buna.sharingmarket.viewmodel.ChatRoomsViewModel

class ChatRoomRecyclerAdapter(val viewModel: ChatRoomsViewModel) :
    RecyclerView.Adapter<ChatRoomRecyclerAdapter.ChatRoomViewHolder>() {
    var chatRoomList: ArrayList<ChatModel> = ArrayList()
    var destUserModelList: ArrayList<ChatUserModel> = ArrayList()

    inner class ChatRoomViewHolder(val binding: ChatRoomLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.destUserModel = destUserModelList[position] // 상대방 정보 데이터 바인딩
            binding.chatModel = chatRoomList[position] // 상대방과 한 채팅 모델 바인딩
            binding.viewModel = viewModel // ChatRoomViewModel 바인딩
            binding.timestampTextView.text = ChatTimeStampUtil.getStamp(chatRoomList[position].lastTimestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding =
            ChatRoomLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        if (chatRoomList != null) return chatRoomList.size
        return 0
    }

    fun update(chatRoomList: ArrayList<ChatModel>, destUserModelList: ArrayList<ChatUserModel>) {
        this.chatRoomList = chatRoomList
        this.destUserModelList = destUserModelList
        notifyDataSetChanged()
    }
}