package com.ethernet389.domain.use_case.note

import com.ethernet389.domain.model.note.BaseNote
import com.ethernet389.domain.repository.NoteRepository

class NotesCreator(
    private val noteRepository: NoteRepository
) {
    suspend fun createNote(note: BaseNote): Boolean {
        return noteRepository.createNote(note)
    }
}