package com.example.maitinimosidienorastis;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity {

    private Map<String, TextInputEditText> editFieldsMap = new HashMap<>();
    Button addProductButton;
    ArrayList<Button> dropdownButtons = new ArrayList<>();
    ArrayList<LinearLayout> layouts = new ArrayList<>();
    int whichLayoutIsEnabled = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        setFiltersForInputFields();
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
                    Intent intent = new Intent(AddProductActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
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
    private void addProductToDB(FirebaseUser currentUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("products");
        isProductNameExists(new ProductNameExistsCallback() {
            @Override
            public void onCallBack(boolean productNameExists) {
                if (productNameExists) Toast.makeText(AddProductActivity.this, "Produktas su tuo pačiu pavadinimu jau egzistuoja", Toast.LENGTH_SHORT).show();
                else {
                    Map<String, String> fieldValues = editFieldsMap.entrySet().stream()
                            .collect(Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> {
                                        String value = entry.getValue().getText().toString().trim();
                                        return value.isEmpty() ? "No Data" : value;
                                    }
                            ));
                    Product product = new Product(fieldValues);
                    ref.push().setValue(product);
                    Toast.makeText(AddProductActivity.this, "Produktas sėkmingai pridėtas", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }, ref);
    }
    private void isProductNameExists(ProductNameExistsCallback callback, DatabaseReference ref) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String newProductName = editFieldsMap.get("name").getText().toString().trim();
                boolean exists = false;
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    String existingProductName = productSnapshot.child("name").getValue(String.class);
                    if (existingProductName != null && existingProductName.equals(newProductName)) {
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