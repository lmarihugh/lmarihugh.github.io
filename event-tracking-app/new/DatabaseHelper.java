package com.example.cs360projectonelynneamarihugh;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AppDatabase.db";
    private static final int DATABASE_VERSION = 2;

    // User table
    public static final String TABLE_USERS;

    static {
        TABLE_USERS = "users";
    }

    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";
    public static final String COLUMN_SALT = "salt";

    // Events table
    public static final String COLUMN_USER = "username";
    public static final String TABLE_EVENTS = "events";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_DESC = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // user table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PASSWORD_HASH + " TEXT NOT NULL, " +
                COLUMN_SALT + " TEXT NOT NULL)");

        // Create events table
        db.execSQL("CREATE TABLE " + TABLE_EVENTS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT, " +
                COLUMN_DESC + " TEXT, " +
                COLUMN_USER + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_USER + ") REFERENCES " +
                TABLE_USERS + "(" + COLUMN_USERNAME + ") ON DELETE CASCADE)");

        db.execSQL("CREATE INDEX idx_events_username ON " +
                TABLE_EVENTS + "(" + COLUMN_USER + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            migrateToVersion2(db);
        }
    }

    private void migrateToVersion2(SQLiteDatabase db) {
        db.execSQL("ALTER TABLE " + TABLE_USERS + " RENAME TO users_old");
        db.execSQL("ALTER TABLE " + TABLE_EVENTS + " RENAME TO events_old");

        onCreate(db);

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_USERNAME + ", " + COLUMN_PASSWORD +
                        " FROM users_old", null);
        while (cursor.moveToNext()) {
            String username = cursor.getString(0);
            String oldPassword = cursor.isNull(1) ? "" : cursor.getString(1);
            String salt = PasswordHasher.generateSalt();
            db.execSQL("INSERT INTO " + TABLE_USERS + " (" + COLUMN_USERNAME + ", " +
                            COLUMN_PASSWORD_HASH + ", " + COLUMN_SALT + ") VALUES (?, ?, ?)",
                    new Object[]{username, PasswordHasher.hash(oldPassword, salt), salt});
        }
        cursor.close();

        String copyEvents = "INSERT INTO events "
                + "(id, title, date, time, description, username) "
                + "SELECT id, title, date, time, description, username "
                + "FROM events_old "
                + "WHERE username IN (SELECT username FROM users)";
        db.execSQL(copyEvents);

        db.execSQL("DROP TABLE events_old");
        db.execSQL("DROP TABLE users_old");
    }
}