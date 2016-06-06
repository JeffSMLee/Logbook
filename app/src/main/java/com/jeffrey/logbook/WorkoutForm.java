package com.jeffrey.logbook;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
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

    private LinearLayout cbLayout;

    private DBAccessor dbAccessor;

    private String date;

    private HashMap<String, List<Set>> sets;
    private ArrayList<Exercise> exercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_form);

        dbAccessor = new DBAccessor(this);

        Intent intent = getIntent();
        date = intent.getStringExtra("date");

        exerciseList = (ExpandableListView) findViewById(R.id.lvExercises);
        exerciseList.setDescendantFocusability(ExpandableListView.FOCUS_AFTER_DESCENDANTS);

        exercises = new ArrayList<>();
        sets = new HashMap<>();

        dbAccessor.retrieveWorkout(date, exercises, sets);

        exerciseAdapter = new WorkoutListAdapter(this, exercises, sets, exerciseList);
        exerciseList.setAdapter(exerciseAdapter);

        cbLayout = (LinearLayout) findViewById(R.id.layoutInputCBContainer);

        final AutoCompleteTextView newExerciseName = (AutoCompleteTextView) findViewById(R.id.etExerciseName);
        newExerciseName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    cbLayout.setVisibility(View.VISIBLE);
                } else {
                    cbLayout.setVisibility(View.INVISIBLE);
                }
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dbAccessor.retrieveHistoricExercises());
        newExerciseName.setAdapter(adapter);
        final CheckBox cbWeight = (CheckBox) findViewById(R.id.cbWeight);
        final CheckBox cbReps = (CheckBox) findViewById(R.id.cbReps);
        final CheckBox cbTime = (CheckBox) findViewById(R.id.cbTime);
        final CheckBox cbDistance = (CheckBox) findViewById(R.id.cbDistance);
        Button addExerciseButton = (Button) findViewById(R.id.btnAddExercise);
        addExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String exerciseName = newExerciseName.getText().toString();
                List<Exercise.Input> inputs = new ArrayList<Exercise.Input>();
                if(cbWeight.isChecked())
                    inputs.add(Exercise.Input.WEIGHT);
                if(cbReps.isChecked())
                    inputs.add(Exercise.Input.REPS);
                if(cbTime.isChecked())
                    inputs.add(Exercise.Input.TIME);
                if(cbDistance.isChecked())
                    inputs.add(Exercise.Input.DISTANCE);
                if(inputs.size() == 0) {
                    Toast.makeText(WorkoutForm.this, "Cannot add exercise with no inputs", Toast.LENGTH_SHORT).show();
                    return;
                }
                exerciseAdapter.addExercise(exerciseName, inputs);
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
                dbAccessor.finishWorkout(exercises, sets, date);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Exit without saving?");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}