package com.bhaskar.corvasnotes.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bhaskar.corvasnotes.R;
import com.bhaskar.corvasnotes.SharedPref.SharedPref;
import com.bhaskar.corvasnotes.adapters.NotesAdapter;
import com.bhaskar.corvasnotes.database.NotesDatabase;
import com.bhaskar.corvasnotes.entities.Note;
import com.bhaskar.corvasnotes.listeners.NotesListener;
import com.bhaskar.corvasnotes.note.CreateNoteActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

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

    private ImageButton buttonPopupMainOptions,buttonPopupViewOptions;
    Dialog dialogPopupMainOptions,dialogPopupViewOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar =  findViewById(R.id.bottomAppBar);
        setSupportActionBar(toolbar);
        setTitle(R.string.app_name);

//        Activity_main_drawer Menu Item Click
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_set :
                        ActivityCompat.startActivity(MainActivity.this, new Intent(MainActivity.this, SettingsActivity.class), null);
                        MainActivity.this.finish();
                        break;

                    case R.id.nav_delete :
                        ActivityCompat.startActivity(MainActivity.this, new Intent(MainActivity.this, DeleteActivity.class), null);
                        MainActivity.this.finish();
                        break;

                    default :
                        break;
                }
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

//        Expanded toolbar
        Toolbar toolbarMain = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbarMain);

        ImageView imageAddNoteMain = findViewById(R.id.imageAddNoteMain);
        imageAddNoteMain.setOnClickListener(view -> startActivityForResult(
                new Intent(getApplicationContext(), CreateNoteActivity.class),
                REQUEST_CODE_ADD_NOTE
        ));

//        Recycler View Notes
        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        notesRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        noteList = new ArrayList<>();
        notesAdapter = new NotesAdapter(noteList,this);
        notesRecyclerView.setAdapter(notesAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTE,false);
//      Search Notes
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
//        final SharedPreferences appSettingsPrefs = getSharedPreferences("App Settings",0);
//        final SharedPreferences.Editor sharedPrefsEdit = appSettingsPrefs.edit();
//        final boolean isNightModeOn = appSettingsPrefs.getBoolean("Night Mode",true);

        SharedPref sharedPref = new SharedPref(this);
        if (sharedPref.isNightModeOn()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

//      Clear Search content clicking Cross button
        ImageView clearSearch = findViewById(R.id.clearSearch);
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                inputSearch.getText().clear();
                clearSearch.setVisibility(View.GONE);
            }
        });

//      Clicking search Button Expand Search Bar
        ImageButton buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layoutSearch = findViewById(R.id.layoutSearch);
                if (layoutSearch.getVisibility()==View.VISIBLE){
                    layoutSearch.setVisibility(View.GONE);
                } else {
                    layoutSearch.setVisibility(View.VISIBLE);
                }
            }
        });

    //      Popup Main Options
        dialogPopupMainOptions = new Dialog(MainActivity.this);
        dialogPopupMainOptions.setContentView(R.layout.popup_main_options);
        dialogPopupMainOptions.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogPopupMainOptions.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        buttonPopupMainOptions = findViewById(R.id.buttonPopupMainOptions);
        buttonPopupMainOptions.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogPopupMainOptions.show();
            }
        });

        Window window = dialogPopupMainOptions.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.TOP;
        window.setAttributes(layoutParams);

        //      Popup View Options
        dialogPopupViewOptions = new Dialog(MainActivity.this);
        dialogPopupViewOptions.setContentView(R.layout.popup_main_options_view);
        dialogPopupViewOptions.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogPopupViewOptions.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        Window window1 = dialogPopupViewOptions.getWindow();
        WindowManager.LayoutParams layoutParams1 = window1.getAttributes();
        layoutParams1.gravity = Gravity.TOP;
        window1.setAttributes(layoutParams1);


    }
//  On Note Clicked Update Note
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

//    Add More Apps BottomSheet Expand
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        final LinearLayout layoutMoreApps = findViewById(R.id.layoutMoreApps);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMoreApps);

        // collapse clicking layoutMoreApps
        layoutMoreApps.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        switch (menuItem.getItemId()) {
            case R.id.quickActionProfile:
                ActivityCompat.startActivity(MainActivity.this, new Intent(MainActivity.this, AboutActivity.class), null);
//                MainActivity.this.finish();
                break;

//            case R.id.quickActionApps:
//                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }
//                break;
//
//            case R.id.quickActionAddOns:
//                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
// On Click Main pop up options
    public void clickSettingsNote(View view) {
        ActivityCompat.startActivity(MainActivity.this, new Intent(MainActivity.this, SettingsActivity.class), null);
        MainActivity.this.finish();
    }
     public void clickRecycleBin(View view) {
        ActivityCompat.startActivity(MainActivity.this, new Intent(MainActivity.this, DeleteActivity.class), null);
        MainActivity.this.finish();
    }
    public void clickViewNote(View view) {
        dialogPopupViewOptions.show();
        dialogPopupMainOptions.hide();
    }
    public void clickViewGridLarge(View view){
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
    }
    public void clickViewGridMedium(View view){
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL));
    }
    public void clickViewGridUniform(View view){
        notesRecyclerView.setLayoutManager(new GridLayoutManager(this,2));
    }
    public void clickViewList(View view){
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void setSketchUri(Uri uri) {
    }
}
