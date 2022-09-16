package com.bhaskar.corvasnotes.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bhaskar.corvasnotes.R;
import com.bhaskar.corvasnotes.adapters.NotesAdapter;
import com.bhaskar.corvasnotes.database.NotesDatabase;
import com.bhaskar.corvasnotes.entities.Note;
import com.bhaskar.corvasnotes.listeners.NotesListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NotesListener {

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NotesAdapter notesAdapter;

    private int noteClickedPosition = -1;

    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(view -> startActivityForResult(
                new Intent(getApplicationContext(), CreateNoteActivity.class),
                REQUEST_CODE_ADD_NOTE
        ));

        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTE);
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode) {

            @SuppressLint("StaticFieldLeak")
            class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

                @Override
                protected List<Note> doInBackground(Void... voids) {
                    return NotesDatabase
                            .getDatabase(getApplicationContext())
                            .noteDao().getAllNotes();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                protected void onPostExecute(List<Note> notes) {
                    super.onPostExecute(notes);
//                    if (noteList.size() == 0) {
//                        noteList.addAll(notes);
//                        notesAdapter.notifyDataSetChanged();
//                    } else {
//                        noteList.add(0, notes.get(0));
//                        notesAdapter.notifyItemInserted(0 );
//                    }
//                    notesRecyclerView.smoothScrollToPosition(0);

                    if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                        noteList.addAll(notes);
                        notesAdapter.notifyDataSetChanged();
                    } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                        noteList.add(0, notes.get(0));
                        notesAdapter.notifyItemInserted( 0 );
                        notesRecyclerView.smoothScrollToPosition(0);
                    } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                        noteList.remove(noteClickedPosition);
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        notesAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
            new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE);
        }else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE);
            }
        }
    }

    public int getNoteClickedPosition() {
        return noteClickedPosition;
    }

    public void setNoteClickedPosition(int noteClickedPosition) {
        this.noteClickedPosition = noteClickedPosition;
    }
}
