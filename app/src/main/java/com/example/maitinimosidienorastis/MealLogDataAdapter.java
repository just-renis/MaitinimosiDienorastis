package com.example.maitinimosidienorastis;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MealLogDataAdapter extends RecyclerView.Adapter<MealLogDataAdapter.MealLogDataViewHolder> {
    private List<MealTimeStamp> mealTimeStampsList;
    private MealLogViewListener listener;
    public MealLogDataAdapter(List<MealTimeStamp> mealTimeStampsList, MealLogViewListener listener) {
        this.mealTimeStampsList = mealTimeStampsList;
        this.listener = listener;
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
        holder.date.setText(getFormattedDate(mealTimeStamp.getDate()));
        holder.time.setText(getFormattedTime(mealTimeStamp.getTime()));
        setupDeleteMealLogDataButton(holder, mealTimeStamp);
    }
    @Override
    public int getItemCount() { return Math.min(mealTimeStampsList.size(), 10); }
    public class MealLogDataViewHolder extends RecyclerView.ViewHolder {
        TextView logId, date, time;
        LinearLayout mainView;
        Button deleteButton;
        ConstraintLayout deleteLayout;
        public MealLogDataViewHolder(@NonNull View mealLogDataView) {
            super(mealLogDataView);
            logId = mealLogDataView.findViewById(R.id.logId);
            date = mealLogDataView.findViewById(R.id.date);
            time = mealLogDataView.findViewById(R.id.time);
            mainView = mealLogDataView.findViewById(R.id.mealLogDataMainView);
            deleteButton = mealLogDataView.findViewById(R.id.deleteButton);
            deleteLayout = mealLogDataView.findViewById(R.id.deleteLayout);
        }
    }
    private void setupDeleteMealLogDataButton(MealLogDataAdapter.MealLogDataViewHolder holder, MealTimeStamp mealTimeStamp) {
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mealTimeStamps");
                ref.child(mealTimeStamp.getKey()).removeValue();
            }
        });
    }
    private String getFormattedDate(String dateText) {
        String[] dateParts = dateText.split(" ");
        return String.format(Locale.getDefault(), "%d %s %d", Integer.parseInt(dateParts[0]), getMonthName(Integer.parseInt(dateParts[1])), Integer.parseInt(dateParts[2]));
    }
    private String getFormattedTime(String timeText) {
        String[] timeParts = timeText.split(" ");
        return String.format(Locale.getDefault(), "%d val. %d min.", Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
    }
    private String getMonthName(int monthIndex) {
        switch (monthIndex) {
            case 1:
                return "Sausio";
            case 2:
                return "Vasario";
            case 3:
                return "Kovo";
            case 4:
                return "Balandžio";
            case 5:
                return "Gegužės";
            case 6:
                return "Birželio";
            case 7:
                return "Liepos";
            case 8:
                return "Rugpjūčio";
            case 9:
                return "Rugsėjo";
            case 10:
                return "Spalio";
            case 11:
                return "Lapkričio";
            case 12:
                return "Gruodžio";
            default:
                return "Sausio";
        }
    }
}
