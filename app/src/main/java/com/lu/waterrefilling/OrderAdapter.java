package com.lu.waterrefilling;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private final List<Order> orderList;
    private final Context context;
    private final User user;
    private final MainContainerActivity mca;
    private final FirebaseFirestore db;
    // Status options for the Spinner
    private final String[] statusOptions = {"Pending", "Accepted", "Processing", "Delivering", "Completed", "Cancelled"};

    public OrderAdapter(Context context, List<Order> orderList, MainContainerActivity mca) {
        this.context = context;
        this.orderList = orderList;
        this.user = mca.user;
        this.mca = mca;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.order_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);


        Log.d("OrdersObject", order.toString());

        holder.address.setText(order.getAddress());
        holder.bookerName.setText(order.getOrderBy());
        holder.bookingDate.setText(order.orderAtFormatted());
        holder.productName.setText(order.getProductName());
        holder.price.setText(order.getPrice());
        holder.totalQty.setText("Total " + order.getQuantity() + " quantity");
        holder.orderStatus.setText(order.getStatus().substring(0, 1).toUpperCase() + order.getStatus().substring(1));
        holder.isPaid.setChecked(order.getIsPaid());
        holder.total.setText(String.valueOf(order.getTotal()) + " Php");


        if ((mca.user.getRole().equals("user") && order.getStatus().equals("pending"))) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                // Create a confirmation dialog
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Update")
                        .setMessage("Are you sure you want to cancel the order??")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            String selectedStatus = "cancelled";
                            db.collection("orders").document(order.getOrderId())
                                    .update("status", selectedStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        notifyDataSetChanged();

                                        Toast.makeText(context, "Status updated to " + selectedStatus, Toast.LENGTH_SHORT).show();

                                        Log.d("FirestoreUpdate", "Order " + order.getOrderId() + " status updated to " + selectedStatus);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreUpdate", "Error updating order " + order.getOrderId(), e);
                                        Toast.makeText(context, "Failed to update status in database", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Dismiss the dialog if "No" is clicked
                            dialog.dismiss();
                        })
                        .show();
            });

        } else {
            holder.deleteButton.setVisibility(View.GONE);

        }

        // Track the previously selected status for comparison
        if (mca.user.getRole().equals("admin")) {
            holder.orderStatusButton.setVisibility(View.VISIBLE);
            holder.orderStatusButton.setOnClickListener(v -> {
                // Create an AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
                builder.setTitle("Select Status")
                        .setItems(statusOptions, (dialog, which) -> {
                            // Handle the selected status option
                            String selectedStatus = statusOptions[which].toLowerCase();
                            // You can update a TextView or other UI elements with the selected status
                            db.collection("orders").document(order.getOrderId())
                                    .update("status", selectedStatus)
                                    .addOnSuccessListener(aVoid -> {
                                        if (orderList.size() > 1) {
                                            notifyDataSetChanged();

                                        } else {
                                            mca.replaceFragment(new OrderFragment());
                                        }
                                        Toast.makeText(context, "Status update to " + selectedStatus, Toast.LENGTH_SHORT).show();

                                        Log.d("FirestoreUpdate", "Order " + order.getOrderId() + " status updated to " + selectedStatus);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("FirestoreUpdate", "Error updating order " + order.getOrderId(), e);
                                        Toast.makeText(context, "Failed to update status in database", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .setCancelable(true)
                        .show();
            });

        } else {
            holder.orderStatusButton.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
