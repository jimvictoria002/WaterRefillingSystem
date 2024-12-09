package com.advento.waterrefilling;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateWater extends Fragment {

    private static final String TAG = "CreateWater";
    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri selectedImageUri;

    private EditText editTextName, editTextPrice;
    private Button buttonSubmit, buttonChooseImage;
    private ProgressBar progressBar;
    private ImageView imageViewPreview;

    public CreateWater() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_water, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextPrice = view.findViewById(R.id.editTextPrice);
        buttonSubmit = view.findViewById(R.id.buttonSubmit);
//        buttonChooseImage = view.findViewById(R.id.imageViewPreview);
        progressBar = view.findViewById(R.id.progressBar);
        imageViewPreview = view.findViewById(R.id.imageViewPreview);

        imageViewPreview.setOnClickListener(v -> openImagePicker());
        buttonSubmit.setOnClickListener(v -> createWaterProduct());

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();

            // Use Picasso to preview the selected image
            Picasso.get()
                    .load(selectedImageUri)
                    .fit()
                    .centerCrop()
                    .into(imageViewPreview);
        }
    }

    private void createWaterProduct() {
        String name = editTextName.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty() || selectedImageUri == null) {
            Toast.makeText(getActivity(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show();
            return;
        }


        try {
            int parsedPrice = Integer.parseInt(price);
            if (parsedPrice > 1000) {
                Toast.makeText(getActivity(), "1,000 Php is the maximum price", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Invalid integer input", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmit.setEnabled(false);

        uploadImageToFirebase(name, price);
    }

    private void uploadImageToFirebase(String name, String price) {
        StorageReference storageReference = FirebaseStorage.getInstance()
                .getReference("waterProductsImage/" + System.currentTimeMillis() + ".jpg");

        storageReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveWaterProductData(name, price, uri.toString()))
                        .addOnFailureListener(e -> showError(e, "Error getting image URL")))
                .addOnFailureListener(e -> showError(e, "Error uploading image"));
    }

    private void saveWaterProductData(String name, String price, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> waterProductData = new HashMap<>();
        waterProductData.put("name", name);
        waterProductData.put("price", price);
        waterProductData.put("imageUrl", imageUrl);

        String currentDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        waterProductData.put("created_at", currentDateAndTime);

        db.collection("water_products")
                .add(waterProductData)
                .addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Water product created successfully", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Water product created with ID: " + documentReference.getId());
                    resetFields();
                    getActivity().getSupportFragmentManager().popBackStack();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    buttonSubmit.setEnabled(true);
                    Toast.makeText(getActivity(), "Error creating water product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error creating water product", e);
                });
    }

    private void showError(Exception e, String message) {
        progressBar.setVisibility(View.GONE);
        buttonSubmit.setEnabled(true);
        Toast.makeText(getActivity(), message + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, message, e);
    }

    private void resetFields() {
        editTextName.setText("");
        editTextPrice.setText("");
        imageViewPreview.setImageResource(R.drawable.ic_default_image); // Reset to default image
        selectedImageUri = null;
        buttonSubmit.setEnabled(false);
    }
}
