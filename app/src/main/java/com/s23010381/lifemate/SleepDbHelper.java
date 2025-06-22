package com.s23010381.lifemate;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SleepDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SleepLogDB";
    private static final String TABLE_NAME = "sleep_logs";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_HOURS = "hours";
    private static final String COL_NOTES = "notes";

    public SleepDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_HOURS + " REAL, " +
                COL_NOTES + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addSleepLog(double hours, String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, getCurrentDate());
        values.put(COL_HOURS, hours);
        values.put(COL_NOTES, notes);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public double getTodaysSleepTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_HOURS + ") FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ?";
        String today = getCurrentDate();
        Cursor cursor = db.rawQuery(query, new String[]{today});
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        return total;
    }

    public String getTodaysSleepNotes() {
        SQLiteDatabase db = this.getReadableDatabase();
        String today = getCurrentDate();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COL_NOTES}, COL_DATE + " = ?", new String[]{today}, null, null, null);
        StringBuilder notesBuilder = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                String note = cursor.getString(0);
                if (note != null && !note.isEmpty()) {
                    if (notesBuilder.length() > 0) notesBuilder.append("; ");
                    notesBuilder.append(note);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return notesBuilder.toString();
    }

    public boolean resetTodaysSleep() {
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

