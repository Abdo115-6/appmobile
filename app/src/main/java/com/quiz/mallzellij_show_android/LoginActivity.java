package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.quiz.mallzellij_show_android.api.ApiService;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.AuthResponse;
import com.quiz.mallzellij_show_android.model.LoginRequest;
import com.quiz.mallzellij_show_android.model.SignupRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private View loginCard, registerCard;
    private TextInputEditText loginEmail, loginPassword;
    private TextInputEditText regName, regEmail, regPassword;
    private TextView loginError, registerError;
    private MaterialButton loginBtn, registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabLayout = findViewById(R.id.tabLayout);
        loginCard = findViewById(R.id.loginCard);
        registerCard = findViewById(R.id.registerCard);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        regName = findViewById(R.id.regName);
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        loginError = findViewById(R.id.loginError);
        registerError = findViewById(R.id.registerError);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) {
                boolean isLogin = tab.getPosition() == 0;
                loginCard.setVisibility(isLogin ? View.VISIBLE : View.GONE);
                registerCard.setVisibility(isLogin ? View.GONE : View.VISIBLE);
                loginError.setVisibility(View.GONE);
                registerError.setVisibility(View.GONE);
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loginBtn.setOnClickListener(v -> login());
        registerBtn.setOnClickListener(v -> register());
    }

    private void login() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();
        if (email.isEmpty() || password.isEmpty()) {
            loginError.setText("Please fill all fields");
            loginError.setVisibility(View.VISIBLE);
            return;
        }
        loginError.setVisibility(View.GONE);
        loginBtn.setEnabled(false);

        ApiService api = RetrofitClient.getApiService();
        api.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                loginBtn.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Welcome " + response.body().getName(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        String errBody = response.errorBody().string();
                        AuthResponse err = new Gson().fromJson(errBody, AuthResponse.class);
                        loginError.setText(err.getMessage());
                    } catch (Exception e) {
                        loginError.setText("Login failed");
                    }
                    loginError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                loginBtn.setEnabled(true);
                loginError.setText("Connection error: " + t.getMessage());
                loginError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void register() {
        String name = regName.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            registerError.setText("Please fill all fields");
            registerError.setVisibility(View.VISIBLE);
            return;
        }
        registerError.setVisibility(View.GONE);
        registerBtn.setEnabled(false);

        ApiService api = RetrofitClient.getApiService();
        api.signup(new SignupRequest(name, email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                registerBtn.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(LoginActivity.this, "Registered successfully! Please login.", Toast.LENGTH_SHORT).show();
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                } else {
                    try {
                        String errBody = response.errorBody().string();
                        AuthResponse err = new Gson().fromJson(errBody, AuthResponse.class);
                        registerError.setText(err.getMessage());
                    } catch (Exception e) {
                        registerError.setText("Registration failed");
                    }
                    registerError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                registerBtn.setEnabled(true);
                registerError.setText("Connection error: " + t.getMessage());
                registerError.setVisibility(View.VISIBLE);
            }
        });
    }
}
