package com.app.buna.sharingmarket.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.databinding.LayoutSelectUserBinding
import com.app.buna.sharingmarket.model.chat.ChatUserModel
import com.app.buna.sharingmarket.viewmodel.SelectUserViewModel

class UserSelectRecyclerAdapter(val userViewModel: SelectUserViewModel) : RecyclerView.Adapter<UserSelectRecyclerAdapter.UserSelectViewHolder>() {
    var userChatModelList: ArrayList<ChatUserModel> = ArrayList()

    inner class UserSelectViewHolder(val binding: LayoutSelectUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.userModel = userChatModelList[position]
            binding.selectUserLayout.setOnClickListener {
                userViewModel.selectedUserPos.postValue(position) // 클릭한 유저의 position으로 값 갱신
            }

            binding.selectUserRadioBtn.isChecked = (position == userViewModel.selectedUserPos.value)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserSelectViewHolder {
        return UserSelectViewHolder(LayoutSelectUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: UserSelectViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return userChatModelList.size
    }

    override fun getItemId(position: Int): Long {
        return userChatModelList[position].hashCode().toLong()
    }

    fun update(newUserChatModelList: ArrayList<ChatUserModel>) {
        userChatModelList = newUserChatModelList
        notifyDataSetChanged()
    }

}