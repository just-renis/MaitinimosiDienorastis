package com.example.maitinimosidienorastis;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> allProductsList;
    private List<Product> showcaseProductsList;
    private ProductViewListener listener;
    private boolean isClickEnabled = false;
    public ProductAdapter(List<Product> productsList) {
        this.allProductsList = productsList;
        this.showcaseProductsList = new ArrayList<>(productsList);
    }
    public ProductAdapter(List<Product> productsList, ProductViewListener listener) {
        this.allProductsList = productsList;
        this.showcaseProductsList = new ArrayList<>(productsList);
        this.isClickEnabled = true;
        this.listener = listener;
    }
    public Product getProduct(int position) { return this.showcaseProductsList.get(position); }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.product_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = showcaseProductsList.get(position);
        holder.productId.setText(String.valueOf(position + 1));
        holder.productName.setText(product.getName());
        if (this.isClickEnabled) {
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(showcaseProductsList.get(holder.getAdapterPosition()));
                }
            });
            holder.deleteLayout.setVisibility(View.GONE);
            holder.editLayout.setVisibility(View.GONE);
        }
        else {
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("products");
                    productsRef.child(product.getKey()).removeValue();
                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), UpdateProductActivity.class);
                    intent.putExtra("PRODUCT_KEY", product.getKey());
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return Math.min(showcaseProductsList.size(), 10);
    }

    public void filter(String text) {
        this.showcaseProductsList.clear();
        if (text.isEmpty()) this.showcaseProductsList.addAll(this.allProductsList);
        else {
            for (Product item: this.allProductsList) {
                if (item.getName().toLowerCase().contains(text)) this.showcaseProductsList.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productId, productName;
        Button deleteButton, editButton;
        ConstraintLayout editLayout, deleteLayout;
        LinearLayout mainView;
        public ProductViewHolder(@NonNull View productView) {
            super(productView);
            productId = productView.findViewById(R.id.productId);
            productName = productView.findViewById(R.id.productName);
            deleteButton = productView.findViewById(R.id.deleteButton);
            deleteLayout = productView.findViewById(R.id.deleteLayout);
            editButton = productView.findViewById(R.id.editButton);
            editLayout = productView.findViewById(R.id.editLayout);
            mainView = productView.findViewById(R.id.mainView);
        }
    }

    public void setShowcaseProductsList() { this.showcaseProductsList = this.allProductsList; }
}
