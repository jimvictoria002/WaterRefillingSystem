package com.lu.waterrefilling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Arrays;

public class MainContainerActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance(); // Firebase Firestore instance
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new ProductsFragment();
            } else if (itemId == R.id.nav_orders) {
                selectedFragment = new OrderFragment();
            } else if (itemId == R.id.nav_archive) {
                selectedFragment = new ArchiveFragment();
            }else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment);
            }
            return true;
        });

        auth = FirebaseAuth.getInstance();

        // Load user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", null);

        if (userJson != null) {
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);
            Log.d("UserData", "Retrieved user: " + user.toString());
        } else {
            Log.d("UserData", "No user data found in SharedPreferences");
        }

        // Check if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }

        // Set default fragment to ProductsFragment
        if (savedInstanceState == null) {
            replaceFragment(new ProductsFragment()); // Do not add to back stack
        }

        // Listen for real-time changes in the orders collection

        if(user.getRole().equals("admin")){
            listenForOrdersUpdates();
        }else{
            listenForOrdersUpdates(user.getId());
        }
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainContainerActivity.this, MainActivity.class));
        finish();
    }

    /**
     * Replaces the current fragment.
     *
     * @param fragment The new fragment to display.
     */
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        // Check if the back stack is empty before exiting
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed(); // Exits the app
        }
    }



    private void listenForOrdersUpdates() {
        db.collection("orders")
                .whereNotIn("status", Arrays.asList("cancelled", "completed")) // Filter out 'cancelled' and 'completed' statuses
                .orderBy("orderAt", Query.Direction.DESCENDING) // Order by orderAt in descending order
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error fetching orders: ", e);
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        // Count active orders
                        int activeOrderCount = snapshot.size();

                        // Update badge count on the Orders tab
                        updateOrdersBadge(activeOrderCount);
                    } else {
                        // Remove badge if no active orders
                        updateOrdersBadge(0);
                        Toast.makeText(this, "No active orders found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void listenForOrdersUpdates(String userId) {
        db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereNotIn("status", Arrays.asList("cancelled", "completed")) // Filter out 'cancelled' and 'completed' statuses
                .orderBy("orderAt", Query.Direction.DESCENDING) // Order by orderAt in descending order
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Log.e("FirestoreError", "Error fetching orders: ", e);
                        return;
                    }

                    if (snapshot != null && !snapshot.isEmpty()) {
                        // Count active orders
                        int activeOrderCount = snapshot.size();

                        // Update badge count on the Orders tab
                        updateOrdersBadge(activeOrderCount);
                    } else {
                        // Remove badge if no active orders
                        updateOrdersBadge(0);
                        Toast.makeText(this, "No active orders found", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateOrdersBadge(int orderCount) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Ensure the badge is visible only if there are active orders
        if (orderCount > 0) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_orders);
            badge.setNumber(orderCount);
            badge.setVisible(true);
            badge.setVerticalOffset(4);
            badge.setHorizontalOffset(5);
        } else {
            bottomNavigationView.removeBadge(R.id.nav_orders); // Remove badge if no active orders
        }
    }
}
