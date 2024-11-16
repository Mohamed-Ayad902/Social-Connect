package com.mayad.instagram.features.navigation.home.ui

import android.content.Intent
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mayad.instagram.R
import com.mayad.instagram.android.base.BaseFragment
import com.mayad.instagram.android.base.Constants
import com.mayad.instagram.android.extension.logd
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.android.extension.navigateSafe
import com.mayad.instagram.android.extension.observe
import com.mayad.instagram.android.extension.show
import com.mayad.instagram.android.extension.showSnackBar
import com.mayad.instagram.android.utils.ScrollListener
import com.mayad.instagram.databinding.FragmentHomeBinding
import com.mayad.instagram.features.common.ui.posts.PostsAdapter
import com.mayad.instagram.features.common.ui.posts.PostsPagingAdapter
import com.mayad.instagram.features.navigation.profile.user_reels.ui.HomeReelsImagesAdapter
import com.mayad.instagram.features.navigation.profile.user_stories.screen.StoriesActivity
import com.mayad.instagram.features.saved.data.SavedItemType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    @Inject
    lateinit var storiesAdapter: StoriesAdapter

    @Inject
    lateinit var reelAdapter: HomeReelsImagesAdapter

    @Inject
    lateinit var postsAdapter: PostsPagingAdapter

    @Inject
    lateinit var firstPostAdapter: PostsAdapter
    private val viewModel: HomeVM by viewModels()
    private var noMorePosts = false

    override fun onResume() {
        super.onResume()
        // important to update stories after user return from stories screen
        // so we make sure it's data is updated with like state
        viewModel.getStories()
    }

    override fun onFragmentReady() {
        binding.btnNotifications.setOnClickListener {
            navigateSafe(HomeFragmentDirections.actionHomeFragmentToNotificationsFragment())
        }
        initStoriesRV()
        initFirstPostRV()
        initReelsRV()
        initPostsRV()
        viewModel.getFollowingsUsers()
        viewModel.getNotificationsCount()
        binding.storiesNestedScrollView.isHorizontalScrollBarEnabled = false
        binding.reelsNestedScrollView.isHorizontalScrollBarEnabled = false
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    postsAdapter.pauseAllMediaPlayers()
                    postsAdapter.releaseAllMediaPlayers()
                    if (isEnabled) {
                        isEnabled = false // Temporarily disable to avoid multiple back presses
                        activity?.onBackPressed()
                    }
                }
            })
    }

    private fun initFirstPostRV() {
        with(firstPostAdapter) {
            binding.firstPostRV.adapter = this

            setonLikeClickedListener {
                if (it.isLikedByMe) viewModel.removeLikePost(it)
                else viewModel.likePost(it)
            }
            setonDoubleTapClickedListener {
                viewModel.likePost(it)
            }
            setonCommentClickedListener {
                postsAdapter.pauseAllMediaPlayers()
                postsAdapter.releaseAllMediaPlayers()
                firstPostAdapter.pauseAllMediaPlayers()
                firstPostAdapter.releaseAllMediaPlayers()
                navigateSafe(HomeFragmentDirections.actionHomeFragmentToCommentsFragment(it))
            }
            setSaveClickedListener { postId, isSavedBefore ->
                if (isSavedBefore)
                    viewModel.removeSavedItem(postId)
                else viewModel.saveItem(postId, SavedItemType.POST.type)
            }
        }
    }

    private fun initPostsRV() {
        with(postsAdapter) {
            binding.postsRv.adapter = this

            setonLikeClickedListener {
                if (it.isLikedByMe) viewModel.removeLikePost(it)
                else viewModel.likePost(it)
            }
            setonDoubleTapClickedListener {
                viewModel.likePost(it)
            }
            setonCommentClickedListener {
                firstPostAdapter.pauseAllMediaPlayers()
                firstPostAdapter.releaseAllMediaPlayers()
                postsAdapter.pauseAllMediaPlayers()
                postsAdapter.releaseAllMediaPlayers()
                navigateSafe(HomeFragmentDirections.actionHomeFragmentToCommentsFragment(it))
            }
            setSaveClickedListener { postId, isSavedBefore ->
                if (isSavedBefore)
                    viewModel.removeSavedItem(postId)
                else viewModel.saveItem(postId, SavedItemType.POST.type)
            }
        }
        binding.postsRv.addOnScrollListener(object :
            ScrollListener(LinearLayoutManager(requireContext()), 3) {
            override fun onLoadMore(currentPage: Int) {
                if (!noMorePosts) viewModel.getPosts()
                else showSnackBar(R.string.no_more_posts)
            }
        })
    }

    private fun initReelsRV() {
        binding.reelsRv.adapter = reelAdapter
        reelAdapter.setOnItemClickListener {
            postsAdapter.pauseAllMediaPlayers()
            postsAdapter.releaseAllMediaPlayers()
            firstPostAdapter.pauseAllMediaPlayers()
            firstPostAdapter.releaseAllMediaPlayers()
            navigateSafe(
                R.id.global_action_to_reelsListFragment,
                bundle = bundleOf(
                    Constants.REELS_BUNDLE to listOf(reelAdapter.list[it]).toTypedArray(),
                    Constants.SELECTED_REEL_BUNDLE to it
                )
            )
        }
    }

    private fun initStoriesRV() {
        binding.storiesRV.adapter = storiesAdapter
        storiesAdapter.setonItemClickListener {
            postsAdapter.pauseAllMediaPlayers()
            postsAdapter.releaseAllMediaPlayers()
            Intent(requireContext(), StoriesActivity::class.java).apply {
                putParcelableArrayListExtra(
                    Constants.STORIES_EXTRA,
                    ArrayList(listOf(it))
                )
                startActivity(this)
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

    override fun onPause() {
        super.onPause()
        postsAdapter.pauseAllMediaPlayers()
        postsAdapter.releaseAllMediaPlayers()
        firstPostAdapter.pauseAllMediaPlayers()
        firstPostAdapter.releaseAllMediaPlayers()
    }

    override fun onDestroy() {
        super.onDestroy()
        postsAdapter.pauseAllMediaPlayers()
        postsAdapter.releaseAllMediaPlayers()
        firstPostAdapter.pauseAllMediaPlayers()
        firstPostAdapter.releaseAllMediaPlayers()
    }

    private fun handleUiState(it: HomeUiState) {
        when (it) {
            is HomeUiState.Failure -> {
                showSnackBar(it.exception.messageRes)
                it.loge(TAG)
            }

            is HomeUiState.PostsLoaded -> {
                "PostsLoaded: ${it.posts.items.size}".logd(TAG)
                binding.postsRv.show()
                binding.firstPostRV.show()
                noMorePosts = it.posts.hasReachedEnd
                val allPosts = it.posts.items

                when {
                    // This is the very first page with at least one post
                    postsAdapter.itemCount == 0 && firstPostAdapter.itemCount == 0 -> {
                        allPosts.firstOrNull()?.let { firstPost ->
                            firstPostAdapter.list = listOf(firstPost)
                            postsAdapter.list = allPosts.drop(1)
                        }
                    }
                    // Additional posts are loaded
                    else -> postsAdapter.list += allPosts
                }
            }

            is HomeUiState.StoriesLoaded -> {
                storiesAdapter.list = it.stories
                binding.tvStories.show(it.stories.isNotEmpty())
                binding.storiesRV.show()
            }

            is HomeUiState.ReelsLoaded -> {
                binding.reelsRv.show()
                reelAdapter.list = it.reels.shuffled()
            }
            is HomeUiState.LoadingPosts -> binding.postsShimmer.show(it.isLoading)
            is HomeUiState.LoadingReels -> binding.reelsShimmer.show(it.isLoading)
            is HomeUiState.LoadingStories -> binding.storiesShimmer.show(it.isLoading)
            is HomeUiState.NotificationsCount -> {
                binding.tvNotificationCount.text = it.count.toString()
                binding.notificationsCountHolder.show(it.count > 0)
            }
        }
    }

    companion object {
        private const val TAG = "HomeFragment"
    }
}