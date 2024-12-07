package com.lu.waterrefilling;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArchiveFragment extends Fragment {

    private FirebaseFirestore db;
    private List<Order> orderList;

    private RecyclerView recyclerView;
    private OrderAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, container, false);

        recyclerView = view.findViewById(R.id.order_recyler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();


        MainContainerActivity mca = (MainContainerActivity) getActivity();

        if (mca.user.getRole().equals("admin")) {
            fetchOrders();

        } else {
            fetchOrders(mca.user.getId());
        }


        // Initialize Adapter
        adapter = new OrderAdapter(getContext(), orderList, mca);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void fetchOrders() {
        db.collection("orders")
                .whereIn("status", Arrays.asList("cancelled", "completed")) // Filter out 'cancelled' and 'completed' statuses
                .orderBy("orderAt", Query.Direction.DESCENDING) // Order by orderAt in descending order
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Failed to fetch orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("OrdersFragment", "Error fetching orders", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        orderList.clear(); // Clear the list before adding new data
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Convert Firestore document to Order object
                            Order order = document.toObject(Order.class);
                            if (order != null) {
                                // Optionally set additional fields like Firestore ID
                                order.setOrderId(document.getId());
                                orderList.add(order);
                                Log.d("OrderList", order.toString());
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update adapter
                    } else {
                        orderList.clear(); // Clear the list if no documents are found
                        adapter.notifyDataSetChanged(); // Refresh the adapter
                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchOrders(String userId) {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereIn("status", Arrays.asList("cancelled", "completed")) // Filter out 'cancelled' and 'completed' statuses
                .orderBy("orderAt", Query.Direction.DESCENDING) // Order by orderAt in descending order
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Failed to fetch orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("OrdersFragment", "Error fetching orders", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        orderList.clear(); // Clear the list before adding new data
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document : documents) {
                            // Convert Firestore document to Order object
                            Order order = document.toObject(Order.class);
                            if (order != null) {
                                // Optionally set additional fields like Firestore ID
                                order.setOrderId(document.getId());
                                orderList.add(order);
                                Log.d("OrderList", order.toString());
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update adapter
                    } else {
                        orderList.clear(); // Clear the list if no documents are found
                        adapter.notifyDataSetChanged(); // Refresh the adapter
                        Toast.makeText(getContext(), "No orders found", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
