package com.example.hikingappasm.Observation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.hikingappasm.DatabaseHelper;
import com.example.hikingappasm.R;

import java.util.Calendar;

public class AddNewObservation extends AppCompatActivity {

    private EditText observation_name_input, observation_date_input, observation_comment_input;
    private Button dateTimeObservationBtn;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private int hike_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_observation);

        initializeViews();
        initializeDateTimePickers();

        Intent intent = getIntent();
        hike_id = intent.getIntExtra("hike_id", 0);

        observation_date_input.setText(getTodayDate());

        Button addNewObservation = findViewById(R.id.addNewObservation);
        addNewObservation.setOnClickListener(v -> handleCreateObservation());

        ImageView backIcon = findViewById(R.id.backAddingObservation);
        backIcon.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        observation_name_input = findViewById(R.id.observation_name);
        observation_date_input = findViewById(R.id.observation_date);
        observation_comment_input = findViewById(R.id.observation_comment);
        dateTimeObservationBtn = findViewById(R.id.dateTimeObservationBtn);

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Observations");
        }
    }

    private void initializeDateTimePickers() {
        datePickerDialog = createDatePickerDialog();
        timePickerDialog = createTimePickerDialog();

        observation_date_input.setOnClickListener(v -> {
            timePickerDialog.show();
            datePickerDialog.show();
        });
    }

    private DatePickerDialog createDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            month = month + 1;
            observation_date_input.setText(makeDateString(day, month, year));
        };
        
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        int style = AlertDialog.THEME_HOLO_DARK;
        return new DatePickerDialog(this, style, dateSetListener, year, month, day);
    }

    private TimePickerDialog createTimePickerDialog() {
        @SuppressLint("SetTextI18n") TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute)
            -> observation_date_input.setText(makeTimeString(minute, hourOfDay) + " - " + observation_date_input.getText().toString());

        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int style = AlertDialog.THEME_HOLO_DARK;

        return new TimePickerDialog(this, style, timeSetListener, hour, minute, true);
    }

    private void handleCreateObservation() {
        String observation_name = observation_name_input.getText().toString().trim();
        String observation_date = observation_date_input.getText().toString().trim();
        String observation_comments = observation_comment_input.getText().toString().trim();

        if (observation_name.isEmpty() || observation_date.isEmpty() || observation_comments.isEmpty()) {
            showAlertDialog("All required fields must be filled!");
        } else {
            ContentValues values = createObservationContentValues(observation_name, observation_date, observation_comments);

            String message = createObservationMessage(observation_name, observation_date, observation_comments);
            showConfirmDialog(message, values);
        }
    }

    private ContentValues createObservationContentValues(String name, String date, String comments) {
        ContentValues values = new ContentValues();
        values.put("hike_id", hike_id);
        values.put("observation", name);
        values.put("date", date);
        values.put("comment", comments);
        return values;
    }

    private String createObservationMessage(String name, String date, String comments) {
        return "New observation will be added:\n\n" +
               "Observation: " + name + ",\n" +
               "Time of observation: " + date + ",\n" +
               "Comment: " + comments + ".\n\n" +
               "Are you sure?";
    }

    private void showConfirmDialog(String message, ContentValues values) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> {
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            dbHelper.addObservation(values.getAsString("observation"), values.getAsInteger("hike_id"),
                    values.getAsString("date"), values.getAsString("comment"));
            navigateToObservationsActivity();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void navigateToObservationsActivity() {
        Intent intent = new Intent(this, ObservationsActivity.class);
        intent.putExtra("hike_id", hike_id);
        setResult(RESULT_OK, intent);
        startActivity(intent);
    }

    private String getTodayDate() {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeTimeString(minute, hours) + " - " + makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        String dayStr = (day < 10) ? "0" + day : String.valueOf(day);
        String monthStr = (month < 10) ? "0" + month : String.valueOf(month);
        return dayStr + "/" + monthStr + "/" + year;
    }

    private String makeTimeString(int minute, int hour) {
        String minuteStr = (minute < 10) ? "0" + minute : String.valueOf(minute);
        String hourStr = (hour < 10) ? "0" + hour : String.valueOf(hour);
        return hourStr + ":" + minuteStr;
    }
}
