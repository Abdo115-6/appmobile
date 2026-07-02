package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import com.quiz.mallzellij_show_android.api.ApiService;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.AuthResponse;
import com.quiz.mallzellij_show_android.model.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText loginEmail, loginPassword;
    private TextView loginError;
    private ProgressBar loginProgress;
    private MaterialButton loginBtn;
    private TextInputLayout loginEmailLayout, loginPasswordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginError = findViewById(R.id.loginError);
        loginProgress = findViewById(R.id.loginProgress);
        loginBtn = findViewById(R.id.loginBtn);

        loginEmailLayout = (TextInputLayout) loginEmail.getParent().getParent();
        loginPasswordLayout = (TextInputLayout) loginPassword.getParent().getParent();

        loginBtn.setOnClickListener(v -> login());

        attachClearErrorListeners();
    }

    private void attachClearErrorListeners() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginError.setVisibility(View.GONE);
                clearErrors();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        loginEmail.addTextChangedListener(clearErrorWatcher);
        loginPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void clearErrors() {
        loginEmailLayout.setErrorEnabled(false);
        loginPasswordLayout.setErrorEnabled(false);
    }

    private void setLoading(boolean loading) {
        loginProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        loginBtn.setEnabled(!loading);
        loginBtn.setText(loading ? getString(R.string.login_loading) : getString(R.string.login_btn));
        loginEmail.setEnabled(!loading);
        loginPassword.setEnabled(!loading);
    }

    private void login() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        clearErrors();
        if (!validateLoginFields(email, password)) return;

        loginError.setVisibility(View.GONE);
        setLoading(true);

        ApiService api = RetrofitClient.getApiService();
        api.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    startActivity(new Intent(LoginActivity.this, ArticlesActivity.class));
                    finish();
                } else {
                    String msg = getString(R.string.error_login_failed);
                    try {
                        String errBody = response.errorBody().string();
                        AuthResponse err = new Gson().fromJson(errBody, AuthResponse.class);
                        if (err != null && err.getMessage() != null) msg = err.getMessage();
                    } catch (Exception ignored) {}
                    loginError.setText(msg);
                    loginError.setVisibility(View.VISIBLE);
                    loginError.announceForAccessibility(msg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false);
                String msg = getString(R.string.error_network) + ": " + t.getMessage();
                loginError.setText(msg);
                loginError.setVisibility(View.VISIBLE);
                loginError.announceForAccessibility(msg);
            }
        });
    }

    private boolean validateLoginFields(String email, String password) {
        if (email.isEmpty()) {
            loginEmailLayout.setError(getString(R.string.error_required));
            loginEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            loginPasswordLayout.setError(getString(R.string.error_required));
            loginPassword.requestFocus();
            return false;
        }
        return true;
    }
}
