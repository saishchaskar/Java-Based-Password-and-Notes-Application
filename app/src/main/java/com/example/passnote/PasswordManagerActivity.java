package com.example.passnote;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PasswordManagerActivity extends AppCompatActivity {

    private EditText siteEditText, usernameEditText, passwordEditText;
    private Button submitButton;
    private ListView passwordListView;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);

        // Initialize views
        siteEditText = findViewById(R.id.siteEditText);
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        submitButton = findViewById(R.id.submitButton);
        passwordListView = findViewById(R.id.passwordListView);

        // Initialize adapter for password list view
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        db = databaseHelper.getWritableDatabase();
        final List<Password> passwordList = getPasswordList();
        final PasswordAdapter passwordAdapter = new PasswordAdapter(this, passwordList);
        passwordListView.setAdapter(passwordAdapter);

        // Set onItemClickListener for password list view
        passwordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Password selectedPassword = (Password) passwordAdapter.getItem(position);

                // Create the menu items
                String[] menuItems = {"Edit", "Delete", "Copy Password", "Open Site"};

                // Create the dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PasswordManagerActivity.this);
                builder.setTitle(selectedPassword.getSite())
                        .setItems(menuItems, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0: // Edit
                                        Intent editIntent = new Intent(PasswordManagerActivity.this, EditPasswordActivity.class);
                                        editIntent.putExtra("site", selectedPassword.getSite());
                                        editIntent.putExtra("username", selectedPassword.getUsername());
                                        editIntent.putExtra("password", selectedPassword.getPassword());
                                        startActivity(editIntent);
                                        break;
                                    case 1: // Delete
                                        deletePassword(selectedPassword.getSite(), selectedPassword.getUsername(), selectedPassword.getPassword());
                                        break;
                                    case 2: // Copy Password
                                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                        ClipData clipData = ClipData.newPlainText("Password", selectedPassword.getPassword());
                                        clipboardManager.setPrimaryClip(clipData);
                                        Toast.makeText(PasswordManagerActivity.this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3: // Search Site
                                        String site = selectedPassword.getSite();
                                        Intent searchIntent = new Intent(Intent.ACTION_WEB_SEARCH);
                                        searchIntent.putExtra(SearchManager.QUERY, site);
                                        startActivity(searchIntent);
                                        break;

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });



        // Set click listener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Add new password to database and update list view
                String site = siteEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if (!site.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_SITE, site);
                    values.put(DatabaseHelper.COLUMN_USERNAME, username);
                    values.put(DatabaseHelper.COLUMN_PASSWORD, password);
                    db.insert(DatabaseHelper.TABLE_NAME, null, values);
                    passwordList.clear();
                    passwordList.addAll(getPasswordList());
                    passwordAdapter.notifyDataSetChanged();
                    siteEditText.setText("");
                    usernameEditText.setText("");
                    passwordEditText.setText("");
                } else {
                    Toast.makeText(PasswordManagerActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<Password> getPasswordList() {
        final List<Password> passwordList = new ArrayList<>();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            final int siteIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_SITE);
            final int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USERNAME);
            final int passwordIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_PASSWORD);
            do {
                final String site = cursor.getString(siteIndex);
                final String username = cursor.getString(usernameIndex);
                final String password = cursor.getString(passwordIndex);
                final Password passwordObj = new Password(site, username, password);
                passwordList.add(passwordObj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return passwordList;
    }

    private void deletePassword(String site, String username, String password) {
        String whereClause = DatabaseHelper.COLUMN_SITE + " = ? AND " + DatabaseHelper.COLUMN_USERNAME + " = ? AND " + DatabaseHelper.COLUMN_PASSWORD + " = ?";
        String[] whereArgs = {site, username, password};
        int rowsDeleted = db.delete(DatabaseHelper.TABLE_NAME, whereClause, whereArgs);
        if (rowsDeleted > 0) {
            Toast.makeText(PasswordManagerActivity.this, "Password deleted", Toast.LENGTH_SHORT).show();
            List<Password> passwordList = getPasswordList();
            PasswordAdapter passwordAdapter = (PasswordAdapter) passwordListView.getAdapter();
            passwordList.clear();
            passwordList.addAll(getPasswordList());
            passwordAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(PasswordManagerActivity.this, "Failed to delete password", Toast.LENGTH_SHORT).show();
        }
    }


}
