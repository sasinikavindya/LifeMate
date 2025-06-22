package com.s23010381.lifemate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDbHelper extends SQLiteOpenHelper {
    private static UserDbHelper instance;
    private SQLiteDatabase database;
    private static final String DATABASE_NAME = "LifeMateDB";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    // Create table query
    private static final String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL,"
            + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
            + COLUMN_PASSWORD + " TEXT NOT NULL"
            + ")";

    private UserDbHelper(Context context) {
        super(context.getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized UserDbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new UserDbHelper(context);
        }
        return instance;
    }

    private synchronized SQLiteDatabase getOpenDatabase() {
        if (database == null || !database.isOpen()) {
            database = getWritableDatabase();
        }
        return database;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_USERS_TABLE);
            Log.d("Database", "Database created successfully");
        } catch (Exception e) {
            Log.e("Database", "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        } catch (Exception e) {
            Log.e("Database", "Error upgrading database", e);
        }
    }

    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = getOpenDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_EMAIL, email);
            values.put(COLUMN_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e("Database", "Error registering user", e);
            return false;
        }
    }

    public boolean checkLogin(String email, String password) {
        SQLiteDatabase db = getOpenDatabase();
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_ID};
            String selection = COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
            String[] selectionArgs = {email, password};

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                    null, null, null);
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("Database", "Error checking login", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Don't close the database in normal operations
    @Override
    public synchronized void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
        database = null;
        super.close();
    }
}