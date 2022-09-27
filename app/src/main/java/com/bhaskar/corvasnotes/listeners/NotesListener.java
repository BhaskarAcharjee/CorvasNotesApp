package com.bhaskar.corvasnotes.listeners;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.bhaskar.corvasnotes.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
