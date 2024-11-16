package com.mayad.instagram.features.navigation.messages.recent.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mayad.instagram.R
import com.mayad.instagram.android.extension.formatCount
import com.mayad.instagram.android.extension.getReadableTimeDifference
import com.mayad.instagram.android.extension.loadImage
import com.mayad.instagram.android.extension.show
import com.mayad.instagram.core.common.domain.User
import com.mayad.instagram.core.domain.interactors.AuthenticationUtils
import com.mayad.instagram.databinding.ItemRecentMessageBinding
import com.mayad.instagram.features.navigation.messages.recent.data.MessageType
import com.mayad.instagram.features.navigation.messages.recent.data.MessageType.*
import com.mayad.instagram.features.navigation.messages.recent.domain.RecentMessage
import javax.inject.Inject

class RecentMessagesAdapter @Inject constructor(
    private val context: Context,
    var authenticationUtils: AuthenticationUtils
) : RecyclerView.Adapter<RecentMessagesAdapter.RecentMessageVH>() {

    private var onItemClick: ((User) -> Unit)? = null
    private val currentUserId = authenticationUtils.getCurrentUserId()!!

    fun setonItemClickListener(listener: (User) -> Unit) {
        onItemClick = listener
    }

    var list: List<RecentMessage>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class RecentMessageVH(val binding: ItemRecentMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<RecentMessage>() {
        override fun areItemsTheSame(oldItem: RecentMessage, newItem: RecentMessage) =
            oldItem.conversationId == newItem.conversationId

        override fun areContentsTheSame(oldItem: RecentMessage, newItem: RecentMessage) =
            oldItem == newItem
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        RecentMessageVH(
            ItemRecentMessageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: RecentMessageVH, position: Int) {
        val recentMessage = list[position]
        with(recentMessage) {
            holder.binding.apply {
                val otherUser = users?.first { user -> user.id != currentUserId }
                otherUser?.let { user ->
                    imageView.loadImage(user.profilePicture)
                    tvUsername.text = user.username
                    ivActiveNow.show(user.isOnline)
                }
                unreadMessagesContainer.show(unreadMessagesCount > 0)
                tvUnreadMessagesCount.text = unreadMessagesCount.formatCount(context)
                tvMessage.text = when (messageType) {
                    Image -> context.getString(R.string.type_image)
                    Video -> context.getString(R.string.type_video)
                    Audio -> context.getString(R.string.type_audio)
                    Text -> lastMessage
                }
                tvTime.text = lastMessageSendTime.getReadableTimeDifference(context)
                holder.itemView.setOnClickListener { otherUser?.let { it1 -> onItemClick?.invoke(it1) } }
            }
        }
    }
}