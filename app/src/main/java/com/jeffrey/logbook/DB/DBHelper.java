package com.jeffrey.logbook.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jeffrey on 4/25/2016.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "logbook.db";

    public static final String WORKOUTS_TABLE_NAME = "Workouts";
    public static final String WORKOUTS_HEADER_ID = "W_Id";
    public static final String WORKOUTS_HEADER_NAME = "WorkoutName";
    public static final String WORKOUTS_HEADER_DATE = "Date";
    public static final String CREATE_ENTRIES_WORKOUTS = "CREATE TABLE " + WORKOUTS_TABLE_NAME + "( "
            + WORKOUTS_HEADER_ID + " INTEGER PRIMARY KEY, "
            + WORKOUTS_HEADER_NAME + " TEXT, "
            + WORKOUTS_HEADER_DATE + " TEXT);";

    public static final String SETS_TABLE_NAME = "Sets";
    public static final String SETS_HEADER_ID = "S_Id";
    public static final String SETS_HEADER_WEIGHT = "Weight";
    public static final String SETS_HEADER_REPS = "Reps";
    public static final String SETS_HEADER_TIME = "Time";
    public static final String SETS_HEADER_DISTANCE = "Distance";
    public static final String CREATE_ENTRIES_SETS = "CREATE TABLE " + SETS_TABLE_NAME + "( "
            + SETS_HEADER_ID + " INTEGER PRIMARY KEY, "
            + SETS_HEADER_WEIGHT + " REAL, "
            + SETS_HEADER_REPS + " INTEGER, "
            + SETS_HEADER_TIME + " TEXT, "
            + SETS_HEADER_DISTANCE + " REAL, "
            + WORKOUTS_HEADER_ID + " INTEGER, "
            + "FOREIGN KEY(" + WORKOUTS_HEADER_ID + ") REFERENCES " + WORKOUTS_TABLE_NAME + "(" + WORKOUTS_HEADER_ID + "));";

    private static final String DATABASE_ADD_TIME = "ALTER TABLE " + SETS_TABLE_NAME + " ADD COLUMN " + SETS_HEADER_TIME + " TEXT;";
    private static final String DATABASE_ADD_DISTANCE = "ALTER TABLE " + SETS_TABLE_NAME + " ADD COLUMN " + SETS_HEADER_DISTANCE + " REAL;";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ENTRIES_WORKOUTS);
        db.execSQL(CREATE_ENTRIES_SETS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DATABASE_ADD_TIME);
            db.execSQL(DATABASE_ADD_DISTANCE);
        }
    }
}
