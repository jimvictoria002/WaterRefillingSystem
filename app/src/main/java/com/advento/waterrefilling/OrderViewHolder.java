package com.advento.waterrefilling;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OrderViewHolder  extends RecyclerView.ViewHolder  {

    public TextView productName, bookerName, bookingDate, address, price, total, orderStatus, totalQty;
    public CheckBox isPaid;
    public Spinner statusSpinner;

    public ProgressBar statusProgressBar;
    public Button orderStatusButton, deleteButton;
    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        // Bind views
        productName = itemView.findViewById(R.id.product_name);
        bookerName = itemView.findViewById(R.id.customer_name);
        bookingDate = itemView.findViewById(R.id.order_date);
        address = itemView.findViewById(R.id.adress);
        price = itemView.findViewById(R.id.price);
        totalQty = itemView.findViewById(R.id.total_qty);
        total = itemView.findViewById(R.id.total);
        orderStatus = itemView.findViewById(R.id.order_status);
        isPaid = itemView.findViewById(R.id.isPaid);
        statusProgressBar = itemView.findViewById(R.id.status_progress_bar);
        orderStatusButton = itemView.findViewById(R.id.update_status);
        deleteButton = itemView.findViewById(R.id.delete_btn);
    }
}
