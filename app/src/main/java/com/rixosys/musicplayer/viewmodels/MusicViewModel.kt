package com.rixosys.musicplayer.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rixosys.musicplayer.model.Song
import com.rixosys.musicplayer.repository.SongRepository

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SongRepository(application)

    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

    private val _currentSongIndex = MutableLiveData(0)
    val currentSongIndex: LiveData<Int> get() = _currentSongIndex

    init {
        _songs.value = repository.getAllSongs()
    }

    fun setSongIndex(index: Int) {
        _currentSongIndex.value = index
    }

    fun nextSong() {
        _currentSongIndex.value = (_currentSongIndex.value?.plus(1) ?: 0) % (_songs.value?.size ?: 1)
    }

    fun previousSong() {
        _currentSongIndex.value = (_currentSongIndex.value?.minus(1)?.takeIf { it >= 0 } ?: (_songs.value?.size ?: 1) - 1)
    }
}