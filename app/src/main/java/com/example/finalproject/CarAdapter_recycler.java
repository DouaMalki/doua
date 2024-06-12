package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CarAdapter_recycler extends RecyclerView.Adapter<CarAdapter_recycler.ViewHolder> {
    private Context context;
    private List<Car_in_recycler> cars;
    private String startDate;
    private String endDate;

    public CarAdapter_recycler(Context context, List<Car_in_recycler> cars, String startDate, String endDate) {
        this.context = context;
        this.cars = cars;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Car_in_recycler car = cars.get(position);
        ImageView carImageView = holder.cardView.findViewById(R.id.carImageView);
        TextView carInfo = holder.cardView.findViewById(R.id.car_info);

        Glide.with(context)
                .load(car.getImageUrl())
                .into(carImageView);

        String details = "Price: $" + car.getPrice() + "\n" +
                "Rating: " + car.getRating() + "/10\n" +
                "Model: " + car.getModel() + "\n" +
                "Make Date: " + car.getMake_date();
        carInfo.setText(details);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, CarDetails.class);
            intent.putExtra("car_id", car.getId());
            intent.putExtra("start_date", startDate);  // Pass the start date
            intent.putExtra("end_date", endDate);  // Pass the end date
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() { return cars.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView carImageView;
        public TextView carInfo;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            carImageView = view.findViewById(R.id.carImageView);
            carInfo = view.findViewById(R.id.car_info);
        }
    }
}

