package com.example.sickcare_application;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "sickcare.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_FAVORIS = "favoris";
    public static final String COLUMN_ID = "id_recette";

    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_FAVORIS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY);";

    public FavoriDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORIS);
        onCreate(db);
    }
}
