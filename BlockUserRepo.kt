package com.mayad.instagram.features.navigation.profile.block.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.mayad.instagram.android.base.Constants.FOLLOWINGS_COLLECTION
import com.mayad.instagram.android.base.Constants.USERS_BLOCKED_ME_COLLECTION
import com.mayad.instagram.android.base.Constants.USERS_COLLECTION
import com.mayad.instagram.android.base.Constants.USERS_I_BLOCKED_COLLECTION
import com.mayad.instagram.android.io.domain.IUserLocalDS
import com.mayad.instagram.core.domain.interactors.FireStoreUtils
import com.mayad.instagram.features.auth.common.domain.IGetUserRepo
import com.mayad.instagram.features.auth.common.domain.follow.IFollowRepo
import com.mayad.instagram.features.navigation.profile.block.domain.Block
import com.mayad.instagram.features.navigation.profile.block.domain.IBlockUserRepo
import com.mayad.instagram.features.navigation.profile.recentActivity.data.ActivityType
import com.mayad.instagram.features.navigation.profile.recentActivity.data.RecentActivityDTO
import com.mayad.instagram.features.navigation.profile.recentActivity.domain.IRecentActivityRepo
import javax.inject.Inject

class BlockUserRepo @Inject constructor(
    firestore: FirebaseFirestore,
    private val followRepo: IFollowRepo,
    private val getUserRepo: IGetUserRepo,
    private val localDS: IUserLocalDS,
    private val recentActivityRepo: IRecentActivityRepo
) : FireStoreUtils<BlockDTO>(firestore), IBlockUserRepo {
    override val collectionName = USERS_COLLECTION

    override suspend fun getBlockedUsers(): List<Block> {
        val currentUserId = localDS.getUserId()
        val blockList = getAllItems(collectionName, currentUserId, USERS_I_BLOCKED_COLLECTION)
            .map { BlockedUsersMapper.mapToDomain(it) }
        blockList.forEach { it.user = getUserRepo.getUser(it.blockedUserId) }
        return blockList
    }

    /**
     * Block a user with the given block details.
     *
     * @param dto The block details including the blocked user ID, blocker ID, timestamp, and reason.
     */
    override suspend fun blockUser(dto: BlockDTO) {
        val currentUserId = localDS.getUserId()
        // If the current user is following the blocked user, unfollow them.
        val isUserIFollow = isNestedDocumentExist(
            documentId = currentUserId,
            nestedCollection = FOLLOWINGS_COLLECTION,
            nestedDocumentId = dto.blockedUserId
        )
        if (isUserIFollow) followRepo.unFollowUser(dto.blockedUserId)

        // If the blocked user is following the current user, remove them as a follower.
        val isUserFollowingMe = isNestedDocumentExist(
            documentId = dto.blockedUserId,
            nestedCollection = FOLLOWINGS_COLLECTION,
            nestedDocumentId = currentUserId
        )
        if (isUserFollowingMe) followRepo.removeFollower(dto.blockedUserId)

        // Add the blocked user to the current user's blocked users list.
        updateItemOtherType(
            baseCollection = collectionName,
            documentId = currentUserId,
            nestedCollection = USERS_I_BLOCKED_COLLECTION,
            nestedDocumentId = dto.blockedUserId,
            item = dto
        )

        // Add the current user to the blocked user's blocked by list.
        updateItemOtherType(
            baseCollection = collectionName,
            documentId = dto.blockedUserId,
            nestedCollection = USERS_BLOCKED_ME_COLLECTION,
            nestedDocumentId = currentUserId,
            item = dto
        )
        recentActivityRepo.addItem(
            RecentActivityDTO(
                generateId(), dto.blockedUserId, "", ActivityType.BLOCKED_USER.type,
                Timestamp.now(), ""
            )
        )
    }

    override suspend fun unblockUser(otherUserId: String) {
        val currentUserId = localDS.getUserId()
        // Remove the other user from the current user's blocked users list.
        deleteItem(
            baseCollection = collectionName,
            documentId = currentUserId,
            nestedCollection = USERS_I_BLOCKED_COLLECTION,
            nestedDocumentId = otherUserId
        )

        // Remove the current user from the other user's blocked by list.
        deleteItem(
            baseCollection = collectionName,
            documentId = otherUserId,
            nestedCollection = USERS_BLOCKED_ME_COLLECTION,
            nestedDocumentId = currentUserId
        )
        recentActivityRepo.addItem(
            RecentActivityDTO(
                generateId(), otherUserId, "", ActivityType.UN_BLOCK_USER.type,
                Timestamp.now(), ""
            )
        )
    }


}