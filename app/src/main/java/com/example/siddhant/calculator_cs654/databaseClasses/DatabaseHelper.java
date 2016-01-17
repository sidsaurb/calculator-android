package com.example.siddhant.calculator_cs654.databaseClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by siddhant on 1/10/2016.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "history.db";
    private static final String TABLE_HISTORY = "history";


    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QUERY = "query";
    private static final String COLUMN_RESPONSE = "response";


    private static final int DATABASE_VERSION = 1;


    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    private static volatile DatabaseHelper mInstance = null;

    public static synchronized DatabaseHelper getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TRIGGER_TABLE = "CREATE TABLE " +
                TABLE_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_QUERY
                + " TEXT," + COLUMN_RESPONSE + " TEXT" + ")";
        db.execSQL(CREATE_TRIGGER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPair(DatabaseClass chain) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUERY, chain.query);
        values.put(COLUMN_RESPONSE, chain.response);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_HISTORY, null, values);
    }

    public ArrayList<DatabaseClass> getAllHistory() {
        String query = "Select * FROM " + TABLE_HISTORY + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<DatabaseClass> myArray = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                String query1 = cursor.getString(1);
                String response = cursor.getString(2);
                DatabaseClass myHistory = new DatabaseClass(query1, response);
                myArray.add(myHistory);
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return myArray;
    }

    public boolean deleteHistory() {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_HISTORY);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
