package com.example.maitinimosidienorastis;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AddProductActivity extends AppCompatActivity {

    private Map<String, TextInputEditText> editFieldsMap = new HashMap<>();
    Button addProductButton;
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
                    Intent intent = new Intent(AddProductActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
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
}