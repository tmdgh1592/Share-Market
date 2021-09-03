package com.app.buna.sharingmarket.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.R
import com.app.buna.sharingmarket.databinding.ChatMeItemLayoutBinding
import com.app.buna.sharingmarket.databinding.ChatOtherItemLayoutBinding
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.UserModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatRecyclerAdatper(val destModel: UserModel) : RecyclerView.Adapter<ChatRecyclerAdatper.BaseViewHolder>() {
    var chatList = ArrayList<ChatModel.Comment>()
    val TYPE_ME = 0
    val TYPE_OTHER = 1

    abstract class BaseViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(position: Int)
    }

    inner class MyChatViewHolder(val binding: ChatMeItemLayoutBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.comment = chatList[position]
            binding.commentTextView.setBackgroundResource(R.drawable.chat_bubble_me)
        }
    }

    inner class OtherChatViewHolder(val binding: ChatOtherItemLayoutBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.comment = chatList[position]
            binding.destModel = destModel
            binding.commentTextView.setBackgroundResource(R.drawable.chat_bubble_other)
            // 상대방이 프로필을 설정한 경우에만 불러오기
            if (destModel.profileImageUrl != null) {
                Glide.with(binding.root).load(Uri.parse(destModel.profileImageUrl)).circleCrop()
                    .into(binding.profileImageView)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            TYPE_ME -> MyChatViewHolder(ChatMeItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_OTHER -> OtherChatViewHolder(ChatOtherItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when(holder) {
            is MyChatViewHolder -> holder.bind(position)
            is OtherChatViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].uid == Firebase.auth.uid) return TYPE_ME
        return TYPE_OTHER
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