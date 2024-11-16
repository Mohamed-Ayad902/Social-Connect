package com.mayad.instagram.features.navigation.messages.recent.ui

import androidx.fragment.app.viewModels
import com.mayad.instagram.R
import com.mayad.instagram.android.base.BaseFragment
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.android.extension.navigateSafe
import com.mayad.instagram.android.extension.observe
import com.mayad.instagram.android.extension.show
import com.mayad.instagram.android.extension.showToast
import com.mayad.instagram.databinding.FragmentRecentMessagesBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecentMessagesFragment : BaseFragment<FragmentRecentMessagesBinding>() {
    @Inject
    lateinit var recentMessagesAdapter: RecentMessagesAdapter
    @Inject
    lateinit var onlineUsersAdapter: OnlineUsersAdapter
    private val viewModel: RecentMessagesVM by viewModels()


    override fun onFragmentReady() {
        setToolBarConfigs(R.string.recent_messages, true)
        viewModel.getRecentMessages()
        viewModel.getOnlineFollowings()
        setupMessagesRV()
        setupOnlineUsersRV()
        binding.storiesNestedScrollView.isHorizontalScrollBarEnabled = false
    }

    private fun setupOnlineUsersRV() {
        binding.onlineFollowingsRV.adapter = onlineUsersAdapter
        onlineUsersAdapter.setOnItemClickListener {
            navigateSafe(
                RecentMessagesFragmentDirections.actionRecentMessagesFragmentToChattingFragment(
                    it
                )
            )
        }
    }

    private fun setupMessagesRV() {
        binding.recentMessagesRV.adapter = recentMessagesAdapter
        recentMessagesAdapter.setonItemClickListener {
            navigateSafe(
                RecentMessagesFragmentDirections.actionRecentMessagesFragmentToChattingFragment(
                    it
                )
            )
        }
    }

    override fun subscribeToObservables() {
        viewModel.apply {
            observe(viewState) {
                handleUiState(it)
            }
        }
    }

    private fun handleUiState(it: RecentMessagesUiState) {
        when (it) {
            is RecentMessagesUiState.Failure -> {
                it.exception.loge(TAG)
                showToast(it.exception.messageRes)
            }
            is RecentMessagesUiState.RecentMessagesLoaded -> {
                binding.recentMessagesRV.show()
                recentMessagesAdapter.list = it.recentMessages
            }

            is RecentMessagesUiState.OnlineUsersLoaded -> {
                binding.onlineFollowingsRV.show()
                onlineUsersAdapter.list = it.users
            }

            is RecentMessagesUiState.LoadingMessages -> binding.shimmerRecentMessages.show(it.isLoading)
            is RecentMessagesUiState.LoadingUsers -> binding.usersShimmer.show(it.isLoading)
        }
    }

    companion object {
        private const val TAG = "RecentMessagesFragment"
    }
}