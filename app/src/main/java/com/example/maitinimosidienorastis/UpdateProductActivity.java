package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
        getDataForProductFieldsFromDB(productsRef);
        setupUpdateProductButton(productsRef);
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
    private void isProductNameExists(ProductNameExistsCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newProductName = editFieldsMap.get("name").getText().toString().trim();
                String currentProductKey = getIntent().getStringExtra("PRODUCT_KEY");
                boolean exists = false;
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String existingProductName = productSnapshot.child("name").getValue(String.class);
                    if (!productSnapshot.getKey().equals(currentProductKey) && existingProductName.equals(newProductName)) {
                        exists = true;
                        break;
                    }
                }
                callback.onCallBack(exists);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { callback.onCallBack(false); }
        });
    }
    private void getDataForProductFieldsFromDB(DatabaseReference productsRef) {
        productsRef.orderByKey().equalTo(getIntent().getStringExtra("PRODUCT_KEY")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                        Map<String, String> fieldValues = (Map<String, String>) productSnapshot.getValue();
                        for (Map.Entry<String, String> entry : fieldValues.entrySet()) {
                            editFieldsMap.get(entry.getKey()).setText(entry.getValue().equals("No Data") ? "" : entry.getValue());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    private void setupUpdateProductButton(DatabaseReference productsRef) {
        updateProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isProductNameExists(new ProductNameExistsCallback() {
                    @Override
                    public void onCallBack(boolean exists) {
                        if (exists) Toast.makeText(UpdateProductActivity.this, "Produktas su tuo pačiu pavadinimu jau egzistuoja", Toast.LENGTH_SHORT).show();
                        else {
                            Map<String, Object> updateMap = new HashMap<>();
                            for (Map.Entry<String, TextInputEditText> entry : editFieldsMap.entrySet()) {
                                TextInputEditText editText = entry.getValue();
                                updateMap.put(entry.getKey(), editText.getText().toString().trim().equals("") ? "No Data" : editText.getText().toString().trim());
                            }
                            productsRef.child(getIntent().getStringExtra("PRODUCT_KEY")).updateChildren(updateMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(UpdateProductActivity.this, "Produktas atnaujintas sėkmingai", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            finish();
                        }
                    }
                });
            }
        });
    }
}