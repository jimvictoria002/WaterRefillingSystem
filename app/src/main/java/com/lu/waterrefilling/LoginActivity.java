package com.lu.waterrefilling;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);

        // Initialize UI elements
        TextInputEditText emailInput = findViewById(R.id.email_input);
        TextInputEditText passwordInput = findViewById(R.id.password_input);
        TextInputLayout emailInputLayout = findViewById(R.id.email_input_layout);
        TextInputLayout passwordInputLayout = findViewById(R.id.password_input_layout);
        Button loginButton = findViewById(R.id.login_button);
        ProgressBar loginProgress = findViewById(R.id.login_progress);
        TextView signupRedirect = findViewById(R.id.signup_redirect);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
            String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

            if (!isValidEmail(email)) {
                emailInputLayout.setError("Enter a valid email address");
                return;
            } else {
                emailInputLayout.setError(null);
            }

            if (password.length() < 8) {
                passwordInputLayout.setError("Password must be at least 8 characters");
                return;
            } else {
                passwordInputLayout.setError(null);
            }

            // Disable button and show progress
            loginButton.setEnabled(false);
            loginProgress.setVisibility(View.VISIBLE);

            // Authenticate with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        // Re-enable button and hide progress


                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                db.collection("users").document(userId).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                // Convert the document snapshot to a User object
                                                User userObject = documentSnapshot.toObject(User.class);

                                                if (userObject != null) {
                                                    // Optionally set the Firebase userId to the User object (if needed)
                                                    userObject.setId(userId);

                                                    // Convert the User object to JSON string
                                                    Gson gson = new Gson();
                                                    String userJson = gson.toJson(userObject);

                                                    // Save the JSON string to SharedPreferences
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    editor.putString("user", userJson);
                                                    editor.apply();



                                                    // Proceed to next activity
                                                    loginButton.setEnabled(true);
                                                    loginProgress.setVisibility(View.GONE);
                                                    Intent intent = new Intent(LoginActivity.this, MainContainerActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    // Handle case where the document could not be converted to a User
                                                    loginButton.setEnabled(true);
                                                    loginProgress.setVisibility(View.GONE);
                                                    Toast.makeText(LoginActivity.this, "Error: User data is corrupted.", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                loginButton.setEnabled(true);
                                                loginProgress.setVisibility(View.GONE);
                                                Toast.makeText(LoginActivity.this, "User data not found.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            loginButton.setEnabled(true);
                                            loginProgress.setVisibility(View.GONE);
                                            Toast.makeText(LoginActivity.this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });

                            }
                        } else {
                            loginButton.setEnabled(true);
                            loginProgress.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        signupRedirect.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean isValidEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void saveToSharedPreferences(String userId, Map<String, Object> userData) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        for (Map.Entry<String, Object> entry : userData.entrySet()) {
            editor.putString(entry.getKey(), entry.getValue().toString());
        }
        editor.apply();
    }
}
