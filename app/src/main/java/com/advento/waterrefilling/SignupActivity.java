package com.advento.waterrefilling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        TextInputEditText emailInput = findViewById(R.id.email_input);
        TextInputEditText firstnameInput = findViewById(R.id.firstname_input);
        TextInputEditText middlenameInput = findViewById(R.id.middlename_input);
        TextInputEditText lastnameInput = findViewById(R.id.lastname_input);
        TextInputEditText addressInput = findViewById(R.id.address_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);
        TextInputEditText confirmPasswordInput = findViewById(R.id.confirmpassword_input);
        Button signupButton = findViewById(R.id.signup_button);
        ProgressBar signupProgress = findViewById(R.id.signup_progress);
        TextView loginRedirect = findViewById(R.id.login_redirect);

        signupButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String firstname = firstnameInput.getText() != null ? firstnameInput.getText().toString().trim() : "";
            String middlename = middlenameInput.getText() != null ? middlenameInput.getText().toString().trim() : "";
            String lastname = lastnameInput.getText() != null ? lastnameInput.getText().toString().trim() : "";
            String address = addressInput.getText() != null ? addressInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
            String confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString().trim() : "";

            // Validate inputs
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid email address");
                return;
            }

            if (firstname.isEmpty() || !firstname.matches("[a-zA-Z]+")) {
                firstnameInput.setError("First name must contain only letters");
                return;
            }

            if (lastname.isEmpty() || !lastname.matches("[a-zA-Z]+")) {
                lastnameInput.setError("Last name must contain only letters");
                return;
            }

            if (address.isEmpty()) {
                addressInput.setError("Address is required");
                return;
            }

            if (password.length() < 8) {
                passwordInput.setError("Password must be at least 8 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            // Show progress bar and disable button
            signupProgress.setVisibility(View.VISIBLE);
            signupButton.setEnabled(false);

            // Create account in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {


                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();

                                // Prepare user data
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("firstname", firstname);
                                userData.put("middlename", middlename); // Optional
                                userData.put("lastname", lastname);
                                userData.put("address", address);
                                userData.put("email", email);
                                userData.put("role", "user");

                                // Save user data in Firestore
                                db.collection("users").document(userId)
                                        .set(userData, SetOptions.merge())
                                        .addOnSuccessListener(aVoid -> {
                                            signupProgress.setVisibility(View.GONE);
                                            signupButton.setEnabled(true);
                                            Toast.makeText(SignupActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                                            // Navigate to LoginActivity
                                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            signupProgress.setVisibility(View.GONE);
                                            signupButton.setEnabled(true);
                                            Toast.makeText(SignupActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            signupProgress.setVisibility(View.GONE);
                            signupButton.setEnabled(true);
                            Toast.makeText(SignupActivity.this, "Sign-up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // Redirect to LoginActivity
        loginRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
