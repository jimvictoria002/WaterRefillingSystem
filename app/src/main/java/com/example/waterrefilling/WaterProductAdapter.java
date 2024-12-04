package com.example.waterrefilling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.AlertDialog;
import android.content.DialogInterface;


public class WaterProductAdapter extends RecyclerView.Adapter<WaterProductViewHolder> {

    Context context;
    List<WaterProduct> waterProductsList;
    User user;

    MainContainerActivity mca;
    public WaterProductAdapter(Context context, List<WaterProduct> waterProductsList, MainContainerActivity mca) {
        this.context = context;
        this.waterProductsList = waterProductsList;
        this.user = mca.user;
        this.mca = mca;
    }

    @NonNull
    @Override
    public WaterProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WaterProductViewHolder(LayoutInflater.from(context).inflate(R.layout.product_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WaterProductViewHolder holder, int position) {
        WaterProduct waterProduct = waterProductsList.get(position);

        if(user.getRole().equals("admin")){
            holder.editButton.setVisibility(View.VISIBLE);
            holder.orderButton.setVisibility(View.GONE);
            holder.decreaseButton.setVisibility(View.GONE);
            holder.increaseButton.setVisibility(View.GONE);
            holder.totalText.setVisibility(View.GONE);
            holder.quantityText.setVisibility(View.GONE);
        }else{
            holder.orderButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.GONE);
            holder.decreaseButton.setVisibility(View.VISIBLE);
            holder.increaseButton.setVisibility(View.VISIBLE);
            holder.totalText.setVisibility(View.VISIBLE);
            holder.quantityText.setVisibility(View.VISIBLE);
        }

        holder.productName.setText(waterProduct.getName());
        holder.priceText.setText("Price: " + waterProduct.getPrice() + " Php");
        holder.quantityText.setText(String.valueOf(waterProduct.getQuantity()));
        holder.productImage.setImageResource(waterProduct.getImageResourceId());

        double total = waterProduct.getPrice() * waterProduct.getQuantity();
        holder.totalText.setText("Total: " + total + " Php");

        // Update quantity handlers
        holder.decreaseButton.setOnClickListener(v -> {
            if (waterProduct.getQuantity() > 1) {
                waterProduct.setQuantity(waterProduct.getQuantity() - 1);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show();
            }
        });

        holder.increaseButton.setOnClickListener(v -> {
            waterProduct.setQuantity(waterProduct.getQuantity() + 1);
            notifyItemChanged(position);
        });


        holder.editButton.setOnClickListener(v ->{
            mca.replaceFragment(new EditWaterProduct(waterProduct));
        });

        // Handle the order button
        holder.orderButton.setOnClickListener(v -> {
            if (user != null) {
                // Create the confirmation dialog
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Order")
                        .setMessage("Name: " + waterProduct.getName() + "\n" +
                                "Price: " + waterProduct.getPrice() + " Php\n" +
                                "Quantity: " + waterProduct.getQuantity() + "\n" +
                                "Total Price: " + total + " Php\n" +
                                "Address: " + user.getAddress())
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            // Disable button and show progress bar
                            holder.orderButton.setEnabled(false);
                            holder.progressBar.setVisibility(View.VISIBLE);

                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            // Create order data
                            Map<String, Object> order = new HashMap<>();
                            order.put("productName", waterProduct.getName());
                            order.put("price", waterProduct.getPrice());
                            order.put("quantity", waterProduct.getQuantity());
                            order.put("total", total);
                            order.put("orderBy", user.getFullname());
                            order.put("address", user.getAddress());
                            order.put("order_at", System.currentTimeMillis());

                            // Send order to Firestore
                            db.collection("orders")
                                    .add(order)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show();
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.orderButton.setEnabled(true);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to place order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        holder.progressBar.setVisibility(View.GONE);
                                        holder.orderButton.setEnabled(true);
                                    });
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            } else {
                Toast.makeText(context, "User not found. Please log in.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public int getItemCount() {
        return waterProductsList.size();
    }
}
