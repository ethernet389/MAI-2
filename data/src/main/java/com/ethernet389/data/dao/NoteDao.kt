package com.ethernet389.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ethernet389.data.model.DataNote

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertNote(dataNote: DataNote): Long

    @Query("SELECT * FROM notes;")
    fun getAllNotes(): List<DataNote>

    @Delete
    fun deleteNote(note: DataNote): Int

    @Query("DELETE FROM notes;")
    fun deleteAllNotes()
}