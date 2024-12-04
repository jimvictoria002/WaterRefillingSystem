package com.example.waterrefilling;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CreateWater extends Fragment {

    private static final String TAG = "CreateWater";

    private EditText editTextName, editTextPrice;
    private Button buttonSubmit;
    private ProgressBar progressBar;

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
        progressBar = view.findViewById(R.id.progressBar);

        buttonSubmit.setOnClickListener(v -> createWaterProduct());

        return view;
    }

    private void createWaterProduct() {
        String name = editTextName.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if (name.isEmpty() || price.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        buttonSubmit.setEnabled(false);

        saveWaterProductData();
    }

    private void saveWaterProductData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> waterProductData = new HashMap<>();
        waterProductData.put("name", editTextName.getText().toString().trim());
        waterProductData.put("price", editTextPrice.getText().toString().trim());

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

    private void resetFields() {
        editTextName.setText("");
        editTextPrice.setText("");
        buttonSubmit.setEnabled(false);
    }
}
