package com.mayad.instagram.features.navigation.profile.user_posts.ui

import android.text.method.LinkMovementMethod
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.mayad.instagram.R
import com.mayad.instagram.android.base.BaseFragment
import com.mayad.instagram.android.base.Constants
import com.mayad.instagram.android.extension.appendEmojis
import com.mayad.instagram.android.extension.clickable
import com.mayad.instagram.android.extension.detectHashtags
import com.mayad.instagram.android.extension.getReadableTimeDifference
import com.mayad.instagram.android.extension.loadImage
import com.mayad.instagram.android.extension.loge
import com.mayad.instagram.android.extension.navigateSafe
import com.mayad.instagram.android.extension.observe
import com.mayad.instagram.android.extension.showSnackBar
import com.mayad.instagram.android.extension.showToast
import com.mayad.instagram.core.common.domain.User
import com.mayad.instagram.databinding.FragmentCommentsBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentsFragment : BaseFragment<FragmentCommentsBinding>() {
    private val args: CommentsFragmentArgs by navArgs()
    private val post by lazy { args.post }
    private val comment by lazy { args.comment }
    private val viewModel: UserContentVM by activityViewModels()

    @Inject
    lateinit var commentsAdapter: CommentsAdapter
    override fun onFragmentReady() {
        setToolBarConfigs(R.string.comments, true)
        setPostViews()
        viewModel.getCurrentUser()
        viewModel.getComments(post.contentId)
        setActions()
    }

    private fun setActions() {
        binding.apply {
            etComment.appendEmojis(btnCool, btnCrazy, btnCringe, btnHaha, btnHeart, btnSad)
            btnNext.setOnClickListener {
                viewModel.commentPost(etComment.text.toString(), post)
            }
            commentsAdapter.setonLikeClickedListener { comment ->
                if (comment.isLikedByMe)
                    viewModel.removePostLikeComment(post.contentId, comment)
                else
                    viewModel.likePostComment(post, comment)
            }
            commentsAdapter.setonDoubleTapClickedListener { comment ->
                viewModel.likePostComment(post, comment)
            }
            commentsAdapter.setonUserClickedListener {
                navigateSafe(
                    R.id.global_action_to_profileFragment,
                    bundleOf(Constants.FIELD_USER_ID to it)
                )
            }
        }
    }

    private fun setPostViews() {
        binding.apply {
            creatorProfilePic.loadImage(post.user?.profilePicture!!)
            tvUsername.text = post.user?.username!!
            tvTime.text = post.timestamp.getReadableTimeDifference(requireContext())
            tvCaption.movementMethod = LinkMovementMethod.getInstance()
            tvCaption.detectHashtags(post.caption, onHashtagClick = { showToast(it) })
            commentsRecyclerView.adapter = commentsAdapter
        }
    }

    override fun subscribeToObservables() {
        viewModel.apply {
            observe(viewState) {
                handleUiState(it)
            }
        }
    }

    private fun handleUiState(it: UserPostsUiState) {
        when (it) {
            is UserPostsUiState.Failure -> {
                it.exception.loge(TAG)
                showSnackBar(it.exception.messageRes)
            }

            is UserPostsUiState.Loading -> showProgress(it.loading)
            is UserPostsUiState.CommentAdded -> {
                val newList = commentsAdapter.list.toMutableList()
                newList.add(it.comment)
                commentsAdapter.list = newList.toList()
                binding.etComment.setText("")
            }

            is UserPostsUiState.CommentsLoaded -> {
                commentsAdapter.list = it.comments
                comment?.let {
                    binding.commentsRecyclerView.scrollToPosition(commentsAdapter.list.indexOf(it))
                }
            }

            is UserPostsUiState.LoadedUserCurrentUser -> setUserViews(it.user)
            else -> {}
        }
    }

    private fun setUserViews(user: User) {
        binding.ivProfilePic.loadImage(user.profilePicture)
        binding.btnNext.clickable(true)
    }

    companion object {
        private const val TAG = "CommentsFragment"
    }
}