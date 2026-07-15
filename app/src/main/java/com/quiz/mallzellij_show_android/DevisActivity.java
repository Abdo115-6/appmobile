package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.Article;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevisActivity extends AppCompatActivity {

    private AutoCompleteTextView deviSite;
    private TextInputEditText deviClient, deviArticle, deviQuantity;
    private TextView deviCoefficient, deviCartons, deviError;
    private ProgressBar deviProgress;
    private MaterialButton deviGenerateBtn;

    private Article selectedArticle;

    private static final List<String> SITES = Arrays.asList("FBC", "FMD");

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    deviArticle.setText(result.getContents());
                    lookupArticle(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devis);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setCheckedItem(R.id.nav_devi);
        boolean isAdmin = UserSession.getInstance().isAdmin() || UserSession.getInstance().isSuperuser();
        if (!isAdmin) {
            navView.getMenu().findItem(R.id.nav_inventory).setVisible(false);
        }
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_articles) {
                startActivity(new Intent(this, ArticlesActivity.class));
                finish();
            } else if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
            } else if (id == R.id.nav_devi) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (id == R.id.nav_logout) {
                UserSession.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        deviSite = findViewById(R.id.deviSite);
        deviClient = findViewById(R.id.deviClient);
        deviArticle = findViewById(R.id.deviArticle);
        deviQuantity = findViewById(R.id.deviQuantity);
        deviCoefficient = findViewById(R.id.deviCoefficient);
        deviCartons = findViewById(R.id.deviCartons);
        deviProgress = findViewById(R.id.deviProgress);
        deviError = findViewById(R.id.deviError);
        deviGenerateBtn = findViewById(R.id.deviGenerateBtn);
        ImageButton scanBtn = findViewById(R.id.scanArticleBtn);

        ArrayAdapter<String> siteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SITES);
        deviSite.setAdapter(siteAdapter);

        scanBtn.setOnClickListener(v -> {
            ScanOptions options = new ScanOptions();
            options.setDesiredBarcodeFormats("QR_CODE");
            options.setPrompt("Scan article QR code");
            options.setCameraId(0);
            options.setBeepEnabled(true);
            options.setBarcodeImageEnabled(true);
            options.setOrientationLocked(false);
            barcodeLauncher.launch(options);
        });

        deviArticle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    lookupArticle(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        deviQuantity.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCartons();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        deviGenerateBtn.setOnClickListener(v -> generateDevi());
    }

    private void lookupArticle(String input) {
        String ref = input.trim().split("\\s+")[0];
        RetrofitClient.getApiService().getArticleByBarcode(ref).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedArticle = response.body();
                    if (selectedArticle.getCoefficient() != null) {
                        deviCoefficient.setText(String.valueOf(selectedArticle.getCoefficient()));
                    } else {
                        deviCoefficient.setText("-");
                    }
                    deviArticle.setText(selectedArticle.getNom());
                    calculateCartons();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                deviError.setText("Article not found: " + t.getMessage());
                deviError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void calculateCartons() {
        String qtyStr = deviQuantity.getText().toString();
        if (selectedArticle == null || selectedArticle.getCoefficient() == null || selectedArticle.getCoefficient() == 0 || qtyStr.isEmpty()) {
            deviCartons.setText("0");
            return;
        }
        try {
            int quantity = Integer.parseInt(qtyStr);
            int coeff = selectedArticle.getCoefficient();
            int cartons = (int) Math.ceil((double) quantity / coeff);
            deviCartons.setText(String.valueOf(cartons));
        } catch (NumberFormatException e) {
            deviCartons.setText("0");
        }
    }

    private void generateDevi() {
        String site = deviSite.getText().toString().trim();
        String client = deviClient.getText().toString().trim();
        String qtyStr = deviQuantity.getText().toString();

        if (site.isEmpty() || client.isEmpty() || selectedArticle == null || qtyStr.isEmpty()) {
            deviError.setText("Please fill all fields");
            deviError.setVisibility(View.VISIBLE);
            return;
        }

        int quantity = Integer.parseInt(qtyStr);
        int coeff = selectedArticle.getCoefficient() != null ? selectedArticle.getCoefficient() : 1;
        int cartons = (int) Math.ceil((double) quantity / coeff);

        String summary = "Site: " + site + "\n"
                + "Client: " + client + "\n"
                + "Article: " + selectedArticle.getNom() + " (" + selectedArticle.getRef() + ")\n"
                + "Quantity: " + quantity + "\n"
                + "Coefficient: " + coeff + "\n"
                + "Cartons: " + cartons;

        deviError.setText(summary);
        deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        deviError.setVisibility(View.VISIBLE);
    }
}
