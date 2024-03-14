package com.example.maitinimosidienorastis;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MealLogActivity extends AppCompatActivity implements ProductViewListener{

    private Button productsButton, insertButton;
    private TextView dateTextPlaceholder, timeTextPlaceholder, productsTextPlaceholder, productsText, dateText, timeText;
    private Calendar calendar;
    private List<Product> allProducts, savedProducts;
    private boolean isProductDataFetched = false, isShowingProducts = false;
    private RecyclerView allProductsList;
    private ProductAdapter productAdapter;
    private EditText searchField;
    private ProgressBar productsProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_log);
        productsButton = findViewById(R.id.productsButton);
        insertButton = findViewById(R.id.insertButton);
        dateTextPlaceholder = findViewById(R.id.dateTextPlaceholder);
        timeTextPlaceholder = findViewById(R.id.timeTextPlaceholder);
        productsTextPlaceholder = findViewById(R.id.productsTextPlaceholder);
        dateText = findViewById(R.id.dateText);
        dateText.setPaintFlags(dateText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        timeText = findViewById(R.id.timeText);
        timeText.setPaintFlags(timeText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        productsText = findViewById(R.id.productsText);
        allProductsList = findViewById(R.id.allProductsList);
        allProductsList.setVisibility(View.GONE);
        searchField = findViewById(R.id.searchField);
        productsProgressBar = findViewById(R.id.productsProgressBar);
        calendar = Calendar.getInstance();
        allProducts = new ArrayList<>();
        savedProducts = new ArrayList<>();
        setupDateButton();
        setupTimeButton();
        setupProductsButton();
        setupInsertButton();
        setupOnBackPressedButton();
    }
    private void setupDateButton() {
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MealLogActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                setDateText(year, month, dayOfMonth);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }
    private void setDateText(int year, int month, int day) {
        switch(month) {
            case 0: dateText.setText(year + " Sausio " + day); return;
            case 1: dateText.setText(year + " Vasario " + day); return;
            case 2: dateText.setText(year + " Kovo " + day); return;
            case 3: dateText.setText(year + " Balandžio " + day); return;
            case 4: dateText.setText(year + " Gegužės " + day); return;
            case 5: dateText.setText(year + " Birželio " + day); return;
            case 6: dateText.setText(year + " Liepos " + day); return;
            case 7: dateText.setText(year + " Rugpjūčio " + day); return;
            case 8: dateText.setText(year + " Rugsėjo " + day); return;
            case 9: dateText.setText(year + " Spalio " + day); return;
            case 10: dateText.setText(year + " Lapkričio " + day); return;
            default: dateText.setText(year + " Gruodžio " + day);
        }
    }
    private void setupTimeButton() {
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MealLogActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeText.setText(hourOfDay + " val. " + minute + " min.");
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });
    }
    private void setupProductsButton() {
        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isProductDataFetched) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        productsProgressBar.setVisibility(View.VISIBLE);
                        receiveUserProducts(currentUser);
                        receiveLocalProducts();
                    }
                }
                else setVisibility(isShowingProducts);
            }
        });
    }
    @Override
    public void onItemClick(Product product) {
        savedProducts.add(product);
        setVisibility(true);
    }
    private void setVisibility(boolean state) {
        isShowingProducts = !state;
        searchField.setText("");
        searchField.setVisibility(state ? View.GONE : View.VISIBLE);
        allProductsList.setVisibility(state ? View.GONE : View.VISIBLE);
        dateTextPlaceholder.setVisibility(state ? View.VISIBLE : View.GONE);
        timeTextPlaceholder.setVisibility(state ? View.VISIBLE : View.GONE);
        productsTextPlaceholder.setVisibility(state ? View.VISIBLE : View.GONE);
        dateText.setVisibility(state ? View.VISIBLE : View.GONE);
        timeText.setVisibility(state ? View.VISIBLE : View.GONE);
        productsText.setVisibility(state ? View.VISIBLE : View.GONE);
        productsButton.setVisibility(state ? View.VISIBLE : View.GONE);
        insertButton.setVisibility(state ? View.VISIBLE : View.GONE);
        StringBuilder savedProductsText = new StringBuilder(savedProducts.size() > 0 ? "1. " + savedProducts.get(0).getName() : "");
        for (int i = 1; i < savedProducts.size(); i++) {
            savedProductsText.append("\n").append((i + 1)).append(". ").append(savedProducts.get(i).getName());
        }
        productsText.setText(savedProductsText);
    }
    private void setupProductAdapter() {
        productAdapter = new ProductAdapter(allProducts, MealLogActivity.this);
        allProductsList.setAdapter(productAdapter);
        allProductsList.setLayoutManager(new LinearLayoutManager(MealLogActivity.this));
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) { productAdapter.filter(cs.toString()); }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
            @Override
            public void afterTextChanged(Editable arg0) { }
        });
        setVisibility(false);
        productAdapter.notifyDataSetChanged();
    }
    private void setupInsertButton() {
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(dateText.getText().toString().replace("Nėra pasirinktos datos", ""))) {
                    Toast.makeText(MealLogActivity.this, "Pasirinkite datą", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(timeText.getText().toString().replace("Nėra pasirinkto laiko", ""))) {
                    Toast.makeText(MealLogActivity.this, "Pasirinkite laiką", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (savedProducts.size() < 1) {
                    Toast.makeText(MealLogActivity.this, "Pasirinkite nors vieną produktą", Toast.LENGTH_SHORT).show();
                    return;
                }
                insertMealTimeStamp();
            }
        });
    }
    private void insertMealTimeStamp()
    {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("mealTimeStamps");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> personalProductsKeys = new ArrayList<>();
                    ArrayList<String> localProductsKeys = new ArrayList<>();
                    for (int i = 0; i < savedProducts.size(); i++)
                    {
                        if (savedProducts.get(i).getIsMineNeededToShow()) personalProductsKeys.add(savedProducts.get(i).getKey());
                        else localProductsKeys.add(savedProducts.get(i).getKey());
                    }
                    MealTimeStamp mealTimeStamp = new MealTimeStamp(getFormattedDate(), getFormattedTime(), personalProductsKeys, localProductsKeys);
                    ref.push().setValue(mealTimeStamp);
                    Toast.makeText(MealLogActivity.this, "Išsaugota", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
    }
    private void receiveUserProducts(FirebaseUser currentUser) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid()).child("products");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {
                    allProducts.add(new Product(productSnapshot.child("name").getValue(String.class), productSnapshot.getKey(), true));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    private void receiveLocalProducts() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("products");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {
                    allProducts.add(new Product(productSnapshot.child("Lithuanian name").getValue(String.class), productSnapshot.getKey(), false));
                }
                isProductDataFetched = true;
                productsProgressBar.setVisibility(View.GONE);
                if (productAdapter == null) setupProductAdapter();
                else productAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    private void setupOnBackPressedButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isShowingProducts) setVisibility(isShowingProducts);
                else finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }
    private String getFormattedDate() {
        String[] parts = dateText.getText().toString().split(" ");
        return String.format(Locale.getDefault(), "%d %d %d", Integer.parseInt(parts[0]), getMonthIndex(parts[1]), Integer.parseInt(parts[2]));
    }
    private int getMonthIndex(String monthName) {
        switch (monthName) {
            case "Sausio":
                return 1;
            case "Vasario":
                return 2;
            case "Kovo":
                return 3;
            case "Balandžio":
                return 4;
            case "Gegužės":
                return 5;
            case "Birželio":
                return 6;
            case "Liepos":
                return 7;
            case "Rugpjūčio":
                return 8;
            case "Rugsėjo":
                return 9;
            case "Spalio":
                return 10;
            case "Lapkričio":
                return 11;
            case "Gruodžio":
                return 12;
            default:
                return 1;
        }
    }
    private String getFormattedTime() {
        String[] timeParts = timeText.getText().toString().split(" ");
        return String.format(Locale.getDefault(), "%d %d", Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[2]));
    }
}