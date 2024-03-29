package com.bhaskar.corvasnotes.note;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bhaskar.corvasnotes.R;
import com.bhaskar.corvasnotes.database.DeleteDatabase;
import com.bhaskar.corvasnotes.database.NotesDatabase;
import com.bhaskar.corvasnotes.entities.Note;
import com.bhaskar.corvasnotes.view.NemosoftsText.NemosoftsEditText;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNoteActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle;
    private TextView textDateTime;
    private ImageView imageNote,imageRemoveSubtitle;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;
    private NemosoftsEditText inputNoteText;
    private ImageButton bold, italic, underline, strikethrough, bullet, quote, clear;

    private View viewSubtitleIndicator, textMiscellaneous;
    private String selectedNoteColor;
    private String selectedImagePath;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;

    private Note alreadyAvailableNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> onBackPressed());

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = (NemosoftsEditText) findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        textMiscellaneous = findViewById(R.id.textMiscellaneous);
        imageNote = findViewById(R.id.imageNote);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

        bold = (ImageButton) findViewById(R.id.bold);
        italic = (ImageButton) findViewById(R.id.italic);
        underline = (ImageButton) findViewById(R.id.underline);
        strikethrough = (ImageButton) findViewById(R.id.strikethrough);
        bullet = (ImageButton) findViewById(R.id.bullet);
        quote = (ImageButton) findViewById(R.id.quote);
        clear = (ImageButton) findViewById(R.id.clear);


        inputNoteText.setSelection(inputNoteText.getEditableText().length());

        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", Locale.getDefault())
                .format(new Date())
        );

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(view -> saveNote());

        selectedNoteColor = "#333333";
        selectedImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate",false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });
            findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageNote.setImageBitmap(null);
                    imageNote.setVisibility(View.GONE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                    selectedImagePath = "";
                }
            });
        findViewById(R.id.imageRemoveSubtitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteSubtitle.setText(null);
                inputNoteSubtitle.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveSubtitle).setVisibility(View.GONE);
            }
        });

        initMiscellaneous();
        setSubtitleIndicatorColor();

//        Miscellaneous Buttons Activity
        bold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.bold(!inputNoteText.contains(NemosoftsEditText.FORMAT_BOLD));
            }
        });
        bold.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_bold, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        italic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.italic(!inputNoteText.contains(NemosoftsEditText.FORMAT_ITALIC));
            }
        });
        italic.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_italic, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        underline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.underline(!inputNoteText.contains(NemosoftsEditText.FORMAT_UNDERLINED));
            }
        });
        underline.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_underline, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        strikethrough.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.strikethrough(!inputNoteText.contains(NemosoftsEditText.FORMAT_STRIKETHROUGH));
            }
        });
        strikethrough.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        bullet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.bullet(!inputNoteText.contains(NemosoftsEditText.FORMAT_BULLET));
            }
        });
        bullet.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_bullet, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.quote(!inputNoteText.contains(NemosoftsEditText.FORMAT_QUOTE));
            }
        });
        quote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_quote, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNoteText.clearFormats();
            }
        });
        clear.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(CreateNoteActivity.this, R.string.toast_format_clear, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.fromHtml(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());

        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }
        if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            findViewById(R.id.imageRemoveWebURL).setVisibility(View.VISIBLE);
            layoutWebURL.setVisibility(View.VISIBLE);
        }
        if (alreadyAvailableNote.getSubtitle() != null && !alreadyAvailableNote.getSubtitle().trim().isEmpty()) {
            inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
            inputNoteSubtitle.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveSubtitle).setVisibility(View.VISIBLE);
            viewSubtitleIndicator.setVisibility(View.VISIBLE);
        }
    }

    private void saveNote() {

//        showing Warning Notes Cann't be Empty
//        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
//            Toast.makeText(this, "Note title can't be empty!", Toast.LENGTH_SHORT).show();
//            return;
//        } else if (inputNoteText.getText().toString().trim().isEmpty()) {
//            Toast.makeText(this, "Note can't be empty!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.toHtml());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        if (layoutWebURL.getVisibility() == View.VISIBLE) {
            note.setWebLink(textWebURL.getText().toString());
        }

        if (alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
        }


        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void avoid) {
                super.onPostExecute(avoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

//    Miscellaneous Activity
    private void initMiscellaneous() {
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);

        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

//        Note Color Change According Selections
        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#3A52Fc";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#4CAF50";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndicatorColor();
            }
        });

