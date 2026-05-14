package com.karunadakote.audio

import android.content.Context
import android.media.MediaPlayer
import com.karunadakote.R

object AmbientSoundManager {

    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {

        stop()

        mediaPlayer = MediaPlayer.create(
            context,
            R.raw.fort_ambience
        )

        mediaPlayer?.isLooping = true

        mediaPlayer?.setVolume(
            0.25f,
            0.25f
        )

        mediaPlayer?.start()
    }

    fun stop() {

        mediaPlayer?.stop()

        mediaPlayer?.release()

        mediaPlayer = null
    }
}