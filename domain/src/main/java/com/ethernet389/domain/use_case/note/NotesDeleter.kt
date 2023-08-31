package com.ethernet389.domain.use_case.note

import com.ethernet389.domain.model.note.Note
import com.ethernet389.domain.repository.NoteRepository

class NotesDeleter(
    private val noteRepository: NoteRepository
) {
    suspend fun deleteNote(note: Note): Boolean {
        return noteRepository.deleteNote(note)
    }
}