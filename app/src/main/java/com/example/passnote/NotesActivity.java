package com.example.passnote;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<Note> notesList;

    private NotesDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbHelper = new NotesDatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        notesList = new ArrayList<>();
        adapter = new NotesAdapter(notesList, this, dbHelper); // Pass all three parameters
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNoteDialog();
            }
        });

        loadNotes(); // Call the method to load notes from the database
    }


    private void loadNotes() {
        Cursor cursor = dbHelper.getAllNotes();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COLUMN_ID);
            int titleIndex = cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COLUMN_TITLE);
            int contentIndex = cursor.getColumnIndexOrThrow(NotesDatabaseHelper.COLUMN_CONTENT);
            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIndex);
                String title = cursor.getString(titleIndex);
                String content = cursor.getString(contentIndex);
                Note note = new Note((int) id, title, content);
                notesList.add(note);
            }
            cursor.close();
            adapter.notifyDataSetChanged();
        }
    }


    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        final EditText titleEditText = view.findViewById(R.id.titleEditText);
        final EditText contentEditText = view.findViewById(R.id.contentEditText);
        builder.setView(view);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();
                long id = dbHelper.addNote(title, content);
                Note note = new Note((int) id, title, content);
                notesList.add(note);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }

    private void showEditNoteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_note, null);
        final EditText titleEditText = view.findViewById(R.id.titleEditText);
        final EditText contentEditText = view.findViewById(R.id.contentEditText);
        titleEditText.setText(note.getTitle());
        contentEditText.setText(note.getContent());
        builder.setView(view);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            private Note updatedNote;

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String title = titleEditText.getText().toString().trim();
                String content = contentEditText.getText().toString().trim();

                // Retrieve the note that the user wants to edit
                Note noteToUpdate = dbHelper.getNoteById(note.getId());

                // Update the note with the new data
                noteToUpdate.setTitle(title);
                noteToUpdate.setContent(content);

                // Update the note in the database
                dbHelper.updateNote(noteToUpdate);

                // Update the note in the adapter
                int position = notesList.indexOf(note);
                notesList.set(position, noteToUpdate);
                adapter.notifyItemChanged(position);

                // Copy note content to clipboard
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Note Content", note.getContent());
                clipboardManager.setPrimaryClip(clipData);

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }


    private void showDeleteNoteDialog(final Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Note");
        builder.setMessage("Are you sure you want to delete this note?");

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dbHelper.deleteNote(note.getId());
                notesList.remove(note);
                adapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.show();
    }
    private void clearNotesList() {
        notesList.clear();
        adapter.notifyDataSetChanged();
    }

}