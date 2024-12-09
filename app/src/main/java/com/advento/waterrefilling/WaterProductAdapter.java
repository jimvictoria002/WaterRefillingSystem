package com.advento.waterrefilling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;


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

        if (user.getRole().equals("admin")) {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.orderButton.setVisibility(View.GONE);
            holder.decreaseButton.setVisibility(View.GONE);
            holder.increaseButton.setVisibility(View.GONE);
            holder.totalText.setVisibility(View.GONE);
            holder.quantityText.setVisibility(View.GONE);
            holder.paymentMode.setVisibility(View.GONE);
        } else {
            holder.orderButton.setVisibility(View.VISIBLE);
            holder.editButton.setVisibility(View.GONE);
            holder.decreaseButton.setVisibility(View.VISIBLE);
            holder.increaseButton.setVisibility(View.VISIBLE);
            holder.totalText.setVisibility(View.VISIBLE);
            holder.quantityText.setVisibility(View.VISIBLE);
            holder.paymentMode.setVisibility(View.VISIBLE);

        }

        holder.productName.setText(waterProduct.getName());
        holder.priceText.setText("Price: " + waterProduct.getPrice() + " Php");
        holder.quantityText.setText(String.valueOf(waterProduct.getQuantity()));
        holder.imageProgressBar.setVisibility(View.VISIBLE); // Show ProgressBar when loading starts
        Picasso.get()
                .load(waterProduct.getImageUrl())
                .into(holder.productImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        holder.imageProgressBar.setVisibility(View.GONE); // Hide on success
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.imageProgressBar.setVisibility(View.GONE); // Hide on error
                    }
                });

        double total = Integer.parseInt(waterProduct.getPrice()) * waterProduct.getQuantity();
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


        holder.editButton.setOnClickListener(v -> {
            mca.replaceFragment(new EditWaterProduct(waterProduct));
        });

        if(user.getRole().equals("admin")){
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> {
                new MaterialAlertDialogBuilder(context)
                        .setTitle("Delete water product")
                        .setMessage("Are you sure you want to delete this water product?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            holder.deleteButton.setEnabled(false);
                            holder.progressBar.setVisibility(View.VISIBLE);
                            // Get the document ID and image URL
                            String docId = waterProduct.getId();
                            String imageUrl = waterProduct.getImageUrl();  // Assuming the image URL is stored in the document

                            // Reference the Firestore document
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference docRef = db.collection("water_products").document(docId);

                            // Reference the image in Firebase Storage
                            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                            // Delete the image from Firebase Storage
                            storageRef.delete()
                                    .addOnSuccessListener(aVoid -> {
                                        // Image deleted successfully, now delete the document from Firestore
                                        docRef.delete()
                                                .addOnSuccessListener(aVoid1 -> {
                                                    // Document successfully deleted
                                                    Toast.makeText(context, "Product deleted", Toast.LENGTH_SHORT).show();
                                                    holder.progressBar.setVisibility(View.GONE);

                                                    mca.replaceFragment(new ProductsFragment());

                                                })
                                                .addOnFailureListener(e -> {
                                                    // Error deleting document
                                                    holder.progressBar.setVisibility(View.GONE);

                                                    Toast.makeText(context, "Error deleting water product document", Toast.LENGTH_SHORT).show();
                                                });
                                    })
                                    .addOnFailureListener(e -> {
                                        holder.progressBar.setVisibility(View.GONE);

                                        // Error deleting image from Firebase Storage
                                        Toast.makeText(context, "Error deleting image", Toast.LENGTH_SHORT).show();
                                    });

                        })
                        .setNegativeButton("No", null)
                        .show();
            });

        }else {
            holder.deleteButton.setVisibility(View.GONE);
        }



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
                            order.put("userId", user.getId());
                            order.put("address", user.getAddress());
                            order.put("isPaid", false);
                            order.put("status", "pending");
                            order.put("orderAt", System.currentTimeMillis());

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
