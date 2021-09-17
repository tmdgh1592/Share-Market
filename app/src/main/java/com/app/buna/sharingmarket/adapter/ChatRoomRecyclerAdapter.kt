package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.LayoutChatRoomBinding
import com.app.buna.sharingmarket.model.chat.ChatRoomModel
import com.app.buna.sharingmarket.model.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.ChatTimeStampUtil
import com.app.buna.sharingmarket.viewmodel.ChatRoomsViewModel

class ChatRoomRecyclerAdapter(val viewModel: ChatRoomsViewModel) :
    RecyclerView.Adapter<ChatRoomRecyclerAdapter.ChatRoomViewHolder>() {
    var chatRoomRoomList: ArrayList<ChatRoomModel> = ArrayList()
    var destUserModelList: ArrayList<ChatUserModel> = ArrayList()

    inner class ChatRoomViewHolder(val binding: LayoutChatRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.destUserModel = destUserModelList[position] // 상대방 정보 데이터 바인딩
            binding.chatModel = chatRoomRoomList[position] // 상대방과 한 채팅 모델 바인딩
            binding.viewModel = viewModel // ChatRoomViewModel 바인딩
            binding.timestampTextView.text = ChatTimeStampUtil.getStamp(chatRoomRoomList[position].lastTimestamp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val binding = LayoutChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatRoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return chatRoomRoomList.size
    }

    override fun getItemId(position: Int): Long {
        return chatRoomRoomList[position].hashCode().toLong()
    }


    fun update(newChatRoomRoomList: List<ChatRoomModel>, newDestUserModelList: List<ChatUserModel>) {
        this.chatRoomRoomList.clear()
        this.destUserModelList.clear()
        this.chatRoomRoomList.addAll(newChatRoomRoomList)
        this.destUserModelList.addAll(newDestUserModelList)

        notifyDataSetChanged()
    }
}