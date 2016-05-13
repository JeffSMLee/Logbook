package com.jeffrey.logbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeffrey on 4/20/2016.
 */
public class StrengthListAdapter extends BaseExpandableListAdapter {

    private Context c;
    private List<String> exercises;
    private HashMap<String, List<Set>> sets;
    private ExpandableListView lv;

    public StrengthListAdapter(Context c, List<String> exercises, HashMap<String, List<Set>> sets, ExpandableListView lv) {
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
        if(sets.get(getGroup(groupPosition)) != null)
            return sets.get(getGroup(groupPosition)).size();
        else
            return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return exercises.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return sets.get(getGroup(groupPosition)).get(childPosition);
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

    public void addExercise(String name) {
        if(!exercises.contains(name)) {
            exercises.add(name);
            ArrayList<Set> setList = new ArrayList<>();
            Set s = new Set(0, 0);
            setList.add(s);
            sets.put(name, setList);
            notifyDataSetChanged();
        } else {
            Toast.makeText(c, "Exercise Already Added", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeExercise(int groupPosition) {
        sets.remove(getGroup(groupPosition).toString());
        exercises.remove(groupPosition);
        notifyDataSetChanged();
    }

    private void addSet(int groupPosition, Set set) {
        List<Set> setList = sets.get(exercises.get(groupPosition));
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

        exerciseName.setText(getGroup(groupPosition).toString());

        removeExerciseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeExercise(groupPosition);
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
            final EditText reps = (EditText) convertView.findViewById(R.id.etNewReps);
            Button completeSet = (Button) convertView.findViewById(R.id.btnCompleteSet);
            completeSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addSet(groupPosition, new Set((kg.isChecked() ? 2.2046 : 1) * Double.parseDouble(weight.getText().toString()), Integer.parseInt(reps.getText().toString())));
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
            Set set = (Set) getChild(groupPosition, childPosition);
            setNum.setText("" + (childPosition + 1));
            weight.setText("" + set.getWeight());
            reps.setText("" + set.getReps());
        }


        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
