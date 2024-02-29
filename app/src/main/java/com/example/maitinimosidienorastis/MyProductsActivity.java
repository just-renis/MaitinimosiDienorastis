package com.example.maitinimosidienorastis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyProductsActivity extends AppCompatActivity {

    private FloatingActionButton goToAddProductButton;
    private RecyclerView productsListView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private TextView noProductsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);
        goToAddProductButton = findViewById(R.id.goToAddProductActivity);
        productsListView = findViewById(R.id.productsListView);
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);;
        noProductsText = findViewById(R.id.noProductsText);
        productsListView.setAdapter(adapter);
        productsListView.setLayoutManager(new LinearLayoutManager(this));
        goToAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyProductsActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
        getMyProducts();
    }
    private void getMyProducts() {
        loadMyProducts(new ProductsLoadedCallback() {
            @Override
            public void onCallBack() {
                if (!productList.isEmpty()) noProductsText.setVisibility(View.GONE);
                else noProductsText.setVisibility(View.VISIBLE);
            }
        });
    }
    private void loadMyProducts(ProductsLoadedCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    productList.add(new Product(productSnapshot.child("name").getValue(String.class), productSnapshot.getKey(), false));
                }
                adapter.setShowcaseProductsList();
                adapter.notifyDataSetChanged();
                callback.onCallBack();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { callback.onCallBack(); }
        });
    }
}