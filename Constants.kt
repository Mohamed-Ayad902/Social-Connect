package com.mayad.instagram.android.base

object Constants {

    // Shared Preferences
    const val SHARED_PREFERENCE_NAME: String = "SHARED_PREFERENCE_NAME"
    const val KEY_IS_FIRST_TIME: String = "KEY_IS_FIRST_TIME"
    const val LANGUAGE: String = "LANGUAGE"
    const val DARK_MODE: String = "DARK_MODE"

    // Extra Keys
    const val STORIES_EXTRA = "stories"
    const val INTENT_ACTIVITY_BUNDLE: String = "INTENT_ACTIVITY_BUNDLE"
    const val RE_LOGIN = "RE_LOGIN"

    // Fragments
    const val CONTENT_FRAGMENT = "CONTENT_FRAGMENT"
    const val STORY_FRAGMENT = 0
    const val POST_FRAGMENT = 1
    const val REEL_FRAGMENT = 2

    // Camera
    const val CAMERA_VIDEO_MAX_DURATION = 60

    // Firestore Collections
    const val USERS_COLLECTION: String = "Users"
    const val SAVED_ITEMS_COLLECTION: String = "SavedItems"
    const val CONVERSATIONS_COLLECTION: String = "Conversations"
    const val MESSAGES_COLLECTION: String = "Messages"
    const val VIEWS_COLLECTION: String = "Views"
    const val HIGHLIGHTS_COLLECTION: String = "Highlights"
    const val POSTS_COLLECTION: String = "Posts"
    const val REELS_COLLECTION: String = "Reels"
    const val RECENT_ACTIVITY_COLLECTION: String = "RecentActivity"
    const val FOLLOWINGS_COLLECTION: String = "Followings"
    const val LIKES_COLLECTION: String = "Likes"
    const val COMMENTS_COLLECTION: String = "Comments"
    const val FOLLOWERS_COLLECTION: String = "Followers"
    const val STORIES_COLLECTION: String = "Stories"
    const val NOTIFICATIONS_COLLECTION: String = "Notifications"
    const val USERS_I_BLOCKED_COLLECTION: String = "UsersIBlocked"
    const val USERS_BLOCKED_ME_COLLECTION: String = "UsersBlockedMe"

    // Firestore Fields
    const val FIELD_PARTICIPANTS: String = "participants"
    const val FIELD_LAST_MESSAGES_SEND_TIME: String = "lastMessageSendTime"
    const val FIELD_LAST_MESSAGES_SEEN_TIME: String = "lastMessageSeenTime"
    const val FIELD_LAST_MESSAGES: String = "lastMessage"
    const val FIELD_MESSAGE_TYPE: String = "messageType"
    const val FIELD_LAST_MESSAGES_CONVERSATION: String = "lastMessageSendTime"
    const val FIELD_MESSAGE_SEND_TIME: String = "messageSendTime"
    const val FIELD_ACTIVITY_TYPE: String = "activityType"
    const val FIELD_BLOCKED_USER_ID = "blockedUserId"
    const val FIELD_BLOCKER_USER_ID = "blockerId"
    const val FIELD_ID = "id"
    const val FIELD_CONTENT_ID = "contentId"
    const val FIELD_USER_ID = "userId"
    const val FIELD_TOKEN = "token"
    const val FIELD_SEEN = "seen"
    const val FIELD_MESSAGE_SEEN_TIME = "messageSeenTime"
    const val FIELD_SENDER_ID = "senderId"
    const val FIELD_RECEIVER_ID = "receiverId"
    const val FIELD_SEEN_COUNT = "seenCount"
    const val FIELD_HIGHLIGHT_REFERENCE = "highlightReference"
    const val FIELD_EXPIRE_DATE = "expireDate"
    const val FIELD_POSTS_COUNT = "postsCount"
    const val FIELD_FOLLOWERS_COUNT = "followersCount"
    const val FIELD_FOLLOWINGS_COUNT = "followingsCount"
    const val FIELD_LIKES_COUNT = "likesCount"
    const val FIELD_SEARCH_KEYS = "searchKeys"
    const val FIELD_STORIES_COUNT = "storiesCount"
    const val FIELD_COMMENTS_COUNT = "commentsCount"
    const val FIELD_TIME_STAMP = "timestamp"
    const val FIELD_LAST_ONLINE_TIME_STAMP = "lastOnlineTimestamp"
    const val FIELD_IS_ONLINE = "online"

    // Storage Folders
    const val POSTS_FOLDER: String = "Posts"
    const val USERS_FOLDER: String = "Users"
    const val PROFILE_PIC_FOLDER: String = "ProfilePic"
    const val POST_CONTENT_FOLDER: String = "Content"
    const val PROFILE_PICTURE: String = "ProfilePicture"

    // User Data
    const val USERNAME = "username"

    // Bundles
    const val POSTS_BUNDLE = "posts"
    const val REELS_BUNDLE = "reels"
    const val SELECTED_POST_BUNDLE = "selectedPost"
    const val SELECTED_REEL_BUNDLE = "selectedReel"
}