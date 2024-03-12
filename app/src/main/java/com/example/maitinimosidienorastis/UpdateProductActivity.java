package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateProductActivity extends AppCompatActivity {

    private Map<String, TextInputEditText> editFieldsMap = new HashMap<>();
    Button updateProductButton;
    ArrayList<Button> dropdownButtons = new ArrayList<>();
    ArrayList<LinearLayout> layouts = new ArrayList<>();
    int whichLayoutIsEnabled = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        updateProductButton = findViewById(R.id.updateProductButton);
        setFiltersForInputFields();
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
        getDataForProductFieldsFromDB(productsRef);
        setupUpdateProductButton(productsRef);
        setupDropDownButtons();
    }
    private void setFiltersForInputFields() {
        String[] fieldNames = {
                "name", "edibleCoefficient", "water", "dryMatter", "proteinTotal", "proteinAnimal",
                "proteinPlant", "fatTotal", "fattyAcidsTotalSaturated", "fattyAcidsTotalMonounsaturated",
                "fattyAcidsTotalPolyunsaturated", "carbohydrates", "starch", "sugars", "fibre", "ashMinerals",
                "calcium", "iron", "zinc", "thiamin", "riboflavin", "niacin", "folicAcid", "vitaminB6",
                "alcohol", "cholesterol", "sodium", "magnesium", "phosphorus", "potassium",  "selenium",
                "iodide", "vitaminA_RE", "vitaminD", "vitaminE_TE", "vitaminB12", "vitaminC",
                "niacinEquivalent", "energyValueKJ", "energyValueKCAL"
        };
        for (int i = 0; i < fieldNames.length; i++) {
            editFieldsMap.put(fieldNames[i], findViewById(getResources().getIdentifier(fieldNames[i], "id", getPackageName())));
            if (i != 0) {
                if (i == 1) editFieldsMap.get(fieldNames[i]).setFilters(new InputFilter[]{ new InputFilterMinMax(0.00, 1.0)});
                else if (i <= 24) editFieldsMap.get(fieldNames[i]).setFilters(new InputFilter[]{ new InputFilterMinMax(0.00, 100.0)});
                else editFieldsMap.get(fieldNames[i]).setFilters(new InputFilter[]{ new InputFilterMinMax(0.00, 100000.0)});
            }
        }
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
                            if (!entry.getKey().equals("isMineNeededToShow")) editFieldsMap.get(entry.getKey()).setText(entry.getValue().equals("No Data") ? "" : entry.getValue());
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
    private void setupDropDownButtons() {
        for (int i = 0; i < 8; i++) {
            dropdownButtons.add(findViewById(getResources().getIdentifier("dropDownButton" + (i + 1), "id", getPackageName())));
            dropdownButtons.get(i).setTag(i);
            layouts.add(findViewById(getResources().getIdentifier("fieldsLayout" + (i + 1), "id", getPackageName())));
            dropdownButtons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = (int) v.getTag();
                    if (layouts.get(i).getVisibility() == View.GONE) {
                        if (whichLayoutIsEnabled != -1) layouts.get(whichLayoutIsEnabled).setVisibility(View.GONE);
                        layouts.get(i).setVisibility(View.VISIBLE);
                        whichLayoutIsEnabled = i;
                    }
                    else {
                        layouts.get(i).setVisibility(View.GONE);
                        whichLayoutIsEnabled = -1;
                    }
                }
            });
        }
    }
}