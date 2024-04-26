package com.example.passnote;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
public class EditPasswordActivity extends AppCompatActivity {

    // Declare views and database helper
    private EditText siteEditText, usernameEditText, passwordEditText;
    private Button saveButton;
    private SQLiteDatabase db;


    // Declare variables to hold the selected password's values
    private String site, username, password;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Initialize views
        siteEditText = findViewById(R.id.siteEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);

        // Get the selected password from the intent
        site = getIntent().getStringExtra("site");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        // Set the text of the EditTexts to the selected password's values
        siteEditText.setText(site);
        usernameEditText.setText(username);
        passwordEditText.setText(password);

        // Initialize the database helper
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();

        // Set click listener for Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String site = siteEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Get the old password data from the intent
                String oldSite = getIntent().getStringExtra("site");
                String oldUsername = getIntent().getStringExtra("username");
                String oldPassword = getIntent().getStringExtra("password");

                // Update the password in the database
                updatePassword(oldSite, oldUsername, oldPassword, site, username, password);

                // Log the values to see if the update is successful
                Log.d("EditPasswordActivity", "Site: " + site);
                Log.d("EditPasswordActivity", "Username: " + username);
                Log.d("EditPasswordActivity", "Password: " + password);
                Log.d("EditPasswordActivity", "Old Site: " + oldSite);
                Log.d("EditPasswordActivity", "Old Username: " + oldUsername);
                Log.d("EditPasswordActivity", "Old Password: " + oldPassword);

                // Close the activity
                finish();
            }
        });

    }
        private void updatePassword(String oldSite, String oldUsername, String oldPassword, String newSite, String newUsername, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_SITE, newSite);
        values.put(DatabaseHelper.COLUMN_USERNAME, newUsername);
        values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);
        String selection = DatabaseHelper.COLUMN_SITE + " = ? AND " + DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {oldSite, oldUsername, oldPassword};
        db.update(DatabaseHelper.TABLE_NAME, values, selection, selectionArgs);
    }
}
