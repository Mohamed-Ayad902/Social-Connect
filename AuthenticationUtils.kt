package com.mayad.instagram.core.domain.interactors

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mayad.instagram.android.extension.logd
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.android.extension.logw
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationUtils @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    private val user = firebaseAuth.currentUser

    suspend fun changePassword(currentPassword: String, newPassword: String): Boolean {
        if (user == null || user.email == null) {
            "changePassword: user is null".loge(TAG)
            return false
        }

        return try {
            val credentials = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credentials).await()
            user.updatePassword(newPassword).await()
            "changePassword: password updated".logd(TAG)
            true
        } catch (e: Exception) {
            "changePassword: error ${e.message}".loge(TAG)
            false
        }

    }

    suspend fun logout(): Boolean = suspendCoroutine { continuation ->
        firebaseAuth.signOut()
        if (firebaseAuth.currentUser == null)
            continuation.resume(true)
        else
            continuation.resume(false)

    }

    suspend fun createUser(email: String, password: String): String? {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        sendVerificationEmail(authResult.user)
        return authResult.user?.uid
    }

    private suspend fun sendVerificationEmail(user: FirebaseUser?) =
        user?.sendEmailVerification()?.await().also {
            "$user".logw("created sendVerificationEmail $TAG")
            "created email verification sent to: ${user?.email}".logd(TAG)
        }

    fun isEmailVerified() =
        user?.isEmailVerified ?: false

    suspend fun login(email: String, password: String): FirebaseUser? {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        "LoginRepo - login: ${authResult.credential} -- ${authResult.additionalUserInfo} -- ${authResult.user}".logd(TAG)
        "LoginRepo - login email verified: ${authResult.user?.isEmailVerified}".logd(TAG)
        return if (authResult.user?.isEmailVerified == true) authResult.user else null
    }

    suspend fun resetPassword(email: String): Void? =
        firebaseAuth.sendPasswordResetEmail(email).await()

    fun getCurrentUserId() = user?.uid

    companion object {
        private const val TAG = "AuthenticationUtils"
    }
}