package com.s23010381.lifemate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WaterLogDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "WaterLogDB";
    private static final String TABLE_NAME = "water_logs";
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_AMOUNT = "amount";

    public WaterLogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public WaterLogDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DATE + " TEXT, " +
                COL_AMOUNT + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addWaterLog(int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_DATE, getCurrentDate());
        values.put(COL_AMOUNT, amount);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public int getTodaysWaterTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_NAME + " WHERE " + COL_DATE + " = ?";
        String today = getCurrentDate();
        Cursor cursor = db.rawQuery(query, new String[]{today});
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    public boolean resetTodaysWater() {
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

