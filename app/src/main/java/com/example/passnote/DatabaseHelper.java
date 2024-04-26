package com.example.passnote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "password_manager.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "passwords";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SITE = "site";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_SITE + " TEXT NOT NULL, " +
                    COLUMN_USERNAME + " TEXT NOT NULL, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public final class NotesContract {
        private NotesContract() {}

        public static class NoteEntry implements BaseColumns {
            public static final String TABLE_NAME = "notes";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_CONTENT = "content";
        }

        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                        NoteEntry._ID + " INTEGER PRIMARY KEY," +
                        NoteEntry.COLUMN_NAME_TITLE + " TEXT," +
                        NoteEntry.COLUMN_NAME_CONTENT + " TEXT)";

        public static final String SQL_DROP_TABLE =
                "DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Not needed for this example implementation
    }
    public void deletePassword(String site, String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_SITE + " = ? AND " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { site, username, password };
        db.delete(TABLE_NAME, selection, selectionArgs);
        db.close();
    }



}