//        Fetch Already available Note color
        if (alreadyAvailableNote != null && alreadyAvailableNote.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            switch (alreadyAvailableNote.getColor()) {
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewColor2).performClick();
                    break;

                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewColor3).performClick();
                    break;

                case "#3A52Fc":
                    layoutMiscellaneous.findViewById(R.id.viewColor4).performClick();
                    break;

                case "#4CAF50":
                    layoutMiscellaneous.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

//        Miscellaneous Activity : ADD IMAGE
        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });
//        Miscellaneous Activity : Show URL Dialog
        layoutMiscellaneous.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();
            }
        });
//        Miscellaneous Activity : ADD SUBTITLE
        layoutMiscellaneous.findViewById(R.id.layoutAddSubtitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                viewSubtitleIndicator.setVisibility(View.VISIBLE);
                inputNoteSubtitle.setVisibility(View.VISIBLE);
            }
        });

        if (alreadyAvailableNote != null) {
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }

    }
        private void showDeleteNoteDialog() {
            if (dialogDeleteNote == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
                View view = LayoutInflater.from(this).inflate(
                        R.layout.layout_delete_note_move, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
                );
                builder.setView(view);
                dialogDeleteNote = builder.create();
                if (dialogDeleteNote.getWindow() != null){
                    dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        @SuppressLint("StaticFieldLeak")
                        class DeleteNoteTask extends AsyncTask<Void, Void, Void>
                        {

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                deleteSaveNote();
                            }

                            @Override
                            protected Void doInBackground(Void... voids) {
                                NotesDatabase.getDatabase(getApplicationContext()).noteDao()
                                        .deleteNote(alreadyAvailableNote);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                Intent intent = new Intent();
                                intent.putExtra("isNoteDeleted",true);
                                setResult(RESULT_OK, intent);
                                dialogDeleteNote.dismiss();
                                finish();
                            }
                        }

                        new DeleteNoteTask().execute();
                    }
                });

                view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogDeleteNote.dismiss();
                    }
                });
            }

            dialogDeleteNote.show();
        }

    private void deleteSaveNote() {
        final Note note = new  Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNoteText(inputNoteText.toHtml());
        note.setDateTime(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);

        if (layoutWebURL.getVisibility() == View.VISIBLE){
            note.setWebLink(textWebURL.getText().toString());
        }

        if (alreadyAvailableNote != null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class  DeleteSaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DeleteDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }
        new DeleteSaveNoteTask().execute();
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
        GradientDrawable gradientDrawable2 = (GradientDrawable) textMiscellaneous.getBackground();
        gradientDrawable2.setColor(Color.parseColor(selectedNoteColor));
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);

                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null);

        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }

        return filePath;
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_add_url,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();

                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }

//    Note Save on BackPressed
    @Override
    public void onBackPressed(){
        if (inputNoteTitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()) {
            super.onBackPressed();
        } else {
            saveNote();
        }
    }

//    On Click Button
//    public void formatBold(View view) {
//        Spannable spannableString = new SpannableStringBuilder(inputNoteText.getText());
//        spannableString.setSpan(new StyleSpan(Typeface.BOLD),
//                inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
//        inputNoteText.setText(spannableString);
//    }
//
//    public void formatItalic(View view) {
//        Spannable spannableString = new SpannableStringBuilder(inputNoteText.getText());
//        spannableString.setSpan(new StyleSpan(Typeface.ITALIC),
//                inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
//        inputNoteText.setText(spannableString);
//    }
//
//    public void formatUnderline(View view) {
//        Spannable spannableString = new SpannableStringBuilder(inputNoteText.getText());
//        spannableString.setSpan(new UnderlineSpan(),
//                inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
//        inputNoteText.setText(spannableString);
//    }
//
//    public void formatStrikethrough(View view) {
//        Spannable spannableString = new SpannableStringBuilder(inputNoteText.getText());
//        spannableString.setSpan(new StrikethroughSpan(),
//                inputNoteText.getSelectionStart(),inputNoteText.getSelectionEnd(),0);
//        inputNoteText.setText(spannableString);
//    }
//
//    public void formatClear(View view) {
//        String clearFormatText = inputNoteText.getText().toString();
//        inputNoteText.setText(clearFormatText);
//    }
//
//    public void formatBullet(View view) {
//    }

}