package com.mayad.instagram.features.navigation.profile.recentActivity.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mayad.instagram.android.base.Constants.RECENT_ACTIVITY_COLLECTION
import com.mayad.instagram.android.base.Constants.USERS_COLLECTION
import com.mayad.instagram.android.io.domain.IUserLocalDS
import com.mayad.instagram.core.domain.interactors.FireStoreUtils
import com.mayad.instagram.features.navigation.profile.recentActivity.domain.IRecentActivityRepo
import javax.inject.Inject

class RecentActivityRepo @Inject constructor(
    firestore: FirebaseFirestore,
    private val localDS: IUserLocalDS,
) : FireStoreUtils<RecentActivityDTO>(firestore), IRecentActivityRepo {
    override val collectionName = RECENT_ACTIVITY_COLLECTION

    override suspend fun addItem(dto: RecentActivityDTO) {
        val userId = localDS.getUserId()
        dto.id = generateId()
        updateItem(
            baseCollection = USERS_COLLECTION,
            documentId = userId,
            nestedCollection = collectionName,
            nestedDocumentId = dto.id,
            item = dto
        )
    }

    override suspend fun deleteItem(id: String) {
        val userId = localDS.getUserId()
        deleteItem(
            baseCollection = USERS_COLLECTION,
            documentId = userId,
            nestedCollection = collectionName,
            nestedDocumentId = id
        )
    }


}