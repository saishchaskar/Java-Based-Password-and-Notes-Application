package com.example.passnote2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditNoteActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;
    private Note note;
    private NotesDatabaseHelper dbHelper;
    private Button copyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        dbHelper = new NotesDatabaseHelper(this);



        // Find the EditText views
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);

        // Retrieve the Note object from the Intent
        note = getIntent().getParcelableExtra("note");

        // Check if the Note object is not null
        if (note != null) {
            // Set the EditText views to display the title and content of the Note object
            titleEditText.setText(note.getTitle());
            contentEditText.setText(note.getContent());
        } else {
            // Handle the case where the Note object is null
            Toast.makeText(this, "Note not found", Toast.LENGTH_SHORT).show();
            finish();
        }



        // Set up the Save button
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the note object with the new data
                note.setTitle(titleEditText.getText().toString());
                note.setContent(contentEditText.getText().toString());

                // Update the note in the database
                dbHelper.updateNote(note);

                // Return to the NotesActivity
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Button deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete the note from the database
                dbHelper.deleteNote(note.getId());

                // Return to the NotesActivity
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
