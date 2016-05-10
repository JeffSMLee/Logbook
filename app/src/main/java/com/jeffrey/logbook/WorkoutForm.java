package com.jeffrey.logbook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffrey.logbook.DB.DBAccessor;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

public class WorkoutForm extends AppCompatActivity {

    private ExpandableListView exerciseList;
    private WorkoutListAdapter exerciseAdapter;

    private DBAccessor dbAccessor;

    private String date;

    private HashMap<String, List<Set>> sets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_form);

        dbAccessor = new DBAccessor(this);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");


        exerciseList = (ExpandableListView) findViewById(R.id.lvExercises);

        ArrayList<String> exercises = new ArrayList<String>();
        sets = new HashMap<>();

        dbAccessor.retrieveWorkout(date, exercises, sets);

        exerciseAdapter = new WorkoutListAdapter(this, exercises, sets, exerciseList);
        exerciseList.setAdapter(exerciseAdapter);


        final EditText newExerciseName = (EditText) findViewById(R.id.etExerciseName);
        Button addExerciseButton = (Button) findViewById(R.id.btnAddExercise);
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = newExerciseName.getText().toString();
                exerciseAdapter.addExercise(exerciseName);
                newExerciseName.setText("");
                View view = WorkoutForm.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workout_form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_finish:
                dbAccessor.finishWorkout(sets, date);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}