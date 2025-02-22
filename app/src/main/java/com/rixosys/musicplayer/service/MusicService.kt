package com.rixosys.musicplayer.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.rixosys.musicplayer.PlayerActivity
import com.rixosys.musicplayer.R
import java.lang.reflect.Field

class MusicService : Service() {
    private val binder = MusicBinder()
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
        set(value) {
            field = value
            showNotification(isPlaying = isPlaying())
        }
    internal val songList: List<Int> by lazy { getAllRawSongs() }
    private val CHANNEL_ID = "MusicPlayerChannel"
    private val NOTIFICATION_ID = 1

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun nextMusic() {
        currentSongIndex = (currentSongIndex + 1) % songList.size
        playNewSong()
    }

    fun previousMusic() {
        currentSongIndex = if (currentSongIndex - 1 < 0) songList.size - 1 else currentSongIndex - 1
        playNewSong()
    }

    private fun playNewSong() {
        mediaPlayer?.let {
            it.reset()
            it.release()
            mediaPlayer = null
        }
        playMusic(currentSongIndex)
    }

    fun playMusic(index: Int) {
        if (index in songList.indices) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, songList[index])
                mediaPlayer?.setOnCompletionListener { nextMusic() }
            } else if (currentSongIndex != index) { 
                mediaPlayer?.reset()
                mediaPlayer = MediaPlayer.create(this, songList[index])
                mediaPlayer?.setOnCompletionListener { nextMusic() }
            }

            mediaPlayer?.let {
                if (!it.isPlaying) {
                    it.start()
                    currentSongIndex = index
                    showNotification(isPlaying = true)
                }
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                showNotification(isPlaying = false)
            }
        }
    }



    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(true)
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true

    private fun getAllRawSongs(): List<Int> {
        val rawClass: Class<*> = R.raw::class.java
        val fields: Array<Field> = rawClass.fields
        return fields.mapNotNull { field ->
            try {
                field.getInt(null)
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun showNotification(isPlaying: Boolean) {
        val intent = Intent(this, PlayerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val playPauseIntent = Intent(this, NotificationReceiver::class.java).apply { action = if (isPlaying) "ACTION_PAUSE" else "ACTION_PLAY" }
        val playPausePendingIntent = PendingIntent.getBroadcast(this, 100, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val nextIntent = Intent(this, NotificationReceiver::class.java).apply { action = "ACTION_NEXT" }
        val nextPendingIntent = PendingIntent.getBroadcast(this, 200, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val prevIntent = Intent(this, NotificationReceiver::class.java).apply { action = "ACTION_PREVIOUS" }
        val prevPendingIntent = PendingIntent.getBroadcast(this, 300, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle("Playing Music")
            .setContentText("Now Playing: ${resources.getResourceEntryName(songList[currentSongIndex])}")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .addAction(R.drawable.ic_skip_previous, "Previous", prevPendingIntent)
            .addAction(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play, "Play/Pause", playPausePendingIntent)
            .addAction(R.drawable.ic_skip_next, "Next", nextPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setOngoing(isPlaying)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Music Player Channel", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
        }
    }

    fun seekForward(ms: Int) {
        mediaPlayer?.let {
            val newPosition = it.currentPosition + ms
            it.seekTo(minOf(newPosition, it.duration))
        }
    }

    fun seekBackward(ms: Int) {
        mediaPlayer?.let {
            val newPosition = it.currentPosition - ms
            it.seekTo(maxOf(newPosition, 0))
        }
    }

    fun getDuration(): Int {
        return mediaPlayer?.duration ?: 0
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    fun seekTo(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getCurrentSongIndex(): Int {
        return currentSongIndex
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            "ACTION_PLAY" -> playMusic(currentSongIndex)
            "ACTION_PAUSE" -> pauseMusic()
            "ACTION_NEXT" -> nextMusic()
            "ACTION_PREVIOUS" -> previousMusic()
        }
        return START_STICKY
    }
}