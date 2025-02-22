package com.rixosys.musicplayer



import android.content.ComponentName

import android.content.Context

import android.content.Intent

import android.content.ServiceConnection

import android.os.Bundle

import android.os.IBinder

import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.LinearLayoutManager

import com.rixosys.musicplayer.adapters.MusicAdapter

import com.rixosys.musicplayer.databinding.ActivityMainBinding

import com.rixosys.musicplayer.service.MusicService

import com.rixosys.musicplayer.viewmodels.MusicViewModel



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val musicViewModel: MusicViewModel by viewModels()

    private var musicService: MusicService? = null

    private var isBound = false

    private var currentPlayingIndex: Int? = null

    private lateinit var adapter: MusicAdapter // Declare adapter as a class member



    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            val binder = service as MusicService.MusicBinder

            musicService = binder.getService()

            isBound = true





            musicViewModel.songs.observe(this@MainActivity) {

                updateUI()

            }

        }



        override fun onServiceDisconnected(name: ComponentName?) {

            isBound = false

            musicService = null

            currentPlayingIndex = null

            adapter.notifyDataSetChanged()

        }

    }



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)



        adapter = MusicAdapter { position ->

            if (currentPlayingIndex != position) {
                currentPlayingIndex = position
                musicService?.stopMusic();
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putExtra("songIndex", position)
                startActivity(intent)

            }

        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.adapter = adapter

        musicViewModel.songs.observe(this) { songList ->
            adapter.submitList(songList)

        }

    }



    override fun onResume() {

        super.onResume()

        val intent = Intent(this, MusicService::class.java)

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

    }



    override fun onPause() {

        super.onPause()

        if (isBound) {

            unbindService(serviceConnection)

            isBound = false

        }

    }



    private fun updateUI() {

        if (isBound && musicService != null) {

            if (musicService?.isPlaying() == true) {

                val newIndex = musicService?.getCurrentSongIndex()

                if (newIndex != currentPlayingIndex) {

                    currentPlayingIndex = newIndex

                    adapter.setCurrentPlayingIndex(currentPlayingIndex)

                    adapter.notifyDataSetChanged()

                }

            } else {

                if (currentPlayingIndex != null) {
                    currentPlayingIndex = null
                    adapter.setCurrentPlayingIndex(null)
                    adapter.notifyDataSetChanged()

                }

            }

        }

    }

}