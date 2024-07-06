package com.example.pilldispenser;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UpcomingPillAdapter extends RecyclerView.Adapter<UpcomingPillAdapter.ViewHolder> {
    private List<Pill> upcomingPillList;

    public UpcomingPillAdapter(List<Pill> upcomingPillList) {
        this.upcomingPillList = upcomingPillList;
    }

    public void updatePills(List<Pill> newPills) {
        this.upcomingPillList = newPills;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pill_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pill pill = upcomingPillList.get(position);
        holder.pillNameTextView.setText(pill.getPillName());
        holder.pillQuantityTextView.setText("Quantity: " + pill.getQuantity());
        holder.pillTimeTextView.setText("Time: " + pill.getTime());
        holder.pillTankTextView.setText("Tank: " + pill.getTank());
    }

    @Override
    public int getItemCount() {
        return upcomingPillList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pillNameTextView;
        public TextView pillQuantityTextView;
        public TextView pillTimeTextView;
        public TextView pillTankTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            pillNameTextView = itemView.findViewById(R.id.pillNameTextView);
            pillQuantityTextView = itemView.findViewById(R.id.pillQuantityTextView);
            pillTimeTextView = itemView.findViewById(R.id.pillTimeTextView);
            pillTankTextView = itemView.findViewById(R.id.pillTankTextView);
        }
    }
}
