package com.example.carpooling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Ride3Adapter extends RecyclerView.Adapter<Ride3Adapter.Ride3ViewHolder> {
    private List<Ride2> rideList;
    private Context context;

    public Ride3Adapter(Context context, List<Ride2> rideList) {
        this.context = context;
        this.rideList = rideList;
    }

    @Override
    public Ride3ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ride, parent, false);
        return new Ride3ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Ride3ViewHolder holder, int position) {
        Ride2 ride = rideList.get(position);
        holder.startLocationText.setText("From: " + ride.getStartLocation());
        holder.destinationText.setText("To: " + ride.getDestination());
        holder.priceText.setText("Price: $" + ride.getPrice());
        holder.acceptButton.setVisibility(ride.isAccepted() ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    public static class Ride3ViewHolder extends RecyclerView.ViewHolder {
        TextView startLocationText, destinationText, priceText;
        Button acceptButton;

        public Ride3ViewHolder(View itemView) {
            super(itemView);
            startLocationText = itemView.findViewById(R.id.startLocationText);
            destinationText = itemView.findViewById(R.id.destinationText);
            priceText = itemView.findViewById(R.id.priceText);
            acceptButton = itemView.findViewById(R.id.acceptButton);
        }
    }
}

