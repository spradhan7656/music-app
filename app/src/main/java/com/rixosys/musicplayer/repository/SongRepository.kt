package com.rixosys.musicplayer.repository

import android.content.Context
import com.rixosys.musicplayer.R
import com.rixosys.musicplayer.model.Song
import java.lang.reflect.Field

class SongRepository(private val context: Context) {

    fun getAllSongs(): List<Song> {
        val rawClass: Class<*> = R.raw::class.java
        val fields: Array<Field> = rawClass.fields

        return fields.mapNotNull { field ->
            try {
                val songId = field.getInt(null)
                val title = context.resources.getResourceEntryName(songId).replace("_", " ")
                Song(id = songId, title = title)
            } catch (e: Exception) {
                null
            }
        }
    }
}
