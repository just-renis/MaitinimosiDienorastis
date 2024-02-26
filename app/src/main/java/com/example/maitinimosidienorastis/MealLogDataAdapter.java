package com.example.maitinimosidienorastis;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MealLogDataAdapter extends RecyclerView.Adapter<MealLogDataAdapter.MealLogDataViewHolder> {
    private List<MealTimeStamp> mealTimeStampsList;
    public MealLogDataAdapter(List<MealTimeStamp> mealTimeStampsList) {
        this.mealTimeStampsList = mealTimeStampsList;
    }
    public MealTimeStamp getProduct(int position) { return this.mealTimeStampsList.get(position); }
    @NonNull
    @Override
    public MealLogDataAdapter.MealLogDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MealLogDataAdapter.MealLogDataViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_log_data_row, parent, false));
    }
    @Override
    public void onBindViewHolder(@NonNull MealLogDataViewHolder holder, int position) {
        MealTimeStamp mealTimeStamp = mealTimeStampsList.get(position);
        holder.logId.setText(String.valueOf(position + 1));
        holder.date.setText(mealTimeStamp.getDate());
        holder.time.setText(mealTimeStamp.getTime());
    }
    @Override
    public int getItemCount() { return Math.min(mealTimeStampsList.size(), 10); }
    public class MealLogDataViewHolder extends RecyclerView.ViewHolder {
        TextView logId, date, time;
        LinearLayout mainView;
        public MealLogDataViewHolder(@NonNull View mealLogDataView) {
            super(mealLogDataView);
            logId = mealLogDataView.findViewById(R.id.logId);
            date = mealLogDataView.findViewById(R.id.date);
            time = mealLogDataView.findViewById(R.id.time);
            mainView = mealLogDataView.findViewById(R.id.mealLogDataMainView);
        }
    }
}
