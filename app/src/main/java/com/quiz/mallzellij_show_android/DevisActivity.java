package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import com.quiz.mallzellij_show_android.model.BpCustomerResponse;
import com.quiz.mallzellij_show_android.model.DevisRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevisActivity extends AppCompatActivity {

    private AutoCompleteTextView deviSite, deviClient, deviArticle;
    private TextInputEditText deviQuantity, deviPrice;
    private TextView deviCoefficient, deviCartons, deviAdjustedQty, deviError;
    private ProgressBar deviProgress;
    private MaterialButton deviGenerateBtn;

    private LinearLayout deviFormContainer, deviReviewContainer;
    private TextView deviReviewSite, deviReviewClient, deviReviewArticle,
            deviReviewQty, deviReviewPrice, deviReviewCoeff, deviReviewCartons;
    private MaterialButton deviEditBtn, deviConfirmBtn;

    private Article selectedArticle;
    private BpCustomerResponse selectedClient;
    private List<BpCustomerResponse> clientList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();

    private DevisRequest pendingRequest;

    private static final List<String> SITES = Arrays.asList("FBC", "FMD");
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private Runnable articleSearchRunnable;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    fetchArticleByBarcode(result.getContents());
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
        deviPrice = findViewById(R.id.deviPrice);
        deviCoefficient = findViewById(R.id.deviCoefficient);
        deviCartons = findViewById(R.id.deviCartons);
        deviAdjustedQty = findViewById(R.id.deviAdjustedQty);
        deviProgress = findViewById(R.id.deviProgress);
        deviError = findViewById(R.id.deviError);
        deviGenerateBtn = findViewById(R.id.deviGenerateBtn);

        deviFormContainer = findViewById(R.id.deviFormContainer);
        deviReviewContainer = findViewById(R.id.deviReviewContainer);
        deviReviewSite = findViewById(R.id.deviReviewSite);
        deviReviewClient = findViewById(R.id.deviReviewClient);
        deviReviewArticle = findViewById(R.id.deviReviewArticle);
        deviReviewQty = findViewById(R.id.deviReviewQty);
        deviReviewPrice = findViewById(R.id.deviReviewPrice);
        deviReviewCoeff = findViewById(R.id.deviReviewCoeff);
        deviReviewCartons = findViewById(R.id.deviReviewCartons);
        deviEditBtn = findViewById(R.id.deviEditBtn);
        deviConfirmBtn = findViewById(R.id.deviConfirmBtn);

        ImageButton scanBtn = findViewById(R.id.scanArticleBtn);

        ArrayAdapter<String> siteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, SITES);
        deviSite.setAdapter(siteAdapter);

        ArrayAdapter<String> clientAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        deviClient.setAdapter(clientAdapter);

        deviClient.setOnItemClickListener((parent, view, position, id) -> {
            selectedClient = clientList.get(position);
        });

        ArrayAdapter<String> articleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        deviArticle.setAdapter(articleAdapter);

        deviArticle.setOnItemClickListener((parent, view, position, id) -> {
            selectedArticle = articleList.get(position);
            deviCoefficient.setText(selectedArticle.getCoefficient() != null
                    ? selectedArticle.getCoefficient().stripTrailingZeros().toPlainString() : "-");
            String sau = selectedArticle.getSau() != null ? " (" + selectedArticle.getSau() + ")" : "";
            deviArticle.setText(selectedArticle.getNom() + sau);
            calculateCartons();
        });

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

        deviClient.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> searchClients(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 400);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        deviArticle.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (articleSearchRunnable != null) searchHandler.removeCallbacks(articleSearchRunnable);
                articleSearchRunnable = () -> searchArticles(s.toString().trim());
                searchHandler.postDelayed(articleSearchRunnable, 400);
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
        deviEditBtn.setOnClickListener(v -> editDevi());
        deviConfirmBtn.setOnClickListener(v -> confirmDevi());
    }

    private void searchClients(String q) {
        if (q.length() < 2) return;
        RetrofitClient.getApiService().searchClients(q).enqueue(new Callback<List<BpCustomerResponse>>() {
            @Override
            public void onResponse(Call<List<BpCustomerResponse>> call, Response<List<BpCustomerResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clientList = response.body();
                    List<String> displayList = new ArrayList<>();
                    for (BpCustomerResponse c : clientList) {
                        displayList.add(c.getCode() + " - " + c.getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DevisActivity.this,
                            android.R.layout.simple_dropdown_item_1line, displayList);
                    deviClient.setAdapter(adapter);
                    if (!displayList.isEmpty()) {
                        deviClient.showDropDown();
                    }
                    deviError.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<BpCustomerResponse>> call, Throwable t) {}
        });
    }

    private void searchArticles(String q) {
        if (q.length() < 2) return;
        RetrofitClient.getApiService().searchArticles(q).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    articleList = response.body();
                    List<String> displayList = new ArrayList<>();
                    for (Article a : articleList) {
                        String sau = a.getSau() != null ? " (" + a.getSau() + ")" : "";
                        displayList.add(a.getNom() + sau);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(DevisActivity.this,
                            android.R.layout.simple_dropdown_item_1line, displayList);
                    deviArticle.setAdapter(adapter);
                    if (!displayList.isEmpty()) {
                        deviArticle.showDropDown();
                    }
                    deviError.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {}
        });
    }

    private void fetchArticleByBarcode(String barcode) {
        String ref = barcode.trim().split("\\s+")[0];
        deviArticle.setText(ref);
        RetrofitClient.getApiService().getArticleByBarcode(ref).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                if (response.isSuccessful() && response.body() != null) {
                    selectedArticle = response.body();
                    deviCoefficient.setText(selectedArticle.getCoefficient() != null
                            ? selectedArticle.getCoefficient().stripTrailingZeros().toPlainString() : "-");
                    String sau = selectedArticle.getSau() != null ? " (" + selectedArticle.getSau() + ")" : "";
                    deviArticle.setText(selectedArticle.getNom() + sau);
                    calculateCartons();
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                deviError.setText("Article not found");
                deviError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void calculateCartons() {
        String qtyStr = deviQuantity.getText().toString();
        if (selectedArticle == null || selectedArticle.getCoefficient() == null
                || selectedArticle.getCoefficient().compareTo(BigDecimal.ZERO) == 0 || qtyStr.isEmpty()) {
            deviCartons.setText("0");
            deviAdjustedQty.setText("0");
            return;
        }
        try {
            BigDecimal qty = new BigDecimal(qtyStr);
            BigDecimal coeff = selectedArticle.getCoefficient();
            int cartons = (int) Math.ceil(qty.divide(coeff, 10, BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal adjustedQty = coeff.multiply(BigDecimal.valueOf(cartons));
            deviCartons.setText(String.valueOf(cartons));
            deviAdjustedQty.setText(adjustedQty.stripTrailingZeros().toPlainString());
        } catch (NumberFormatException e) {
            deviCartons.setText("0");
            deviAdjustedQty.setText("0");
        }
    }

    private void generateDevi() {
        String site = deviSite.getText().toString().trim();
        String qtyStr = deviQuantity.getText().toString();

        if (site.isEmpty() || selectedClient == null || selectedArticle == null || qtyStr.isEmpty()) {
            deviError.setText("Please fill all fields");
            deviError.setVisibility(View.VISIBLE);
            return;
        }

        BigDecimal inputQty = new BigDecimal(qtyStr);
        BigDecimal coeff = selectedArticle.getCoefficient() != null ? selectedArticle.getCoefficient() : BigDecimal.ONE;
        int cartons = (int) Math.ceil(inputQty.divide(coeff, 10, BigDecimal.ROUND_HALF_UP).doubleValue());
        BigDecimal adjustedQty = coeff.multiply(BigDecimal.valueOf(cartons));

        pendingRequest = new DevisRequest();
        pendingRequest.setSite(site);
        pendingRequest.setClientCode(selectedClient.getCode());
        pendingRequest.setClientName(selectedClient.getName());
        pendingRequest.setArticleRef(selectedArticle.getRef());
        pendingRequest.setArticleName(selectedArticle.getNom());
        pendingRequest.setQuantity(adjustedQty);
        pendingRequest.setPrice(parsePrice(deviPrice.getText().toString()));
        pendingRequest.setCoefficient(coeff);
        pendingRequest.setCartons(cartons);
        pendingRequest.setCreusr0(UserSession.getInstance().getEmail());

        deviReviewSite.setText(site);
        deviReviewClient.setText(selectedClient.getCode() + " - " + selectedClient.getName());
        deviReviewArticle.setText(selectedArticle.getRef() + " - " + selectedArticle.getNom());
        deviReviewQty.setText(adjustedQty.stripTrailingZeros().toPlainString());
        deviReviewPrice.setText(parsePrice(deviPrice.getText().toString()).stripTrailingZeros().toPlainString());
        deviReviewCoeff.setText(coeff.stripTrailingZeros().toPlainString());
        deviReviewCartons.setText(String.valueOf(cartons));

        deviError.setVisibility(View.GONE);
        deviFormContainer.setVisibility(View.GONE);
        deviReviewContainer.setVisibility(View.VISIBLE);
    }

    private void confirmDevi() {
        if (pendingRequest == null) return;

        deviProgress.setVisibility(View.VISIBLE);
        deviConfirmBtn.setEnabled(false);
        deviEditBtn.setEnabled(false);
        deviError.setVisibility(View.GONE);

        RetrofitClient.getApiService().submitDevis(pendingRequest).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                deviProgress.setVisibility(View.GONE);
                deviConfirmBtn.setEnabled(true);
                deviEditBtn.setEnabled(true);
                if (response.isSuccessful()) {
                    deviError.setText("Devi saved successfully!");
                    deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                    deviReviewContainer.setVisibility(View.GONE);
                    deviFormContainer.setVisibility(View.VISIBLE);
                    deviError.setVisibility(View.VISIBLE);
                    clearForm();
                } else {
                    deviError.setText("Failed to save devis");
                    deviError.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
                    deviError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                deviProgress.setVisibility(View.GONE);
                deviConfirmBtn.setEnabled(true);
                deviEditBtn.setEnabled(true);
                deviError.setText("Connection error: " + t.getMessage());
                deviError.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
                deviError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void editDevi() {
        deviReviewContainer.setVisibility(View.GONE);
        deviFormContainer.setVisibility(View.VISIBLE);
        deviError.setVisibility(View.GONE);
    }

    private BigDecimal parsePrice(String text) {
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private void clearForm() {
        deviSite.setText("");
        deviClient.setText("");
        deviArticle.setText("");
        deviQuantity.setText("");
        deviPrice.setText("");
        deviCoefficient.setText("-");
        deviCartons.setText("0");
        deviAdjustedQty.setText("0");
        selectedArticle = null;
        selectedClient = null;
        clientList.clear();
        articleList.clear();
    }
}
