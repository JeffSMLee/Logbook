package com.jeffrey.logbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeffrey on 4/20/2016.
 */
public class WorkoutListAdapter extends BaseExpandableListAdapter {

    private Context c;
    private List<Exercise> exercises;
    private HashMap<String, List<Set>> sets;
    private ExpandableListView lv;

    public WorkoutListAdapter(Context c, List<Exercise> exercises, HashMap<String, List<Set>> sets, ExpandableListView lv) {
        this.c = c;
        this.exercises = exercises;
        this.sets = sets;
        this.lv = lv;
    }

    @Override
    public int getGroupCount() {
        return exercises.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(sets.get(exercises.get(groupPosition).getName()) != null)
            return sets.get(exercises.get(groupPosition).getName()).size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return exercises.get(groupPosition).getName();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sets.get(exercises.get(groupPosition).getName()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void addExercise(String name, List<Exercise.Input> inputs) {
        for(Exercise e : exercises) {
            if(e.getName() == name) {
                Toast.makeText(c, "Exercise Already Added", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        exercises.add(new Exercise(name, inputs));
        ArrayList<Set> setList = new ArrayList<>();
        Set s = new Set();
        setList.add(s);
        sets.put(name, setList);
        notifyDataSetChanged();
    }

    private void removeExercise(int groupPosition) {
        sets.remove(exercises.get(groupPosition).getName());
        exercises.remove(groupPosition);
        notifyDataSetChanged();
    }

    private void addSet(int groupPosition, Set set) {
        List<Set> setList = sets.get(exercises.get(groupPosition).getName());
        setList.add(getChildrenCount(groupPosition) - 1, set);
        notifyDataSetChanged();
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {   //inflate layout if does not exist
            convertView = inflater.inflate(R.layout.exercise_name_row, parent, false);
        } else if(convertView.findViewById(R.id.btnDeleteExercise) == null) {   //re-inflate layout if outdated
            convertView = inflater.inflate(R.layout.exercise_name_row, parent, false);
        }

        TextView exerciseName = (TextView) convertView.findViewById(R.id.tvExerciseName);
        exerciseName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lv.isGroupExpanded(groupPosition)) {
                        lv.collapseGroup(groupPosition);
                    } else {
                        lv.expandGroup(groupPosition);
                    }
                }
            });
        Button removeExerciseButton = (Button) convertView.findViewById(R.id.btnDeleteExercise);

        exerciseName.setText(exercises.get(groupPosition).getName());

        removeExerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage("Remove " + exercises.get(groupPosition).getName() + "?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeExercise(groupPosition);
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (childPosition == getChildrenCount(groupPosition) - 1) { //layout for new set
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.new_exercise_set_row, parent, false);
            } else if(convertView.findViewById(R.id.btnCompleteSet) == null) {
                convertView = inflater.inflate(R.layout.new_exercise_set_row, parent, false);
            }
            final EditText weight = (EditText) convertView.findViewById(R.id.etNewWeight);
            final RadioButton kg = (RadioButton) convertView.findViewById(R.id.rbKg);
            LinearLayout weightLayout = (LinearLayout) convertView.findViewById(R.id.layoutNewWeight);
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.WEIGHT)) {
                weightLayout.setVisibility(View.VISIBLE);
                if(childPosition > 0)
                    weight.setText("" + ((Set)getChild(groupPosition, childPosition - 1)).getWeight());
                else
                    weight.setText("");
            } else {
                weightLayout.setVisibility(View.GONE);
            }
            final EditText reps = (EditText) convertView.findViewById(R.id.etNewReps);
            LinearLayout repsLayout = (LinearLayout) convertView.findViewById(R.id.layoutNewReps);
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.REPS)) {
                repsLayout.setVisibility(View.VISIBLE);
                if(childPosition > 0)
                    reps.setText("" + ((Set)getChild(groupPosition, childPosition - 1)).getReps());
                else
                    reps.setText("");
            } else {
                repsLayout.setVisibility(View.GONE);
            }
            final NumberPicker npHours = (NumberPicker) convertView.findViewById(R.id.npHours);
            npHours.setMinValue(0);
            npHours.setMaxValue(24);
            final NumberPicker npMinutes = (NumberPicker) convertView.findViewById(R.id.npMinutes);
            npMinutes.setMinValue(0);
            npMinutes.setMaxValue(59);
            final NumberPicker npSeconds = (NumberPicker) convertView.findViewById(R.id.npSeconds);
            npSeconds.setMinValue(0);
            npSeconds.setMaxValue(59);
            LinearLayout timeLayout = (LinearLayout) convertView.findViewById(R.id.layoutNewTime);
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.TIME)) {
                timeLayout.setVisibility(View.VISIBLE);
            } else {
                timeLayout.setVisibility(View.GONE);
            }
            final EditText distance = (EditText) convertView.findViewById(R.id.etNewDistance);
            final RadioButton km = (RadioButton) convertView.findViewById(R.id.rbKm);
            LinearLayout distanceLayout = (LinearLayout) convertView.findViewById(R.id.layoutNewDistance);
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.DISTANCE)) {
                distanceLayout.setVisibility(View.VISIBLE);
            } else {
                distanceLayout.setVisibility(View.GONE);
            }
            Button completeSet = (Button) convertView.findViewById(R.id.btnCompleteSet);
            completeSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Set set = new Set();
                    if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.WEIGHT)) {
                        if(weight.getText().toString().equals("")) {
                            Toast.makeText(c, "Enter a weight", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        set.setWeight((kg.isChecked() ? 2.2046 : 1) * Double.parseDouble(weight.getText().toString()));
                        weight.setText("");
                    }
                    if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.REPS)) {
                        if(reps.getText().toString().equals("")) {
                            Toast.makeText(c, "Enter number of reps", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(reps.getText().toString().equals("0")) {
                            Toast.makeText(c, "Need at least 1 rep", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        set.setReps(Integer.parseInt(reps.getText().toString()));
                        weight.setText("");
                    }
                    if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.TIME)) {
                        if(npHours.getValue() == 0 && npMinutes.getValue() == 0 && npSeconds.getValue() == 0) {
                            Toast.makeText(c, "Enter a time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        set.setTime("" + String.format("%02d", npHours.getValue()) + ":"
                                + String.format("%02d", npMinutes.getValue()) + ":"
                                + String.format("%02d", npSeconds.getValue()));
                        npHours.setValue(0);
                        npMinutes.setValue(0);
                        npSeconds.setValue(0);
                    }
                    if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.DISTANCE)) {
                        if(distance.getText().toString().equals("")) {
                            Toast.makeText(c, "Enter a distance", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        set.setDistance((km.isChecked() ? 0.6213 : 1) * Double.parseDouble(distance.getText().toString()));
                        distance.setText("");
                    }
                    addSet(groupPosition, set);
                }
            });
        } else {
            if(convertView == null) {
                convertView = inflater.inflate(R.layout.exercise_set_row, parent, false);
            } else if(convertView.findViewById(R.id.tvSetNumber) == null) {
                convertView = inflater.inflate(R.layout.exercise_set_row, parent, false);
            }
            TextView setNum = (TextView) convertView.findViewById(R.id.tvSetNumber);
            TextView weight = (TextView) convertView.findViewById(R.id.tvWeight);
            TextView reps = (TextView) convertView.findViewById(R.id.tvReps);
            TextView time = (TextView) convertView.findViewById(R.id.tvTime);
            TextView distance = (TextView) convertView.findViewById(R.id.tvDistance);
            Set set = (Set) getChild(groupPosition, childPosition);
            setNum.setText("" + (childPosition + 1));
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.WEIGHT)) {
                weight.setText("" + set.getWeight() + " lbs");
                weight.setVisibility(View.VISIBLE);
            } else {
                weight.setVisibility(View.GONE);
            }
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.REPS)) {
                reps.setText("" + set.getReps() + " reps");
                reps.setVisibility(View.VISIBLE);
            } else {
                reps.setVisibility(View.GONE);
            }
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.TIME)) {
                time.setText("" + set.getTime());
                time.setVisibility(View.VISIBLE);
            } else {
                time.setVisibility(View.GONE);
            }
            if(exercises.get(groupPosition).getInputs().contains(Exercise.Input.DISTANCE)) {
                distance.setText("" + set.getDistance() + " mi");
                distance.setVisibility(View.VISIBLE);
            } else {
                distance.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
