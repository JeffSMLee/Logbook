package com.jeffrey.logbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RadioButton;

import com.jeffrey.logbook.DB.DBAccessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutForm extends AppCompatActivity {

    private ExpandableListView exerciseList;
    private StrengthListAdapter exerciseAdapter;

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
        exerciseList.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);

        ArrayList<String> exercises = new ArrayList<String>();
        sets = new HashMap<>();

        dbAccessor.retrieveWorkout(date, exercises, sets);

        exerciseAdapter = new StrengthListAdapter(this, exercises, sets, exerciseList);
        exerciseList.setAdapter(exerciseAdapter);

        ListView cardioList = (ListView) findViewById(R.id.lvCardio);
        final CardioListAdapter cardioAdapter = new CardioListAdapter(this, new ArrayList<CardioExercise>());
        cardioList.setAdapter(cardioAdapter);

        final AutoCompleteTextView newExerciseName = (AutoCompleteTextView) findViewById(R.id.etExerciseName);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dbAccessor.retrieveHistoricExercises());
        newExerciseName.setAdapter(adapter);
        final EditText hours = (EditText) findViewById(R.id.etHours);
        final EditText minutes = (EditText) findViewById(R.id.etMinutes);
        final EditText seconds = (EditText) findViewById(R.id.etSeconds);
        final RadioButton rbStrength = (RadioButton) findViewById(R.id.rbStrength);
        Button addExerciseButton = (Button) findViewById(R.id.btnAddExercise);
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = newExerciseName.getText().toString();
                if(rbStrength.isChecked()) {
                    exerciseAdapter.addExercise(exerciseName);
                    newExerciseName.setText("");
                } else {
                    cardioAdapter.addExercise(new CardioExercise(exerciseName,
                            Integer.parseInt(hours.getText().toString()),
                            Integer.parseInt(minutes.getText().toString()),
                            Integer.parseInt(seconds.getText().toString())));
                }
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