package com.jeffrey.logbook.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.jeffrey.logbook.Exercise;
import com.jeffrey.logbook.Set;

import java.sql.SQLInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeffrey on 5/3/2016.
 */
public class DBAccessor {

    private DBHelper helper;

    private Context c;

    public DBAccessor(Context c) {
        helper = new DBHelper(c);
        this.c = c;
    }

    public void finishWorkout(List<Exercise> exercises, HashMap<String, List<Set>> map, String date) {
        for(String name : map.keySet()) {
            if(map.get(name).size() == 0){
                Toast.makeText(c, "Cannot submit exercise with no completed sets", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SQLiteDatabase d = helper.getReadableDatabase();

        String[] projection = {
                DBHelper.WORKOUTS_HEADER_ID
        };
        String[] selectionArgs = {date};

        Cursor c = d.query(DBHelper.WORKOUTS_TABLE_NAME,
                projection,
                DBHelper.WORKOUTS_HEADER_DATE + " = ?",
                selectionArgs,
                null,
                null,
                null
        );

        if(c.getCount() != 0) {
            c.moveToFirst();
            while(true) {
                long id = c.getLong(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_ID));
                d.delete(DBHelper.SETS_TABLE_NAME, DBHelper.WORKOUTS_HEADER_ID + " = ?", new String[]{String.valueOf(id)});
                d.delete(DBHelper.WORKOUTS_TABLE_NAME, DBHelper.WORKOUTS_HEADER_ID + " = ?", new String[]{String.valueOf(id)});
                if(c.isLast()) {
                    break;
                }
                c.moveToNext();
            }
        }
        c.close();
        d.close();
        SQLiteDatabase db = helper.getWritableDatabase();

        for(Exercise e : exercises) {
            ContentValues exerciseValues = new ContentValues();
            exerciseValues.put(DBHelper.WORKOUTS_HEADER_NAME, e.getName());
            exerciseValues.put(DBHelper.WORKOUTS_HEADER_DATE, date);

            long id = db.insert(DBHelper.WORKOUTS_TABLE_NAME, null, exerciseValues);
            List<Set> sets = map.get(e.getName());
            for(int i = 0; i < sets.size() - 1; i++) {
                ContentValues setValues = new ContentValues();
                if(e.getInputs().contains(Exercise.Input.WEIGHT))
                    setValues.put(DBHelper.SETS_HEADER_WEIGHT, sets.get(i).getWeight());
                if(e.getInputs().contains(Exercise.Input.REPS))
                    setValues.put(DBHelper.SETS_HEADER_REPS, sets.get(i).getReps());
                if(e.getInputs().contains(Exercise.Input.TIME))
                    setValues.put(DBHelper.SETS_HEADER_TIME, sets.get(i).getTime());
                if(e.getInputs().contains(Exercise.Input.DISTANCE))
                    setValues.put(DBHelper.SETS_HEADER_DISTANCE, sets.get(i).getDistance());
                setValues.put(DBHelper.WORKOUTS_HEADER_ID, id);
                db.insert(DBHelper.SETS_TABLE_NAME, null, setValues);
            }
        }
        db.close();
        helper.close();
    }

    public void retrieveWorkout(String date, List<Exercise> exercises, HashMap<String, List<Set>> sets) {
        SQLiteDatabase db = helper.getReadableDatabase();

        String[] projection = {
                DBHelper.WORKOUTS_HEADER_ID,
                DBHelper.WORKOUTS_HEADER_NAME
        };
        String[] selectionArgs = new String[]{date};
        Cursor c = db.query(DBHelper.WORKOUTS_TABLE_NAME,
                projection,
                DBHelper.WORKOUTS_HEADER_DATE + " = ?",
                selectionArgs,
                null,
                null,
                DBHelper.WORKOUTS_HEADER_ID + " ASC");
        if(c.getCount() != 0 ) {
            c.moveToFirst();
            while(true) {
                String name = c.getString(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_NAME));
                long id = c.getLong(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_ID));
                String[] projection2 = {
                        DBHelper.SETS_HEADER_WEIGHT,
                        DBHelper.SETS_HEADER_REPS,
                        DBHelper.SETS_HEADER_TIME,
                        DBHelper.SETS_HEADER_DISTANCE
                };
                Cursor c2 = db.query(DBHelper.SETS_TABLE_NAME,
                        projection2,
                        DBHelper.WORKOUTS_HEADER_ID + " = ?",
                        new String[]{String.valueOf(id)},
                        null,
                        null,
                        DBHelper.SETS_HEADER_ID + " ASC"
                );
                ArrayList<Set> list = new ArrayList<>();
                c2.moveToFirst();
                List<Exercise.Input> inputs = new ArrayList<>();
                for(int i = 0; i < c2.getCount(); i++) {
                    Set s = new Set();
                    if(!c2.isNull(c2.getColumnIndex(DBHelper.SETS_HEADER_WEIGHT))){
                        s.setWeight(c2.getDouble(c2.getColumnIndex(DBHelper.SETS_HEADER_WEIGHT)));
                        if(i == 0)
                            inputs.add(Exercise.Input.WEIGHT);
                    }
                    if(!c2.isNull(c2.getColumnIndex(DBHelper.SETS_HEADER_REPS))) {
                        s.setReps(c2.getInt(c2.getColumnIndex(DBHelper.SETS_HEADER_REPS)));
                        if(i == 0)
                            inputs.add(Exercise.Input.REPS);
                    }
                    if(!c2.isNull(c2.getColumnIndex(DBHelper.SETS_HEADER_TIME))) {
                        s.setTime(c2.getString(c2.getColumnIndex(DBHelper.SETS_HEADER_TIME)));
                        if(i == 0)
                            inputs.add(Exercise.Input.TIME);
                    }
                    if(!c2.isNull(c2.getColumnIndex(DBHelper.SETS_HEADER_DISTANCE))) {
                        s.setDistance(c2.getDouble(c2.getColumnIndex(DBHelper.SETS_HEADER_DISTANCE)));
                        if(i == 0)
                            inputs.add(Exercise.Input.DISTANCE);
                    }
                    list.add(s);
                    c2.moveToNext();
                }
                list.add(list.size(), new Set());   //dummy
                sets.put(name, list);
                exercises.add(new Exercise(name, inputs));
                if(c.isLast()) {
                    break;
                }
                c2.close();
                c.moveToNext();
            }
        }
        c.close();
        db.close();
    }

