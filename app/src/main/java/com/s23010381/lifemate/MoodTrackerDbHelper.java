package com.s23010381.lifemate;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MoodTrackerDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MoodTrackerDB";
    private static final String TABLE_NAME = "mood_logs";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_MOOD = "mood";
    private static final String COL_NOTES = "notes";

    public MoodTrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_MOOD + " TEXT, " +
                COL_NOTES + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean logMood(String mood, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, getCurrentDate());
        values.put(COL_MOOD, mood);
        values.put(COL_NOTES, notes);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public String getTodaysMood() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getCurrentDate();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_MOOD, COL_NOTES},
                COL_DATE + " = ?", new String[]{today}, null, null, null);
        if (cursor.moveToFirst()) {
            String mood = cursor.getString(0);
            String notes = cursor.getString(1);
            if (notes != null && !notes.isEmpty()) {
                mood += " (" + notes + ")";
            }
            cursor.close();
            return mood;
        }
        cursor.close();
        return null;
    }

    public boolean resetTodaysMood() {
        SQLiteDatabase db = this.getWritableDatabase();
        String today = getCurrentDate();
        int result = db.delete(TABLE_NAME, COL_DATE + " = ?", new String[]{today});
        return result > 0;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }
}
