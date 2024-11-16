package com.mayad.instagram.features.navigation.messages.chat.ui

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.mayad.instagram.R
import com.mayad.instagram.android.base.BaseFragment
import com.mayad.instagram.android.extension.fadeInThenOut
import com.mayad.instagram.android.extension.loadImage
import com.mayad.instagram.android.extension.show
import com.mayad.instagram.databinding.FragmentShowMediaBinding

class ShowMediaFragment : BaseFragment<FragmentShowMediaBinding>() {
    private val args: ShowMediaFragmentArgs by navArgs()
    private val media by lazy { args.media }

    override fun onFragmentReady() {
        var isPlayerMuted = false
        binding.apply {
            btnMute.show(media.isVideo)
            playerView.show(media.isVideo)
            imageView.show(media.isVideo.not())
            media.thumb?.fullResolution?.let { imageView.loadImage(it) }
            if (media.isVideo) {
                playerView.player?.pause()
                playerView.player?.release()
                playerView.player = ExoPlayer.Builder(requireContext()).build()
                playerView.player?.repeatMode = ExoPlayer.REPEAT_MODE_ONE
                playerView.player?.setMediaItem(MediaItem.fromUri(media.videoPath!!))
                playerView.player?.prepare()
            }
            btnMute.setOnClickListener {
                if (isPlayerMuted)
                    playerView.player?.volume = 1f
                else playerView.player?.volume = 0f
                isPlayerMuted = !isPlayerMuted
                val imageRes = if (isPlayerMuted) R.drawable.muted else R.drawable.un_muted
                btnMute.setImageResource(imageRes)
            }
        }
    }

    override fun subscribeToObservables() {}

    override fun onPause() {
        super.onPause()
        binding.playerView.player?.pause()
    }
}