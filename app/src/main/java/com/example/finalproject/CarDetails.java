package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CarDetails extends AppCompatActivity {
    private ImageView carImageView;
    private TextView carDetailsTextView;
    private Button bookButton;
    private int carId;
    private String startDate;
    private String endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_details);

        carImageView = findViewById(R.id.carImageView);
        carDetailsTextView = findViewById(R.id.carDetailsTextView);
        bookButton = findViewById(R.id.bookButton);

        Intent intent=getIntent();
        // Receiving data from intent
        carId = intent.getIntExtra("car_id", 0);
        startDate = intent.getStringExtra("start_date");
        endDate = intent.getStringExtra("end_date");

        if (carId != 0) {
            loadCarDetails(carId);
        } else {
            Toast.makeText(this, "Invalid car ID", Toast.LENGTH_SHORT).show();
        }

        bookButton.setOnClickListener(v -> bookCar());
    }

    private void loadCarDetails(int carId) {
        String url = "http://192.168.88.20/CarRentalApp/car_details_script.php?id=" + carId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length() > 0) {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            Glide.with(getApplicationContext()).load(jsonObject.getString("image_url")).into(carImageView);

                            String details = "*Model: " + jsonObject.getString("model") + "\n\n"
                                    + "*Price: $" + jsonObject.getDouble("price") + "\n\n"
                                    + "*Rating: " + jsonObject.getDouble("rating") + "/10\n\n"
                                    + "*Make Date: " + jsonObject.getString("make_date") + "\n\n"
                                    + "*Description: " + jsonObject.getString("description");
                            carDetailsTextView.setText(details);
                        } else {
                            Toast.makeText(CarDetails.this, "No car details available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(CarDetails.this, "Failed to parse car details", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(CarDetails.this, "Failed to fetch car details: " + error.getMessage(), Toast.LENGTH_LONG).show());

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }



    private void bookCar() {
        String bookingUrl = "http://192.168.88.20/CarRentalApp/booking.php";
        StringRequest bookingRequest = new StringRequest(Request.Method.POST, bookingUrl,
                response -> Toast.makeText(CarDetails.this, "Car booked successfully!", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(CarDetails.this, "Booking failed: " + error.getMessage(), Toast.LENGTH_LONG).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("car_id", String.valueOf(carId));
                params.put("user_id", "1");
                params.put("start_date", startDate);
                params.put("end_date", endDate);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(bookingRequest);
    }
}
