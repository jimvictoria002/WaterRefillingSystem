package com.lu.waterrefilling;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
public class EditWaterProduct extends Fragment {

    public WaterProduct wp;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private Button buttonSubmit;
    private ImageView imageViewPreview; // Reference to the ImageView for preview

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for picking an image
    private Uri imageUri; // Uri to store the selected image

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

        // Get references to the EditText fields, Submit button, ProgressBar, and ImageView
        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
        progressBar = view.findViewById(R.id.progressBar);
        imageViewPreview = view.findViewById(R.id.imageViewPreview);  // Reference to the ImageView

        imageViewPreview.setOnClickListener(v -> openImagePicker());

        // Set the values from the WaterProduct object
        if (wp != null) {
            editTextName.setText(wp.getName());
            editTextPrice.setText(String.valueOf(wp.getPrice())); // Assuming price is a numeric value

            ProgressBar imageProgressbar = view.findViewById(R.id.image_progress_bar);

            // Load the image URL into the ImageView using Picasso only if URL exists
            if (wp.getImageUrl() != null && !wp.getImageUrl().isEmpty()) {
                imageProgressbar.setVisibility(View.VISIBLE);
                Picasso.get()
                        .load(wp.getImageUrl()) // URL from Firestore
                        .placeholder(R.drawable.ic_default_image)  // Default image while loading
                        .into(imageViewPreview, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                imageProgressbar.setVisibility(View.GONE); // Hide on success
                            }

                            @Override
                            public void onError(Exception e) {
                                imageProgressbar.setVisibility(View.GONE); // Hide on error
                            }
                        });  // ImageView to display the image
            } else {
                imageViewPreview.setImageResource(R.drawable.ic_default_image);  // Default image if no URL is provided
            }
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
                wp.setName(updatedName);
                wp.setPrice(updatedPriceStr);

                // Show the progress bar and disable the submit button
                progressBar.setVisibility(View.VISIBLE);
                buttonSubmit.setEnabled(false);

                // Call method to update WaterProduct details and image
                updateWaterProduct(updatedName, updatedPriceStr);
            } catch (NumberFormatException e) {
                // Hide the progress bar and enable the button
                progressBar.setVisibility(View.GONE);
                buttonSubmit.setEnabled(true);

                Toast.makeText(getContext(), "Please enter a valid price", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Method to update the WaterProduct document in Firestore
    private void updateWaterProduct(String updatedName, String updatedPriceStr) {
        // Get reference to the specific document in the water_products collection
        DocumentReference waterProductRef = db.collection("water_products").document(wp.getId());

        // Update the document in Firestore
        waterProductRef.update("name", updatedName, "price", updatedPriceStr)
                .addOnSuccessListener(aVoid -> {
                    // Show success message
                    MainContainerActivity mca = ((MainContainerActivity) getActivity());
                    if (imageUri != null) {
                        // Check if there is an existing image URL and delete the old image
                        if (wp.getImageUrl() != null && !wp.getImageUrl().isEmpty()) {
                            // Get reference to the old image in Firebase Storage
                            StorageReference oldImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(wp.getImageUrl());
                            // Delete the old image
                            oldImageRef.delete().addOnSuccessListener(aVoid1 -> {
                                // Image deleted successfully, now upload the new image
                                uploadNewImage(mca, imageUri, wp, waterProductRef);
                            }).addOnFailureListener(e -> {
                                // Handle error in deleting the old image
                                Toast.makeText(getContext(), "Failed to delete old image", Toast.LENGTH_SHORT).show();
                                // Proceed to upload the new image even if deletion fails
                                uploadNewImage(mca, imageUri, wp, waterProductRef);
                            });
                        } else {
                            // No existing image, directly upload the new one
                            uploadNewImage(mca, imageUri, wp, waterProductRef);
                        }
                    } else {
                        // No new image, just update the product information
                        Toast.makeText(getContext(), "Water product updated", Toast.LENGTH_SHORT).show();
                        mca.replaceFragment(new ProductsFragment());

                        // Hide the progress bar and enable the button
                        progressBar.setVisibility(View.GONE);
                        buttonSubmit.setEnabled(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // Hide the progress bar and enable the button
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);

                    // Show error message
                    Toast.makeText(getContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadNewImage(MainContainerActivity mca, Uri imageUri, WaterProduct wp, DocumentReference waterProductRef) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("waterProductsImage/" + wp.getId());
        storageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Get the download URL of the uploaded image
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the WaterProduct object with the new image URL
                        wp.setImageUrl(uri.toString());

                        // Update the Firestore document with the image URL
                        waterProductRef.update("imageUrl", wp.getImageUrl())
                                .addOnSuccessListener(bVoid -> {
                                    Toast.makeText(getContext(), "Water product updated", Toast.LENGTH_SHORT).show();

                                    // Hide the progress bar and enable the button
                                    progressBar.setVisibility(View.GONE);
                                    buttonSubmit.setEnabled(true);
                                    mca.replaceFragment(new ProductsFragment());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Image edit failed", Toast.LENGTH_SHORT).show();

                                    // Hide the progress bar and enable the button
                                    progressBar.setVisibility(View.GONE);
                                    buttonSubmit.setEnabled(true);
                                });
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                });
    }
    // Method to open the image picker
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle the result from the image picker
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imageUri = data.getData();
            imageViewPreview.setImageURI(imageUri); // Set the selected image to the ImageView

            // Upload the selected image
        }
    }
}

