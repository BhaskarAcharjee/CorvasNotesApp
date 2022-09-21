package com.bhaskar.corvasnotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.airbnb.lottie.LottieAnimationView;
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

    boolean isSwitchOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =  findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


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

        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (noteList.size() != 0){
                    notesAdapter.searchNotes(editable.toString());
                    findViewById(R.id.clearSearch).setVisibility(View.VISIBLE);
                }
            }
        });

//        Set Preference for light Dark Mode (Previous Selection)
        final SharedPreferences appSettingsPrefs = getSharedPreferences("App Settings",0);
        final SharedPreferences.Editor sharedPrefsEdit = appSettingsPrefs.edit();
        final boolean isNightModeOn = appSettingsPrefs.getBoolean("Night Mode",true);

        if (isNightModeOn){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        //        Day Night Mode switch
        final LottieAnimationView lottieSwitchButton = findViewById(R.id.lottieSwitchButton);
        lottieSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSwitchOn) {
                    lottieSwitchButton.setMinAndMaxProgress(0.5f, 1.0f); // Light Mode To Dark Mode animation
                    lottieSwitchButton.playAnimation();
                    isSwitchOn = false;
                } else {
                    lottieSwitchButton.setMinAndMaxProgress(0.0f, 0.4f); // Dark Mode To Light Mode animation
                    lottieSwitchButton.playAnimation();
                    isSwitchOn = true;
                }
//                if (isNightModeOn) {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                    sharedPrefsEdit.putBoolean("Night Mode",false);
//                    sharedPrefsEdit.apply();
//                } else {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                    sharedPrefsEdit.putBoolean("Night Mode",true);
//                    sharedPrefsEdit.apply();
//                }

            }
        });

//        Clear Search Box Text
//        if (inputSearch.getText()!=null){
//            findViewById(R.id.clearSearch).setVisibility(View.VISIBLE);
//        }
        ImageView clearSearch = findViewById(R.id.clearSearch);
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearch.getText().clear();
                clearSearch.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(),CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {

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

                    if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                        noteList.addAll(notes);
                        notesAdapter.notifyDataSetChanged();
                    } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                        noteList.add(0, notes.get(0));
                        notesAdapter.notifyItemInserted( 0 );
                        notesRecyclerView.smoothScrollToPosition(0);
                    } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                        noteList.remove(noteClickedPosition);

                        if (isNoteDeleted){
                            notesAdapter.notifyItemRemoved(noteClickedPosition);
                        }else {
                            noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                            notesAdapter.notifyItemChanged(noteClickedPosition);
                        }
                    }
                }
            }
            new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
    super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK){
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        }else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK){
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted",false));
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
