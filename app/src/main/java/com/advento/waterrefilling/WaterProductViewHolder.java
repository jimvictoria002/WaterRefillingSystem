package com.advento.waterrefilling;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WaterProductViewHolder extends RecyclerView.ViewHolder {

    // Define the view elements from the XML layout
    public ImageView productImage;
    public TextView productName;
    public TextView priceText;
    public TextView quantityText, paymentMode;
    public TextView totalText;
    public Button decreaseButton;
    public Button increaseButton;
    public Button orderButton;
    public Button editButton, deleteButton;
    public ProgressBar progressBar, imageProgressBar;

    public WaterProductViewHolder(@NonNull View itemView) {
        super(itemView);

        // Bind the views using findViewById
        productImage = itemView.findViewById(R.id.product_image); // Replace with actual ID
        productName = itemView.findViewById(R.id.product_name); // Replace with actual ID
        priceText = itemView.findViewById(R.id.tv_price_2);
        quantityText = itemView.findViewById(R.id.tv_quantity_2);
        totalText = itemView.findViewById(R.id.tv_total_2);
        decreaseButton = itemView.findViewById(R.id.btn_decrease_2);
        increaseButton = itemView.findViewById(R.id.btn_increase_2);
        orderButton = itemView.findViewById(R.id.btn_order_2);
        editButton = itemView.findViewById(R.id.edit_btn);
        progressBar = itemView.findViewById(R.id.progress_bar);
        imageProgressBar = itemView.findViewById(R.id.image_progress_bar);
        deleteButton = itemView.findViewById(R.id.delete_btn);
        paymentMode = itemView.findViewById(R.id.tv_payment_mode);
    }
}
