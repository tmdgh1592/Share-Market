package com.app.buna.sharingmarket.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.buna.sharingmarket.CommentType
import com.app.buna.sharingmarket.databinding.ChatDateDividerLayoutBinding
import com.app.buna.sharingmarket.databinding.ChatMeItemLayoutBinding
import com.app.buna.sharingmarket.databinding.ChatOtherItemLayoutBinding
import com.app.buna.sharingmarket.model.items.chat.ChatModel
import com.app.buna.sharingmarket.model.items.chat.ChatUserModel
import com.app.buna.sharingmarket.utils.BaseDiffUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class ChatRecyclerAdatper(val destModel: ChatUserModel) :
    RecyclerView.Adapter<ChatRecyclerAdatper.BaseViewHolder>() {
    var chatList = ArrayList<ChatModel.Comment>()
    private val TYPE_ME = 0
    private val TYPE_OTHER = 1
    private val TYPE_DIVIDER = 2


    abstract class BaseViewHolder(binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(position: Int)
    }

    inner class MyChatViewHolder(val binding: ChatMeItemLayoutBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.comment = chatList[position]

            val timeStamp = chatList[position].usingTimeStamp.split(" ")[1]
            binding.myTimestampTextView.text = timeStamp // 타임스탬프

            // 동시간대에 보낸 마지막 메세지에만 시간 표시
            /*if (position+1 < itemCount) {
                if (chatList[position + 1].uid == Firebase.auth.uid && // 다음 채팅도 내가 보낸 채팅이고
                    timeStamp == chatList[position + 1].usingTimeStamp.split(" ")[1]) { // 현재 타임스탬프와 바로 다음에 보낸 타임스탬프의 시간이 같다면
                    binding.myTimestampTextView.visibility = View.GONE // 현재 채팅의 타임스탬프는 안보이게 하기
                } else { // 그 외) 다음 채팅은 내가 보낸 것이 아니거나, 타임스탬프 시간이 다른 경우
                    binding.myTimestampTextView.visibility = View.VISIBLE // 타임스탬프를 나타냄
                    binding.myTimestampTextView.text = timeStamp // 타임스탬프

                    // 마지막에서 이전 채팅이면서 그 다음 채팅이 내 채팅이 아닌 경우에만 margin값 설정
                    if (!(position == itemCount-2 && chatList[position].uid == chatList[position+1].uid)) {
                        val param = binding.constraintLayout.layoutParams as RecyclerView.LayoutParams
                        param.bottomMargin = 50
                        binding.constraintLayout.layoutParams = param // 타임스탬프가 갱신될 때 다음 채팅과 간격을 두기 위해 margin값을 줌
                    }
                }
            }
            if (position == itemCount-1) {
                binding.myTimestampTextView.visibility = View.VISIBLE // 타임스탬프를 나타냄
                binding.myTimestampTextView.text = timeStamp // 타임스탬프

                val param = binding.constraintLayout.layoutParams as RecyclerView.LayoutParams
                param.bottomMargin = 20
                binding.constraintLayout.layoutParams = param // 마지막 채팅은 EditText와 간격을 두기 위해 margin값을 줌
            }*/
        }
    }

    inner class OtherChatViewHolder(val binding: ChatOtherItemLayoutBinding) :
        BaseViewHolder(binding) {
        override fun bind(position: Int) {
            binding.comment = chatList[position]
            binding.destModel = destModel

            val timeStamp = chatList[position].usingTimeStamp.split(" ")[1]
            if (destModel.profileImageUrl != null) {
                Glide.with(binding.root).load(Uri.parse(destModel.profileImageUrl)).circleCrop()
                    .into(binding.profileImageView)
            }
            binding.otherTimestampTextView.text = timeStamp
            /*// 동시간대에 보낸 마지막 메세지에만 타임스탬프 표시, 동시간대 이전 닉네임, 프로필 안보이게 설정
            if (position+1 < itemCount) {

                if (chatList[position].uid != chatList[position - 1].uid || // 이전에 채팅을 보낸 사람이 다른 사람이라면
                    chatList[position].uid == chatList[position + 1].uid && // 다음 채팅도 같은 사람이 보낸 채팅이고
                    timeStamp == chatList[position + 1].usingTimeStamp.split(" ")[1]) { // 현재 타임스탬프와 바로 다음에 보낸 타임스탬프의 시간이 같다면
                    if (destModel.profileImageUrl != null) {
                        Glide.with(binding.root).load(Uri.parse(destModel.profileImageUrl)).circleCrop()
                            .into(binding.profileImageView)
                    }
                    binding.otherTimestampTextView.visibility = View.GONE // 현재 채팅의 타임스탬프는 안보이게 하기
                } else {
                    binding.profileImageView.visibility = View.GONE // 현재 프로필 안보이게 하기
                    binding.nicknameTextView.visibility = View.GONE // 현재 닉네임 텍스트뷰 안보이게 하기
                    binding.otherTimestampTextView.text =
                        chatList[position].usingTimeStamp.split(" ")[1] // 타임스탬프
                }

                if (position == itemCount-1) { // 처음이나 마지막에 보낸 채팅은 무조건 타임스탬프가 보이게 함
                    // 상대방이 프로필을 설정했다면
                    binding.otherTimestampTextView.text =
                        chatList[position].usingTimeStamp.split(" ")[1] // 타임스탬프
                }
            }
*/
        }
    }

    inner class ChatDividerViewHolder(val binding: ChatDateDividerLayoutBinding) :
        BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val date = chatList[position].usingTimeStamp.split(" ")[0] // ex) -> 2021.09.05
            val year = date.split(".")[0]
            val month = date.split(".")[1]
            val day = date.split(".")[2]

            val weeks = arrayOf("일", "월", "화", "수", "목", "금", "토")
            val calendar = Calendar.getInstance()
            calendar.set(year.toInt(), month.toInt()-1, day.toInt()) // 0~6 (일~토), Month는 1빼줘야 함

            binding.date = "${year}년 ${month}월 ${day}일 ${weeks[calendar.get(Calendar.DAY_OF_WEEK)-1]}요일" // 2021년 09월 06일 월요일
        }
   }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_ME -> MyChatViewHolder(
                ChatMeItemLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_OTHER -> OtherChatViewHolder(
                ChatOtherItemLayoutBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )
            TYPE_DIVIDER -> ChatDividerViewHolder(
                ChatDateDividerLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        when (holder) {
            is MyChatViewHolder -> holder.bind(position)
            is OtherChatViewHolder -> holder.bind(position)
            is ChatDividerViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (chatList[position].commentType == CommentType.DATE_DIVIDER) {
            return TYPE_DIVIDER
        } else if (chatList[position].uid == Firebase.auth.uid) {
            return TYPE_ME
        }
        return TYPE_OTHER

    }

    override fun getItemId(position: Int): Long {
        return chatList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    fun update(newChatList: ArrayList<ChatModel.Comment>) {
        val dateAddednewChatList = ArrayList<ChatModel.Comment>() // 새로운 채팅을 담을 리스트
        var willBeChangedDay = "" // 채팅들의 날짜를 구분하기위한 채팅 날짜 구분 데이터를 저장할 변수

        newChatList.forEachIndexed { index, comment ->
            val date = comment.usingTimeStamp.split(" ")[0] // 채팅을 보낸 날짜. ex) 2021.09.06

            // 날짜 변동에 따라 Date Divider를 사이에 추가
            if (!date.equals(willBeChangedDay)) { // 현재 보고 있는 채팅의 날짜가 이전 채팅의 날짜와 다른 경우
                willBeChangedDay = date // 날짜 갱신
                dateAddednewChatList.add(ChatModel.Comment(timeStamp = newChatList[index].timeStamp, commentType = CommentType.DATE_DIVIDER)) // Date Divider 추가
            }
            // 채팅 추가
            dateAddednewChatList.add(comment)
        }


        val diffUtil = BaseDiffUtil(chatList, dateAddednewChatList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)

        chatList.clear()
        chatList.addAll(dateAddednewChatList)

        diffResult.dispatchUpdatesTo(this)

    }
}
