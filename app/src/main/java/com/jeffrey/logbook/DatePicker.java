package com.jeffrey.logbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

public class DatePicker extends AppCompatActivity {

    private android.widget.DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        picker = (android.widget.DatePicker) findViewById(R.id.datePicker);

    }

    public void confirmDate(View view) {
        String date = (picker.getMonth() + 1) + "/" + picker.getDayOfMonth() + "/" + picker.getYear();
        //Toast.makeText(this, date, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, WorkoutForm.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }
}
