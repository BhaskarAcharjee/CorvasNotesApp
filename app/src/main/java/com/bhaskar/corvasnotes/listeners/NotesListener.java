package com.bhaskar.corvasnotes.listeners;

import com.bhaskar.corvasnotes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
