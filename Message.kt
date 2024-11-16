package com.mayad.instagram.features.navigation.messages.chat.domain

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.mayad.instagram.core.common.domain.Media
import com.mayad.instagram.features.navigation.messages.recent.data.MessageType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String = "",
    val senderId: String = "",
    val message: String = "",
    val messageType: MessageType = MessageType.Text,
    val messageSendTime: Timestamp = Timestamp.now(),
    val messageSeenTime: Timestamp? = null,
    val media: Media? = null,
    val audioPath: String? = "",
    val audioDuration: Long = 0,
    var isPlaying: Boolean = false
) : Parcelable