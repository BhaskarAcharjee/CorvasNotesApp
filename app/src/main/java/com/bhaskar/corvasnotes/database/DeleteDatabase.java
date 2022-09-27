package com.bhaskar.corvasnotes.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bhaskar.corvasnotes.dao.NoteDao;
import com.bhaskar.corvasnotes.entities.Note;

    @Database(entities = Note.class, version = 1, exportSchema = false)
    public abstract class DeleteDatabase extends RoomDatabase {
        private static DeleteDatabase deleteDatabase;
        public static synchronized DeleteDatabase getDatabase(Context context) {
            if (deleteDatabase == null) {
                deleteDatabase = Room.databaseBuilder(context, DeleteDatabase.class,
                        "delete_db").build();
            }
            return deleteDatabase;
        }
        public abstract NoteDao noteDao();
    }