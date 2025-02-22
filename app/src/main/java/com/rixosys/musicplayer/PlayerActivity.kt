package com.rixosys.musicplayer



import android.content.ComponentName

import android.content.Context

import android.content.Intent

import android.content.ServiceConnection

import android.os.Bundle

import android.os.Handler

import android.os.IBinder

import android.os.Looper

import android.util.Log

import android.widget.SeekBar

import androidx.appcompat.app.AppCompatActivity

import com.rixosys.musicplayer.databinding.ActivityPlayerBinding

import com.rixosys.musicplayer.service.MusicService



class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private var musicService: MusicService? = null

    private var isBound = false

    private var songIndex = 0

    private val handler = Handler(Looper.getMainLooper())



    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as MusicService.MusicBinder

            musicService = binder.getService()

            isBound = true

            songIndex = intent.getIntExtra("songIndex", 0)

            musicService?.playMusic(songIndex)

            updateUI()

            setupSeekBar()

        }



        override fun onServiceDisconnected(name: ComponentName?) {

            isBound = false

            musicService = null

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)

        setContentView(binding.root)



        val intent = Intent(this, MusicService::class.java)

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)





        binding.btnPlayPause.setOnClickListener {

            if (musicService?.isPlaying() == true) {

                musicService?.pauseMusic()

            } else {

                musicService?.playMusic(songIndex)

            }

            updateUI()

        }



        binding.btnNext.setOnClickListener {

            musicService?.seekForward(10000)

            updateUI()

        }



        binding.btnPrevious.setOnClickListener {

            musicService?.seekBackward(10000)

            updateUI()

        }



        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser && musicService != null) {

                    musicService?.seekTo(progress)

                }

            }



            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

    }



    private fun setupSeekBar() {

        handler.post(object : Runnable {

            override fun run() {

                if (musicService != null && isBound) {

                    val currentPosition = musicService?.getCurrentPosition() ?: 0

                    val duration = musicService?.getDuration() ?: 0



                    binding.seekBar.max = duration

                    binding.seekBar.progress = currentPosition

                    handler.postDelayed(this, 1000) // Update every second

                } else {

                    handler.removeCallbacks(this) // Stop if service is null or unbound

                }

            }

        })

    }



    private fun updateUI() {

        if (musicService != null) {

            val isPlaying = musicService?.isPlaying() ?: false

            binding.btnPlayPause.setImageResource(if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play)



            try {

                binding.txtSongName.text = getString(resources.getIdentifier(resources.getResourceEntryName(musicService!!.songList[musicService!!.getCurrentSongIndex()]), "string", packageName))

            } catch (e: Exception) {

                Log.e("PlayerActivity", "Error getting song name", e)

                binding.txtSongName.text = "Unknown Song"

            }

        }

    }



    override fun onDestroy() {

        super.onDestroy()

        handler.removeCallbacksAndMessages(null)

        if (isBound) {

            unbindService(serviceConnection)

            isBound = false

        }

    }

}