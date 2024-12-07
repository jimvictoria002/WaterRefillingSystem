package com.lu.waterrefilling;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private WaterProductAdapter adapter;
    private List<WaterProduct> waterProductsList;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Firestore and list
        db = FirebaseFirestore.getInstance();
        waterProductsList = new ArrayList<>();

        MainContainerActivity mca = (MainContainerActivity) getActivity();

        // Initialize Adapter
        adapter = new WaterProductAdapter(getContext(), waterProductsList, mca);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firestore
        fetchWaterProducts();

        Button createBtn = view.findViewById(R.id.create_water_product);
        String role = mca.user.getRole();

        if (!role.equals("admin")) {
            createBtn.setVisibility(View.GONE);
        } else {
            createBtn.setVisibility(View.VISIBLE);
            createBtn.setOnClickListener(v -> {
                mca.replaceFragment(new CreateWater());
            });
        }


        return view;
    }

    private void fetchWaterProducts() {
        db.collection("water_products")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Convert Firestore document to WaterProduct object
                            WaterProduct waterProduct = document.toObject(WaterProduct.class);
                            if (waterProduct != null) {
                                // Optionally set additional fields like Firestore ID
                                waterProduct.setId(document.getId());
                                waterProductsList.add(waterProduct);
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update adapter
                    } else {
                        Toast.makeText(getContext(), "No products found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to fetch products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProductsFragment", "Error fetching products", e);
                });
    }
}
