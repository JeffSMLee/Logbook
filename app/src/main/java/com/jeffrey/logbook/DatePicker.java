package com.jeffrey.logbook;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffrey.logbook.DB.DBAccessor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DatePicker extends AppCompatActivity {

    private android.widget.DatePicker picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        picker = (android.widget.DatePicker) findViewById(R.id.datePicker);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.date_picker_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_write_file:
                DBAccessor accessor = new DBAccessor(this);
                String state = Environment.getExternalStorageState();
                if (Environment.MEDIA_MOUNTED.equals(state)) {
                    String directory = "Logbook";
                    File dir = new File(Environment.getExternalStorageDirectory(), directory);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, "logbook.txt");
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(accessor.retrieveAllExercisesAsString("\t").getBytes());
                        fos.close();
                    } catch(FileNotFoundException e) {
                        Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
                    } catch(IOException e) {
                        Toast.makeText(this, "Error writing to file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "storage unavailable", Toast.LENGTH_SHORT).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void confirmDate(View view) {
        String date = (picker.getMonth() + 1) + "/" + picker.getDayOfMonth() + "/" + picker.getYear();
        Intent intent = new Intent(this, WorkoutForm.class);
        intent.putExtra("date", date);
        startActivity(intent);
    }
}
