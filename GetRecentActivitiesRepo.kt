package com.mayad.instagram.features.navigation.profile.recentActivity.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mayad.instagram.android.base.Constants
import com.mayad.instagram.android.io.domain.IUserLocalDS
import com.mayad.instagram.core.domain.interactors.FireStoreUtils
import com.mayad.instagram.features.auth.common.domain.IGetUserRepo
import com.mayad.instagram.features.content.comment.domain.ICommentsRepo
import com.mayad.instagram.features.content.post.domain.IPostsRepo
import com.mayad.instagram.features.content.reel.domain.IReelsRepo
import com.mayad.instagram.features.navigation.home.notifications.data.CommentNotificationsRequest
import com.mayad.instagram.features.navigation.home.notifications.data.CommentType
import com.mayad.instagram.features.navigation.profile.recentActivity.domain.IGetRecentActivitiesRepo
import com.mayad.instagram.features.navigation.profile.recentActivity.domain.RecentActivity
import javax.inject.Inject

class GetRecentActivitiesRepo @Inject constructor(
    firestore: FirebaseFirestore,
    private val localDS: IUserLocalDS,
    private val getUserRepo: IGetUserRepo,
    private val postsRepo: IPostsRepo,
    private val reelsRepo: IReelsRepo,
    private val commentsRepo: ICommentsRepo,
) : FireStoreUtils<RecentActivityDTO>(firestore), IGetRecentActivitiesRepo {
    override val collectionName = Constants.RECENT_ACTIVITY_COLLECTION

    override suspend fun getRecentActivity(): List<RecentActivity> {
        val userId = localDS.getUserId()
        val dtos = getAllItems(
            baseCollection = Constants.USERS_COLLECTION,
            documentId = userId,
            nestedCollection = collectionName
        )
        val list = mutableListOf<RecentActivity>()
        dtos.forEach {
            val domain = RecentActivityMapper.mapToDomain(it)

            when (ActivityType.getType(it.activityType)) {
                ActivityType.LIKED_POST -> domain.post = postsRepo.getPostById(it.contentId)
                ActivityType.LIKED_REEL -> domain.reel = reelsRepo.getReelById(it.contentId)
                ActivityType.LIKED_POST_COMMENT -> {
                    domain.comment = commentsRepo.getCommentById(
                        CommentNotificationsRequest(it.contentId, it.commentId, CommentType.POST)
                    )
                    domain.post = postsRepo.getPostById(it.contentId)
                }

                ActivityType.LIKED_REEL_COMMENT -> {
                    domain.comment = commentsRepo.getCommentById(
                        CommentNotificationsRequest(it.contentId, it.commentId, CommentType.REEL)
                    )
                    domain.reel = reelsRepo.getReelById(it.contentId)
                }

                ActivityType.COMMENTED_POST -> {
                    domain.comment = commentsRepo.getCommentById(
                        CommentNotificationsRequest(it.contentId, it.commentId, CommentType.POST)
                    )
                    domain.post = postsRepo.getPostById(it.contentId)
                }

                ActivityType.COMMENTED_REEL -> {domain.comment = commentsRepo.getCommentById(
                    CommentNotificationsRequest(it.contentId, it.commentId, CommentType.REEL)
                )
                    domain.reel = reelsRepo.getReelById(it.contentId)
                }
                ActivityType.FOLLOWED_USER -> domain.followingUser =
                    getUserRepo.getUser(it.otherUserId)

                ActivityType.UN_FOLLOWED_USER -> domain.followingUser =
                    getUserRepo.getUser(it.otherUserId)

                ActivityType.BLOCKED_USER -> domain.followingUser =
                    getUserRepo.getUser(it.otherUserId)

                ActivityType.REPORTED_USER -> domain.followingUser =
                    getUserRepo.getUser(it.otherUserId)

                ActivityType.UN_BLOCK_USER -> domain.followingUser =
                    getUserRepo.getUser(it.otherUserId)
            }
            list.add(domain)
        }
        return list
    }
}