package com.example.carpooling;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
public class Ride2Adapter extends RecyclerView.Adapter<Ride2Adapter.RideViewHolder> {
    private List<Ride2> availableRides;

    public Ride2Adapter(List<Ride2> availableRides) {
        this.availableRides = availableRides;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride2 ride = availableRides.get(position);

        holder.startLocationText.setText("From: " + ride.getStartLocation());
        holder.destinationText.setText("To: " + ride.getDestination());
        holder.priceText.setText("Price: $" + ride.getPrice());

        holder.acceptButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ChooseActivity.class);

            intent.putExtra("rideId", ride.getRideID());
            intent.putExtra("startLocation", ride.getStartLocation());
            intent.putExtra("destination", ride.getDestination());
            intent.putExtra("price", ride.getPrice());
            intent.putExtra("driverID", ride.getDriverID());
            intent.putExtra("startLocationLat", ride.getStartLat());
            intent.putExtra("startLocationLng", ride.getStartLng());
            intent.putExtra("endLocationLat", ride.getDestinationLat());
            intent.putExtra("endLocationLng", ride.getDestinationLng());

            v.getContext().startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return availableRides.size();
    }
    public static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView startLocationText, destinationText, priceText;
        Button acceptButton;

        public RideViewHolder(View itemView) {
            super(itemView);
            startLocationText = itemView.findViewById(R.id.startLocationText);
            destinationText = itemView.findViewById(R.id.destinationText);
            priceText = itemView.findViewById(R.id.priceText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}