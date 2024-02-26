package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {

    private Map<String, TextInputEditText> editFieldsMap = new HashMap<>();
    Button updateProductButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        updateProductButton = findViewById(R.id.updateProductButton);
        setupEditFieldsMap();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
        productsRef.orderByKey().equalTo(getIntent().getStringExtra("PRODUCT_KEY")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Map<String, String> fieldValues = (Map<String, String>) productSnapshot.getValue();
                        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                            editFieldsMap.get(entry.getKey()).setText(entry.getValue());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> updateMap = new HashMap<>();
                for (Map.Entry<String, TextInputEditText> entry : editFieldsMap.entrySet()) {
                    TextInputEditText editText = entry.getValue();
                    updateMap.put(entry.getKey(), editText.getText().toString());
                }
                productsRef.child(getIntent().getStringExtra("PRODUCT_KEY")).updateChildren(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UpdateProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                Intent intent = new Intent(getApplicationContext(), MyProductsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void setupEditFieldsMap() {
        String[] fieldNames = {
                "name", "edibleCoefficient", "water", "dryMatter", "proteinTotal", "proteinAnimal",
                "proteinPlant", "fatTotal", "fattyAcidsTotalSaturated", "fattyAcidsTotalMonounsaturated",
                "fattyAcidsTotalPolyunsaturated", "cholesterol", "carbohydrates", "starch", "sugars",
                "fibre", "ashMinerals", "sodium", "magnesium", "phosphorus", "potassium", "calcium",
                "iron", "zinc", "selenium", "iodide", "vitaminA_RE", "vitaminD", "vitaminE_TE",
                "thiamin", "riboflavin", "niacin", "niacinEquivalent", "folicAcid", "vitaminB6",
                "vitaminB12", "vitaminC", "alcohol", "energyValueKJ", "energyValueKCAL"
        };
        for (String fieldName : fieldNames) editFieldsMap.put(fieldName, findViewById(getResources().getIdentifier(fieldName, "id", getPackageName())));
    }
}