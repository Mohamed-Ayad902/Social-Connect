package com.mayad.instagram.core.domain.interactors

import android.net.Uri
import com.google.firebase.storage.StorageReference
import com.mayad.instagram.android.base.Constants
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class StorageUtils @Inject constructor(private val storage: StorageReference) {

    fun generatePicId(): String {
        val timestamp = System.currentTimeMillis()
        val uniqueId = UUID.randomUUID().toString()
        return "$timestamp-$uniqueId"
    }

    // returns the imageUrl
    suspend fun uploadItem(path: String, imageByteArray: ByteArray): String {
        val picPath = storage.child(path)
        picPath.putBytes(imageByteArray).await()
        return picPath.downloadUrl.await().toString()
    }

    suspend fun uploadUri(path: String, uri: Uri): String {
        val audioPath = storage.child(path)
        audioPath.putFile(uri).await()
        return audioPath.downloadUrl.await().toString()
    }

    fun getFolderPath(
        contentId: String,
        mediaPath: String,
        isFullResolution: Boolean
    ): String {
        val extension = if (isFullResolution) "-FullQuality" else "-LowQuality"
        return "${Constants.POSTS_FOLDER}/$contentId/${Constants.POST_CONTENT_FOLDER}/$mediaPath$extension"
    }
}