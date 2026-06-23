package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

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
    private ProgressBar loginProgress, registerProgress;
    private MaterialButton loginBtn, registerBtn;
    private TextInputLayout loginEmailLayout, loginPasswordLayout;
    private TextInputLayout regNameLayout, regEmailLayout, regPasswordLayout;

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
        loginProgress = findViewById(R.id.loginProgress);
        registerProgress = findViewById(R.id.registerProgress);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginEmailLayout = (TextInputLayout) loginEmail.getParent().getParent();
        loginPasswordLayout = (TextInputLayout) loginPassword.getParent().getParent();
        regNameLayout = (TextInputLayout) regName.getParent().getParent();
        regEmailLayout = (TextInputLayout) regEmail.getParent().getParent();
        regPasswordLayout = (TextInputLayout) regPassword.getParent().getParent();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                boolean isLogin = tab.getPosition() == 0;
                loginCard.setVisibility(isLogin ? View.VISIBLE : View.GONE);
                registerCard.setVisibility(isLogin ? View.GONE : View.VISIBLE);
                loginError.setVisibility(View.GONE);
                registerError.setVisibility(View.GONE);
                clearErrors();

                View target = isLogin ? loginEmail : regName;
                target.post(() -> target.requestFocus());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loginBtn.setOnClickListener(v -> login());
        registerBtn.setOnClickListener(v -> register());

        attachClearErrorListeners();
    }

    private void attachClearErrorListeners() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginError.setVisibility(View.GONE);
                registerError.setVisibility(View.GONE);
                clearErrors();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        loginEmail.addTextChangedListener(clearErrorWatcher);
        loginPassword.addTextChangedListener(clearErrorWatcher);
        regName.addTextChangedListener(clearErrorWatcher);
        regEmail.addTextChangedListener(clearErrorWatcher);
        regPassword.addTextChangedListener(clearErrorWatcher);
    }

    private void clearErrors() {
        loginEmailLayout.setErrorEnabled(false);
        loginPasswordLayout.setErrorEnabled(false);
        regNameLayout.setErrorEnabled(false);
        regEmailLayout.setErrorEnabled(false);
        regPasswordLayout.setErrorEnabled(false);
    }

    private void setLoading(boolean loading, boolean isLogin) {
        ProgressBar progress = isLogin ? loginProgress : registerProgress;
        MaterialButton button = isLogin ? loginBtn : registerBtn;
        TextInputEditText email = isLogin ? loginEmail : regEmail;
        TextInputEditText password = isLogin ? loginPassword : regPassword;
        TextInputEditText name = isLogin ? null : regName;

        progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        button.setEnabled(!loading);
        button.setText(loading
            ? (isLogin ? getString(R.string.login_loading) : getString(R.string.register_loading))
            : (isLogin ? getString(R.string.login_btn) : getString(R.string.register_btn)));
        email.setEnabled(!loading);
        password.setEnabled(!loading);
        if (name != null) name.setEnabled(!loading);
    }

    private void login() {
        String email = loginEmail.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        clearErrors();
        if (!validateLoginFields(email, password)) return;

        loginError.setVisibility(View.GONE);
        setLoading(true, true);

        ApiService api = RetrofitClient.getApiService();
        api.login(new LoginRequest(email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false, true);
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
                setLoading(false, true);
                String msg = getString(R.string.error_network) + ": " + t.getMessage();
                loginError.setText(msg);
                loginError.setVisibility(View.VISIBLE);
                loginError.announceForAccessibility(msg);
            }
        });
    }

    private void register() {
        String name = regName.getText().toString().trim();
        String email = regEmail.getText().toString().trim();
        String password = regPassword.getText().toString().trim();

        clearErrors();
        if (!validateRegisterFields(name, email, password)) return;

        registerError.setVisibility(View.GONE);
        setLoading(true, false);

        ApiService api = RetrofitClient.getApiService();
        api.signup(new SignupRequest(name, email, password)).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                setLoading(false, false);
                if (response.isSuccessful() && response.body() != null) {
                    String msg = getString(R.string.success_register);
                    registerError.setTextColor(getColor(R.color.primary));
                    registerError.setText(msg);
                    registerError.setVisibility(View.VISIBLE);
                    registerError.announceForAccessibility(msg);
                    tabLayout.selectTab(tabLayout.getTabAt(0));
                } else {
                    String msg = getString(R.string.error_register_failed);
                    try {
                        String errBody = response.errorBody().string();
                        AuthResponse err = new Gson().fromJson(errBody, AuthResponse.class);
                        if (err != null && err.getMessage() != null) msg = err.getMessage();
                    } catch (Exception ignored) {}
                    registerError.setTextColor(getColor(R.color.error));
                    registerError.setText(msg);
                    registerError.setVisibility(View.VISIBLE);
                    registerError.announceForAccessibility(msg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                setLoading(false, false);
                String msg = getString(R.string.error_network) + ": " + t.getMessage();
                registerError.setTextColor(getColor(R.color.error));
                registerError.setText(msg);
                registerError.setVisibility(View.VISIBLE);
                registerError.announceForAccessibility(msg);
            }
        });
    }

    private boolean validateLoginFields(String email, String password) {
        if (email.isEmpty()) {
            loginEmailLayout.setError(getString(R.string.error_required));
            loginEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            loginEmailLayout.setError(getString(R.string.error_invalid_email));
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

    private boolean validateRegisterFields(String name, String email, String password) {
        if (name.isEmpty()) {
            regNameLayout.setError(getString(R.string.error_required));
            regName.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            regEmailLayout.setError(getString(R.string.error_required));
            regEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            regEmailLayout.setError(getString(R.string.error_invalid_email));
            regEmail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            regPasswordLayout.setError(getString(R.string.error_required));
            regPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            regPasswordLayout.setError(getString(R.string.error_short_password));
            regPassword.requestFocus();
            return false;
        }
        return true;
    }
}