    public String[] retrieveHistoricExercises() {
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(true, DBHelper.WORKOUTS_TABLE_NAME, new String[]{DBHelper.WORKOUTS_HEADER_NAME}, null, null, null, null, null, null);
        if(c.getCount() == 0)
            return new String[] {};
        String[] names  = new String[c.getCount()];
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            names[i] = c.getString(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_NAME));
            c.moveToNext();
        }
        c.close();
        db.close();
        return names;
    }

    public String retrieveAllExercisesAsString(String delimiter) {
        String s = "";

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(DBHelper.WORKOUTS_TABLE_NAME,
                new String[]{DBHelper.WORKOUTS_HEADER_ID, DBHelper.WORKOUTS_HEADER_NAME, DBHelper.WORKOUTS_HEADER_DATE}, null, null,
                null,
                null,
                DBHelper.WORKOUTS_HEADER_ID + " ASC");
        if(c.getCount() == 0)
            return s;
        c.moveToFirst();
        for(int i = 0; i < c.getCount(); i++) {
            String date = c.getString(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_DATE));
            String name = c.getString(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_NAME));
            Cursor d = db.query(DBHelper.SETS_TABLE_NAME,
                    new String[]{DBHelper.SETS_HEADER_WEIGHT, DBHelper.SETS_HEADER_REPS, DBHelper.SETS_HEADER_TIME, DBHelper.SETS_HEADER_DISTANCE},
                    DBHelper.WORKOUTS_HEADER_ID + " = ?",
                    new String[]{c.getString(c.getColumnIndex(DBHelper.WORKOUTS_HEADER_ID))},
                    null,
                    null,
                    DBHelper.SETS_HEADER_ID + " ASC");
            d.moveToFirst();
            for(int j = 0; j < d.getCount(); j++) {
                s += date + delimiter + name;
                if(!d.isNull(d.getColumnIndex(DBHelper.SETS_HEADER_WEIGHT)))
                    s += delimiter + d.getDouble(d.getColumnIndex(DBHelper.SETS_HEADER_WEIGHT));
                else
                    s += delimiter + "-";
                if(!d.isNull(d.getColumnIndex(DBHelper.SETS_HEADER_REPS)))
                    s += delimiter + d.getInt(d.getColumnIndex(DBHelper.SETS_HEADER_REPS));
                else
                    s += delimiter + "-";
                if(!d.isNull(d.getColumnIndex(DBHelper.SETS_HEADER_TIME)))
                    s += delimiter + d.getInt(d.getColumnIndex(DBHelper.SETS_HEADER_TIME));
                else
                    s += delimiter + "-";
                if(!d.isNull(d.getColumnIndex(DBHelper.SETS_HEADER_DISTANCE)))
                    s += delimiter + d.getInt(d.getColumnIndex(DBHelper.SETS_HEADER_DISTANCE));
                else
                    s += delimiter + "-";
                s += "\n";
                d.moveToNext();
            }
            d.close();
            c.moveToNext();
        }
        c.close();
        db.close();
        return s;
    }
}
