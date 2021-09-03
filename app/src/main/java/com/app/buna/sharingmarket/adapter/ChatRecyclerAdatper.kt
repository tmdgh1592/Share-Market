package com.app.buna.sharingmarket.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ChatItemLayoutBinding
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatRecyclerAdatper(val destModel: UserModel) : RecyclerView.Adapter<ChatRecyclerAdatper.ChatViewHolder>() {
    var chatList = ArrayList<ChatModel.Comment>()

    inner class ChatViewHolder(val binding: ChatItemLayoutBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindMyChat(position: Int) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(binding.constraintLayout)
            binding.comment = chatList[position]
            binding.profileImageView.visibility = View.GONE
            binding.nicknameTextView.visibility = View.GONE
            binding.otherTimestampTextView.visibility = View.GONE
            binding.commentTextView.setBackgroundResource(R.drawable.chat_bubble_me)
        }
        fun bindOtherChat(position: Int) {
            binding.comment = chatList[position]
            binding.destModel = destModel
            binding.myTimestampTextView.visibility = View.GONE
            binding.commentTextView.setBackgroundResource(R.drawable.chat_bubble_other)
            // 상대방이 프로필을 설정한 경우에만 불러오기
            if (destModel.profileImageUrl != null) {
                Glide.with(binding.root).load(Uri.parse(destModel.profileImageUrl)).circleCrop()
                    .into(binding.profileImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        if(chatList[position].uid == Firebase.auth.uid) { // 본인이라면
            holder.bindMyChat(position)
        } else { // 상대방이라면
            holder.bindOtherChat(position)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun update(newChatList: ArrayList<ChatModel.Comment>) {
        chatList.clear()
        chatList = newChatList
        notifyDataSetChanged()
    }
}