package com.example.maitinimosidienorastis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity {

    private Map<String, TextInputEditText> editFieldsMap = new HashMap<>();
    Button addProductButton;
    Integer productsCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        String[] fieldNames = {
                "name", "edibleCoefficient", "water", "dryMatter", "proteinTotal", "proteinAnimal",
                "proteinPlant", "fatTotal", "fattyAcidsTotalSaturated", "fattyAcidsTotalMonounsaturated",
                "fattyAcidsTotalPolyunsaturated", "cholesterol", "carbohydrates", "starch", "sugars",
                "fibre", "ashMinerals", "sodium", "magnesium", "phosphorus", "potassium", "calcium",
                "iron", "zinc", "selenium", "iodide", "vitaminA_RE", "vitaminD", "vitaminE_TE",
                "thiamin", "riboflavin", "niacin", "niacinEquivalent", "folicAcid", "vitaminB6",
                "vitaminB12", "vitaminC", "alcohol", "energyValueKJ", "energyValueKCAL"
        };
        for (String fieldName : fieldNames) {
            editFieldsMap.put(fieldName, findViewById(getResources().getIdentifier(fieldName, "id", getPackageName())));
        }
        addProductButton = findViewById(R.id.addProductButton);
        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editFieldsMap.get("name").getText().toString())) {
                    Toast.makeText(AddProductActivity.this, "Įrašykite produkto pavadinimą", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) addProductToDB(currentUser);
                else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MyProductsActivity.class);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private void addProductToDB(FirebaseUser currentUser) {
        DatabaseReference countRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("productsCount");
        countRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer count = mutableData.getValue(Integer.class);
                if (count == null) mutableData.setValue(1);
                else mutableData.setValue(count + 1);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                countRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsCount = snapshot.getValue(Integer.class);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("products");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Map<String, String> fieldValues = editFieldsMap.entrySet().stream()
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                entry -> entry.getValue().getText().toString()
                                        ));
                                Product product = new Product(fieldValues);
                                ref.push().setValue(product);
                                Toast.makeText(AddProductActivity.this, "Produktas sėkmingai pridėtas", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MyProductsActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }
}