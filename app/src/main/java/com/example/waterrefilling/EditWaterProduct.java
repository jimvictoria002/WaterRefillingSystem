package com.example.waterrefilling;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

public class EditWaterProduct extends Fragment {

    public WaterProduct wp;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button buttonSubmit;

    public EditWaterProduct(WaterProduct wp) {
        this.wp = wp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_water_product, container, false);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Get references to the EditText fields, Submit button, and ProgressBar
        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        progressBar = view.findViewById(R.id.progressBar);

        // Set the values from the WaterProduct object
        if (wp != null) {
            editTextName.setText(wp.getName());
            editTextPrice.setText(String.valueOf(wp.getPrice())); // Assuming price is a numeric value
        }

        // Set onClickListener for the Submit button
        buttonSubmit.setOnClickListener(v -> {
            // Get the updated values from EditText fields
            String updatedName = editTextName.getText().toString().trim();
            String updatedPriceStr = editTextPrice.getText().toString().trim();

            // Validate input fields
            if (updatedName.isEmpty() || updatedPriceStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Update the WaterProduct object
                double updatedPrice = Double.parseDouble(updatedPriceStr);
                wp.setName(updatedName);
                wp.setPrice(updatedPrice);

                // Show the progress bar and disable the submit button
                progressBar.setVisibility(View.VISIBLE);
                buttonSubmit.setEnabled(false);

                // Get reference to the specific document in the water_products collection
                DocumentReference waterProductRef = db.collection("water_products").document(wp.getId());

                // Update the document in Firestore
                waterProductRef.update("name", updatedName, "price", updatedPrice)
                        .addOnSuccessListener(aVoid -> {
                            // Hide the progress bar and enable the button
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);

                            // Show success message
                            Toast.makeText(getContext(), "Water product updated", Toast.LENGTH_SHORT).show();

                            // Optionally, handle further logic like returning to the previous screen
                        })
                        .addOnFailureListener(e -> {
                            // Hide the progress bar and enable the button
                            progressBar.setVisibility(View.GONE);
                            buttonSubmit.setEnabled(true);

                            // Show error message
                            Toast.makeText(getContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                        });

            } catch (NumberFormatException e) {
                // Hide the progress bar and enable the button
                progressBar.setVisibility(View.GONE);
                buttonSubmit.setEnabled(true);

                Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
