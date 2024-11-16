package com.mayad.instagram.features.navigation.messages.chat.ui

import android.os.Build
import android.view.WindowInsetsController
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.devlomi.record_view.OnRecordListener
import com.mayad.instagram.R
import com.mayad.instagram.android.base.BaseFragment
import com.mayad.instagram.android.base.Constants
import com.mayad.instagram.android.dialog.GalleryDialogs
import com.mayad.instagram.android.extension.afterTextChanged
import com.mayad.instagram.android.extension.getReadableTimeDifferenceFor1Hour
import com.mayad.instagram.android.extension.gone
import com.mayad.instagram.android.extension.loadImage
import com.mayad.instagram.android.extension.logd
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.android.extension.navigateSafe
import com.mayad.instagram.android.extension.observe
import com.mayad.instagram.android.extension.show
import com.mayad.instagram.android.extension.showToast
import com.mayad.instagram.android.extension.visible
import com.mayad.instagram.databinding.FragmentChattingBinding
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChattingFragment : BaseFragment<FragmentChattingBinding>() {
    private val viewModel: ChatVM by viewModels()
    private val args: ChattingFragmentArgs by navArgs()
    private val user by lazy { args.reciever }

    @Inject
    lateinit var messagesAdapter: MessagesAdapter

    @Inject
    lateinit var galleryAdapter: DialogGalleryAdapter

    override fun onFragmentReady() {
        // Set the status bar color for this fragment only
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 and above
            val windowInsetsController = requireActivity().window.insetsController
            windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
            requireActivity().window.navigationBarColor =
                ContextCompat.getColor(requireContext(), R.color.instagram_middleColor)
        } else {
            // For older Android versions
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.instagram_middleColor)
        }
        setActions()
        setupRecorder()
        viewModel.loadDirectories()
        viewModel.getMessages(user.id)
        binding.messagesRV.adapter = messagesAdapter
        binding.apply {
            imageView.loadImage(user.profilePicture)
            tvUsername.text = user.username
            tvFullName.text = user.fullName
            ivActiveNow.show(user.isOnline)
            tvLastActiveTime.text =
                if (user.isOnline) requireContext().getString(R.string.active_now)
                else user.lastOnlineTime?.getReadableTimeDifferenceFor1Hour(requireContext())
        }
    }

    private fun setupRecorder() {
        binding.apply {
            recordButton.setRecordView(recordView)
            recordView.setOnBasketAnimationEndListener { textMessageContainer.visible() }
            recordView.setOnRecordListener(object : OnRecordListener {
                override fun onStart() {
                    textMessageContainer.gone()
                    viewModel.startRecording()
                }

                override fun onCancel() {
                    textMessageContainer.visible()
                    viewModel.cancelRecording()
                }

                override fun onFinish(recordTime: Long, limitReached: Boolean) {
                    textMessageContainer.visible()
                    viewModel.sendMessage(
                        receiverId = user.id,
                        duration = recordTime,
                        receiverToken = user.token
                    )
                }

                override fun onLessThanSecond() {
                    textMessageContainer.visible()
                    viewModel.cancelRecording()
                }

                override fun onLock() {}
            })

        }
    }

    private fun setActions() {
        messagesAdapter.onMediaClickListener {
            navigateSafe(
                ChattingFragmentDirections.actionChattingFragmentToShowMediaFragment(
                    it
                )
            )
        }
        binding.apply {
            etMessage.afterTextChanged {
                btnSend.show(it.trim().isNotEmpty())
                recordButton.show(it.trim().isEmpty())
                recordView.show(it.trim().isEmpty())
            }
            btnBack.setOnClickListener { closeFragment() }
            tvUsername.setOnClickListener {
                navigateSafe(
                    R.id.global_action_to_profileFragment,
                    bundleOf(Constants.FIELD_USER_ID to user.id)
                )
            }
            tvFullName.setOnClickListener {
                navigateSafe(
                    R.id.global_action_to_profileFragment,
                    bundleOf(Constants.FIELD_USER_ID to user.id)
                )
            }
            btnSend.setOnClickListener {
                viewModel.sendMessage(
                    msg = etMessage.text.toString(),
                    receiverId = user.id,
                    receiverToken = user.token
                )
                if (messagesAdapter.messages.size > 1)
                    messagesRV.smoothScrollToPosition(messagesAdapter.messages.size - 1)
                etMessage.setText("")
            }
            btnGallery.setOnClickListener {
                GalleryDialogs.showGallery(
                    requireContext(),
                    galleryAdapter,
                    onNextClicked = {
                        viewModel.sendMessage(
                            media = it,
                            receiverId = user.id,
                            receiverToken = user.token
                        )
                    },
                    onDirectorySelected = {
                        viewModel.loadPictures(it)
                    })
            }
            val currentUser = ZegoUIKitUser(user.id, user.username)
            currentUser.avatar = user.profilePicture

            btnAudioCall.setIsVideoCall(false)
            btnVideoCall.setIsVideoCall(true)

            btnAudioCall.setOnClickListener { _ ->
                val users = arrayListOf(currentUser)
                btnAudioCall.setInvitees(users)
            }

            btnVideoCall.setOnClickListener { _ ->
                val users = arrayListOf(currentUser)
                btnVideoCall.setInvitees(users)
            }
        }
    }

    override fun subscribeToObservables() {
        viewModel.apply {
            observe(viewState) {
                handleUiState(it)
            }
        }
    }

    private fun handleUiState(it: ChatUiState) {
        when (it) {
            is ChatUiState.Failure -> {
                it.exception.loge(TAG)
                showToast(it.exception.messageRes)
            }

            is ChatUiState.Loading -> showProgress(it.isLoading)
            ChatUiState.MessageSent -> {
                "MessageSent size - ${messagesAdapter.messages.size}".logd(TAG)
                if (messagesAdapter.messages.isEmpty()) viewModel.getMessages(user.id)
            }

            is ChatUiState.MessagesLoaded -> {
                messagesAdapter.messages = it.messages
                viewModel.seeMessages(user.id)
            }

            is ChatUiState.LoadedDirectories -> GalleryDialogs.setDirectories(it.directories)
            is ChatUiState.LoadedPictures -> galleryAdapter.list = it.pictures
        }
    }

    override fun onPause() {
        super.onPause()
        messagesAdapter.releaseAudioPlayer()
    }
    override fun onDestroyView() {
        super.onDestroyView()

        // Restore the default status bar color when leaving the fragment
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12 and above
            requireActivity().window.insetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            requireActivity().window.statusBarColor =
                ContextCompat.getColor(requireContext(), R.color.background)
        }
    }
    companion object {
        private const val TAG = "ChattingFragment"
    }
}