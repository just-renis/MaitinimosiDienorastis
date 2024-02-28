package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealLogDataActivity extends AppCompatActivity {

    private RecyclerView mealLogDataList;
    private MealLogDataAdapter adapter;
    private List<MealTimeStamp> mealTimeStampsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log_data);
        mealLogDataList = findViewById(R.id.mealLogDataList);
        mealTimeStampsList = new ArrayList<>();
        adapter = new MealLogDataAdapter(mealTimeStampsList);
        mealLogDataList.setAdapter(adapter);
        mealLogDataList.setLayoutManager(new LinearLayoutManager(this));
        getData();
    }
    private void getData() {
        DatabaseReference userProductsRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("mealTimeStamps");
        userProductsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealTimeStampsList.clear();
                for (DataSnapshot mealTimeStampSnapshot : dataSnapshot.getChildren()) {
                    ArrayList<String> productsKeys = new ArrayList<>();
                    for (DataSnapshot productKeySnapshot : mealTimeStampSnapshot.child("products").getChildren()) {
                        productsKeys.add(productKeySnapshot.getValue(String.class));
                    }
                    mealTimeStampsList.add(new MealTimeStamp(
                            mealTimeStampSnapshot.child("date").getValue(String.class),
                            mealTimeStampSnapshot.child("time").getValue(String.class),
                            productsKeys
                    ));
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}