package com.mayad.instagram.android.exception

import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.StorageException
import com.mayad.instagram.R
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.core.common.Resource

object ExceptionCatcher {

    fun <Model> catchNetworkError(exception: Throwable): Resource<Model> {
        exception.toString().loge("ExceptionCatcher")

        val error: InstaExceptions = when (exception) {
            is FirebaseAuthInvalidUserException -> InstaExceptions.Authentication(R.string.invalid_user_exception)
            is FirebaseAuthInvalidCredentialsException -> InstaExceptions.Authentication(R.string.invalid_credentials)
            is FirebaseAuthUserCollisionException -> InstaExceptions.Authentication(R.string.email_already_exists)
            is FirebaseTooManyRequestsException -> InstaExceptions.Authentication(R.string.too_many_request)

            is FirebaseFirestoreException -> {
                when (exception.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> InstaExceptions.Network(R.string.permission_denied)
                    FirebaseFirestoreException.Code.UNAVAILABLE -> InstaExceptions.Network(R.string.service_not_available)
                    FirebaseFirestoreException.Code.CANCELLED -> InstaExceptions.Network(R.string.request_canceled)
                    else -> InstaExceptions.Unknown(R.string.unknown_error,exception.message.toString())
                }
            }

            is FirebaseFunctionsException -> {
                when (exception.code) {
                    FirebaseFunctionsException.Code.UNAUTHENTICATED -> InstaExceptions.Network(R.string.un_authenticated)
                    FirebaseFunctionsException.Code.NOT_FOUND -> InstaExceptions.Network(R.string.not_found)
                    FirebaseFunctionsException.Code.UNAVAILABLE -> InstaExceptions.Network(R.string.service_not_available)
                    FirebaseFunctionsException.Code.ALREADY_EXISTS -> InstaExceptions.Network(R.string.function_already_exists)
                    else -> InstaExceptions.Unknown(R.string.unknown_error,exception.message.toString())
                }
            }

            is StorageException -> {
                when (exception.errorCode) {
                    StorageException.ERROR_NOT_AUTHORIZED -> InstaExceptions.Storage(R.string.un_authenticated)
                    StorageException.ERROR_OBJECT_NOT_FOUND -> InstaExceptions.Storage(R.string.not_found)
                    StorageException.ERROR_QUOTA_EXCEEDED -> InstaExceptions.Storage(R.string.quota_exceeded)
                    else -> {
                        InstaExceptions.Unknown(R.string.unknown_error,exception.message.toString())
                    }
                }
            }

            else -> InstaExceptions.Unknown(R.string.unknown_error,exception.message.toString())

        }
        return Resource.Failure(error)
    }
}