package com.rixosys.musicplayer.adapters



import android.view.LayoutInflater

import android.view.View

import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil

import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView

import com.rixosys.musicplayer.R

import com.rixosys.musicplayer.databinding.ItemMusicBinding

import com.rixosys.musicplayer.model.Song



class MusicAdapter(

    private val onClick: (Int) -> Unit

) : ListAdapter<Song, MusicAdapter.MusicViewHolder>(SongDiffCallback()) {

    private var currentPlayingIndex: Int? = null

    fun setCurrentPlayingIndex(position: Int?) {

        this.currentPlayingIndex = position

    }

    inner class MusicViewHolder(private val binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song, position: Int) {

            binding.txtSongTitle.text = song.title



            if (position == currentPlayingIndex) {

                binding.imgPlayPause.setImageResource(R.drawable.ic_pause)
                binding.imgPlayPause.visibility = View.VISIBLE

            } else {
                binding.imgPlayPause.visibility = View.INVISIBLE

            }
            binding.root.setOnClickListener { onClick(position) }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {

        val binding = ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MusicViewHolder(binding)

    }



    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {

        holder.bind(getItem(position), position)

    }

}



class SongDiffCallback : DiffUtil.ItemCallback<Song>() {

    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean = oldItem == newItem

}