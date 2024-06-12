package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ListCars extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;
    private List<Car> cars = new ArrayList<>();
    private String startDate, endDate;

    Button btn_open_drawer;
    Button btn_apply_filters;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_searched_cars);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(this, cars, startDate, endDate);
        recyclerView.setAdapter(carAdapter);

        //get the start and the end selected dates, to view the available cars at this time interval
        Intent intent = getIntent();
        startDate = intent.getStringExtra("start_date");
        endDate = intent.getStringExtra("end_date");

        String loadUrl = "http://192.168.88.20/CarRentalApp/get_available_cars_script.php?start_date='"+startDate+"'&end_date='"+endDate+"'";
        String load_filtered_url="http://192.168.88.20/CarRentalApp/get_filtered_cars_script.php?start_date='" + startDate + "'&end_date='" + endDate+"'";
        loadItems(loadUrl);//to fill the data in the recycler view

        //I put the spinner here, because we don't need to fill them at each time we open the bar
        //here I fill the spinners
        Spinner modelsSpinner = findViewById(R.id.spinner_model);
        Spinner makeDatesSpinner = findViewById(R.id.spinner_make_date);
        Spinner sortSpinner = findViewById(R.id.spinner_sort);
        String modelsUrl = "http://192.168.88.20/CarRentalApp/model_spinner.php";
        String makeDatesUrl = "http://192.168.88.20/CarRentalApp/make_date_spinner.php";
        loadSpinnerData(modelsUrl, modelsSpinner);
        loadSpinnerData(makeDatesUrl, makeDatesSpinner);
        sortSpinner = findViewById(R.id.spinner_sort);
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sorting_options, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);



        btn_open_drawer=findViewById(R.id.btn_open_drawer);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Button btnOpenDrawer = findViewById(R.id.btn_open_drawer);
        btnOpenDrawer.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        btn_apply_filters = findViewById(R.id.btn_apply_filters);
        Spinner finalSortSpinner = sortSpinner;
        btn_apply_filters.setOnClickListener(apply -> {
            String selectedModel = modelsSpinner.getSelectedItem().toString();
            String selectedMakeDate = makeDatesSpinner.getSelectedItem().toString();
            String selectedSortingOption = finalSortSpinner.getSelectedItem().toString();
            if(selectedModel.equals("none"))
            {
                selectedModel="";
            }
            if(selectedMakeDate.equals("none"))
            {
                selectedMakeDate="";
            }
            //the URL with the selected filter parameters
            String filteredUrl = load_filtered_url + "&model=" + encodeURIComponent(selectedModel) + "&make_date=" + encodeURIComponent(selectedMakeDate)+ "&sort=" + encodeURIComponent(selectedSortingOption);
            loadItems(filteredUrl);
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void loadItems(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        cars.clear();
                        if (jsonArray.length() == 0) {
                            Toast.makeText(this, "No cars found for the selected filters", Toast.LENGTH_LONG).show();
                        } else {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                cars.add(new Car(
                                        object.getInt("id"),
                                        object.getString("image_url"),
                                        object.getDouble("price"),
                                        object.getDouble("rating"),
                                        object.getString("model"),
                                        object.getString("make_date")
                                ));
                            }
                        }
                        carAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("ListCars", "Error parsing JSON response", e);
                        Toast.makeText(this, "Error parsing data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("Network Error", "Error Message: " + error.getMessage());
                    Toast.makeText(getApplicationContext(), "Failed to connect to the server", Toast.LENGTH_LONG).show();
                });

        queue.add(request);
    }

    private void loadSpinnerData(String url, Spinner spinner) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> data = new ArrayList<>();
                    data.add("none");
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            data.add(response.getString(i));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
                    spinner.setAdapter(adapter);
                }, error -> {
            Toast.makeText(getApplicationContext(), "Error loading data", Toast.LENGTH_SHORT).show();
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }
    private String encodeURIComponent(String component) {
        try {
            return URLEncoder.encode(component, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("EncodeError", "UTF-8 encoding not supported");
            return ""; // or some fallback value you see fit
        }
    }

}
