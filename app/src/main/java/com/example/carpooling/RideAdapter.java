package com.example.carpooling;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.location.Geocoder;
import android.location.Address;
import java.io.IOException;


public class RideAdapter extends RecyclerView.Adapter<RideAdapter.RideViewHolder> {
    private List<Ride> rideList;
    public RideAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }
    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ride, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);
        holder.startLocation.setText("From: " + ride.getStartLocation());
        holder.destination.setText("To: " + ride.getDestination());
        holder.price.setText(String.format("Price: $%.2f", ride.getPrice()));
        holder.acceptButton.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RatingPassengerActivity.class);
            intent.putExtra("rideID", ride.getRideID());
            intent.putExtra("driverID", ride.getDriverID());
            v.getContext().startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return rideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView startLocation;
        TextView destination;
        TextView price;
        Button acceptButton;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            startLocation = itemView.findViewById(R.id.startLocationText);
            destination = itemView.findViewById(R.id.destinationText);
            price = itemView.findViewById(R.id.priceText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}

