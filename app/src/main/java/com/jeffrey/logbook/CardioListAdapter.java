package com.jeffrey.logbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Jeffrey on 5/13/2016.
 */
public class CardioListAdapter extends BaseAdapter {

    private ArrayList<CardioExercise> list;
    private Context c;

    public CardioListAdapter(Context c, ArrayList<CardioExercise> list) {
        this.c = c;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.cardio_row, null);
        }
        CardioExercise cardio = list.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.tvCardioName);
        name.setText(cardio.getName());
        TextView time = (TextView) convertView.findViewById(R.id.tvCardioTime);
        time.setText("" + cardio.getHours() + ":" + cardio.getMinutes() + ":" + cardio.getSeconds());
        Button remove = (Button) convertView.findViewById(R.id.btnDeleteCardio);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    public void addExercise(CardioExercise exercise) {
        if(list.contains(exercise))
            Toast.makeText(c, "Exercise Already Added", Toast.LENGTH_SHORT).show();
        else {
            list.add(exercise);
            notifyDataSetChanged();
        }

    }

}
