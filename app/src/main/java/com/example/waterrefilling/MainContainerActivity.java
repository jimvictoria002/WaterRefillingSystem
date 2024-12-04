package com.example.waterrefilling;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

public class MainContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private FirebaseAuth auth;
    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    private Toolbar toolbar;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        navigationView.bringToFront();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Load user data from SharedPreferences
        // Retrieve the shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);

        String userJson = sharedPreferences.getString("user", null);

        if (userJson != null) {
            // Convert the JSON string back to a User object using Gson
            Gson gson = new Gson();
            user = gson.fromJson(userJson, User.class);

            // Now you can access the User object and its fields
            Log.d("UserData", "Retrieved user: " + user.toString());
        } else {
            // Handle case where the user data doesn't exist or is not found
            Log.d("UserData", "No user data found in SharedPreferences");
        }


        // Set email and role in navigation header
        setupHeaderView();

        // Check if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            redirectToLogin();
        }

        // Handle back button press
        handleBackButton();

        // Set default fragment
        if (savedInstanceState == null) {
            replaceFragment(new Homepage());
        }
    }

    private void setupHeaderView() {
        View headerView = navigationView.getHeaderView(0);
        TextView userEmailText = headerView.findViewById(R.id.user_email_text);
        TextView userRole = headerView.findViewById(R.id.user_role);
        userEmailText.setText(user.getEmail());
        userRole.setText(user.getRole());
    }

    private void redirectToLogin() {
        Toast.makeText(this, "Please log in", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainContainerActivity.this, MainActivity.class));
        finish();
    }

    private void handleBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    finish();
                }
            }
        });
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String selected;
        try {
            selected = getResources().getResourceEntryName(item.getItemId());
        } catch (Resources.NotFoundException e) {
            selected = "Unknown";
        }

        switch (selected) {
            case "nav_home":
                replaceFragment(new Homepage());
                break;
            case "nav_products":
                replaceFragment(new ProductsFragment());
                break;
            case "nav_logout":
                logoutUser();
                break;
            default:
                Toast.makeText(this, selected, Toast.LENGTH_SHORT).show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        auth.signOut();
        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(MainContainerActivity.this, MainActivity.class));
        finish();
    }

}