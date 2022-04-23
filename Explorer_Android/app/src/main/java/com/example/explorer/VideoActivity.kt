package com.example.explorer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.navArgs
import com.example.explorer.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

class VideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVideoBinding

    private val args: VideoActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        binding.playerView.post {
            val player = ExoPlayer.Builder(this).build()
            binding.playerView.player = player
            player.setMediaItem(MediaItem.fromUri(args.path))
            player.prepare()
        }
    }
}