package com.example.imageview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.imageview.R;
import com.example.imageview.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {
            // Ensure Firebase is initialized
            FirebaseApp.initializeApp(this);

            // Initialize Firebase Auth
            auth = FirebaseUtil.getAuth();

            // Check if user is already logged in
            if (FirebaseUtil.isLoggedIn()) {
                Log.d(TAG, "User already logged in, redirecting to MainActivity");
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return;
            }

            // Initialize views
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);
            progressBar = findViewById(R.id.progressBar);

            // Set up click listeners
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUser();
                }
            });

            tvRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error in LoginActivity onCreate", e);
            Toast.makeText(this, "Error initializing login: " + e.getMessage(), Toast.LENGTH_LONG).show();

            // Initialize views even if Firebase fails
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);
            btnLogin = findViewById(R.id.btnLogin);
            tvRegister = findViewById(R.id.tvRegister);
            progressBar = findViewById(R.id.progressBar);

            // Disable login button if Firebase failed
            if (btnLogin != null) {
                btnLogin.setEnabled(false);
                btnLogin.setText("Login Unavailable");
            }

            // Still allow registration navigation
            if (tvRegister != null) {
                tvRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                    }
                });
            }
        }
    }

    private void loginUser() {
        try {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validate input
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Email diperlukan");
                etEmail.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                etPassword.setError("Password diperlukan");
                etPassword.requestFocus();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Ensure Firebase Auth is initialized
            if (auth == null) {
                try {
                    auth = FirebaseUtil.getAuth();
                } catch (Exception e) {
                    Log.e(TAG, "Failed to initialize Firebase Auth", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Login tidak tersedia: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            // Authenticate user
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                // Login success
                                Log.d(TAG, "Login successful");
                                Toast.makeText(LoginActivity.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Login failed
                                Log.e(TAG, "Login failed", task.getException());
                                String errorMessage = "Login gagal";
                                if (task.getException() != null) {
                                    errorMessage += ": " + task.getException().getMessage();
                                }
                                Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error during login", e);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
