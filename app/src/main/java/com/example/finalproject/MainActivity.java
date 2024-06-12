package com.example.finalproject;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Button startDateButton;
    private Button endDateButton;
    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startDateButton = findViewById(R.id.start_date_button);
        endDateButton = findViewById(R.id.end_date_button);
        Button checkAvailabilityButton = findViewById(R.id.check_availability_button);

        startDateButton.setOnClickListener(v -> showDatePickerDialog(true));
        endDateButton.setOnClickListener(v -> showDatePickerDialog(false));
        checkAvailabilityButton.setOnClickListener(v -> {
            if (startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                Intent intent = new Intent(this, ListCars.class);
                intent.putExtra("start_date", startDate);
                intent.putExtra("end_date", endDate);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "Please select both start and end dates.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        if (!isStartDate && startDate != null) {
            try {
                String[] parts = startDate.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]) - 1;
                int day = Integer.parseInt(parts[2]);
                calendar.set(year, month, day + 1);  // Ensures the end date is at least the day after start date
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                MainActivity.this,
                (view, year1, month1, dayOfMonth) -> {
                    String selectedDate = year1 + "-" + String.format("%02d", (month1 + 1)) + "-" + String.format("%02d", dayOfMonth);
                    if (isStartDate) {
                        startDate = selectedDate;
                        startDateButton.setText("Start Date: " + startDate);
                    } else {
                        endDate = selectedDate;
                        endDateButton.setText("End Date: " + endDate);
                    }
                }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
}
