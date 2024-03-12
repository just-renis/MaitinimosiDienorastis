package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealLogDataActivity extends AppCompatActivity implements MealLogViewListener{

    private RecyclerView mealLogDataList;
    private MealLogDataAdapter adapter;
    private List<MealTimeStamp> mealTimeStampsList;
    private TextView noMealLogsText;
    private Button generateReportButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log_data);
        mealLogDataList = findViewById(R.id.mealLogDataList);
        noMealLogsText = findViewById(R.id.noMealLogsText);
        mealTimeStampsList = new ArrayList<>();
        adapter = new MealLogDataAdapter(mealTimeStampsList, MealLogDataActivity.this);
        mealLogDataList.setAdapter(adapter);
        mealLogDataList.setLayoutManager(new LinearLayoutManager(this));
        generateReportButton = findViewById(R.id.generateReportButton);
        setupGenerateReportButton();
        getMealTimeStamps();
    }
    private void getMealTimeStamps() {
        loadMealTimeStamps(new CallbackInterface() {
            @Override
            public void onCallBack() {
                if (!mealTimeStampsList.isEmpty()) noMealLogsText.setVisibility(View.GONE);
                else noMealLogsText.setVisibility(View.VISIBLE);
            }
        });
    }
    private void loadMealTimeStamps(CallbackInterface callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mealTimeStamps");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealTimeStampsList.clear();
                for (DataSnapshot mealTimeStampSnapshot : dataSnapshot.getChildren()) {
                    ArrayList<String> personalProductsKeys = new ArrayList<>();
                    ArrayList<String> localProductsKeys = new ArrayList<>();
                    for (DataSnapshot productKeySnapshot : mealTimeStampSnapshot.child("personalProducts").getChildren()) {
                        personalProductsKeys.add(productKeySnapshot.getValue(String.class));
                    }
                    for (DataSnapshot productKeySnapshot : mealTimeStampSnapshot.child("localProducts").getChildren()) {
                        localProductsKeys.add(productKeySnapshot.getValue(String.class));
                    }
                    mealTimeStampsList.add(new MealTimeStamp(
                            mealTimeStampSnapshot.getKey(),
                            mealTimeStampSnapshot.child("date").getValue(String.class),
                            mealTimeStampSnapshot.child("time").getValue(String.class),
                            personalProductsKeys,
                            localProductsKeys
                    ));
                }
                adapter.notifyDataSetChanged();
                callback.onCallBack();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { callback.onCallBack(); }
        });
    }
    private void setupGenerateReportButton() {
        generateReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mealTimeStampsList.isEmpty()) {
                    Intent intent = new Intent(MealLogDataActivity.this, GenerateReportActivity.class);
                    startActivity(intent);
                }
                else Toast.makeText(MealLogDataActivity.this, "Neturite jokių mitybos veiklų", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Product product) {

    }
}