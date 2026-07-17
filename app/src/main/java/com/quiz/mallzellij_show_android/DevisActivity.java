package com.quiz.mallzellij_show_android;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.ResponseBody;

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

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.widget.TableRow;
import android.widget.TableLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DevisActivity extends AppCompatActivity {

    private static class ArticleEntry {
        Article article;
        BigDecimal quantity;
        BigDecimal price;
        int cartons;
        BigDecimal coefficient;
        BigDecimal adjustedQty;

        ArticleEntry(Article article, BigDecimal quantity, BigDecimal price,
                     int cartons, BigDecimal coefficient, BigDecimal adjustedQty) {
            this.article = article;
            this.quantity = quantity;
            this.price = price;
            this.cartons = cartons;
            this.coefficient = coefficient;
            this.adjustedQty = adjustedQty;
        }
    }

    private AutoCompleteTextView deviSite, deviClient, deviArticle;
    private TextInputEditText deviQuantity, deviPrice;
    private TextView deviError;
    private ProgressBar deviProgress;
    private MaterialButton deviGenerateBtn, deviAddArticleBtn;

    private LinearLayout deviFormContainer, deviReviewContainer, deviArticleListContainer;
    private TextView deviArticleListEmpty;
    private TextView deviReviewSite, deviReviewClient;
    private TableLayout deviReviewArticleTable;
    private MaterialButton deviEditBtn, deviCsvBtn, deviConfirmBtn;

    private Article selectedArticle;
    private BpCustomerResponse selectedClient;
    private List<BpCustomerResponse> clientList = new ArrayList<>();
    private List<Article> articleList = new ArrayList<>();
    private List<ArticleEntry> articleEntries = new ArrayList<>();

    private static final List<String> SITES = Arrays.asList("FBC", "FMD");
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private Runnable articleSearchRunnable;

    private String pendingCsvContent;
    private String pendingCsvFileName;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    fetchArticleByBarcode(result.getContents());
                }
            });

    private final ActivityResultLauncher<String> storagePermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    if (pendingCsvContent != null) {
                        saveCsvToFile(pendingCsvFileName, pendingCsvContent);
                        pendingCsvContent = null;
                        pendingCsvFileName = null;
                    } else {
                        saveCsvToDownloads();
                    }
                } else {
                    deviError.setText("Storage permission denied");
                    deviError.setVisibility(View.VISIBLE);
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
        toolbar.inflateMenu(R.menu.toolbar_devi_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_export_csv) {
                downloadAllCsv();
                return true;
            }
            return false;
        });
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
        deviProgress = findViewById(R.id.deviProgress);
        deviError = findViewById(R.id.deviError);
        deviGenerateBtn = findViewById(R.id.deviGenerateBtn);

        deviAddArticleBtn = findViewById(R.id.deviAddArticleBtn);
        deviArticleListContainer = findViewById(R.id.deviArticleListContainer);
        deviArticleListEmpty = findViewById(R.id.deviArticleListEmpty);

        deviFormContainer = findViewById(R.id.deviFormContainer);
        deviReviewContainer = findViewById(R.id.deviReviewContainer);
        deviReviewSite = findViewById(R.id.deviReviewSite);
        deviReviewClient = findViewById(R.id.deviReviewClient);
        deviReviewArticleTable = findViewById(R.id.deviReviewArticleTable);
        deviEditBtn = findViewById(R.id.deviEditBtn);
        deviCsvBtn = findViewById(R.id.deviCsvBtn);
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
            String sau = selectedArticle.getSau() != null ? " (" + selectedArticle.getSau() + ")" : "";
            deviArticle.setText(selectedArticle.getNom() + sau);
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

        deviGenerateBtn.setOnClickListener(v -> generateDevi());
        deviAddArticleBtn.setOnClickListener(v -> addArticle());
        deviEditBtn.setOnClickListener(v -> editDevi());
        deviCsvBtn.setOnClickListener(v -> downloadCsv());
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
                    String sau = selectedArticle.getSau() != null ? " (" + selectedArticle.getSau() + ")" : "";
                    deviArticle.setText(selectedArticle.getNom() + sau);
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                deviError.setText("Article not found");
                deviError.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addArticle() {
        String qtyStr = deviQuantity.getText().toString();
        if (selectedArticle == null || qtyStr.isEmpty()) {
            deviError.setText("Select an article and enter quantity");
            deviError.setVisibility(View.VISIBLE);
            return;
        }

        try {
            BigDecimal qty = new BigDecimal(qtyStr);
            BigDecimal coeff = selectedArticle.getCoefficient() != null ? selectedArticle.getCoefficient() : BigDecimal.ONE;
            int cartons = (int) Math.ceil(qty.divide(coeff, 10, BigDecimal.ROUND_HALF_UP).doubleValue());
            BigDecimal adjustedQty = coeff.multiply(BigDecimal.valueOf(cartons));

            ArticleEntry entry = new ArticleEntry(selectedArticle, adjustedQty,
                    parsePrice(deviPrice.getText().toString()), cartons, coeff, adjustedQty);
            articleEntries.add(entry);

            selectedArticle = null;
            deviArticle.setText("");
            deviQuantity.setText("");
            deviPrice.setText("");

            refreshArticleList();
            deviError.setVisibility(View.GONE);
        } catch (Exception e) {
            deviError.setText("Invalid input: " + e.getMessage());
            deviError.setVisibility(View.VISIBLE);
        }
    }

    private void removeArticle(int index) {
        if (index >= 0 && index < articleEntries.size()) {
            articleEntries.remove(index);
            refreshArticleList();
        }
    }

    private void refreshArticleList() {
        deviArticleListContainer.removeAllViews();
        if (articleEntries.isEmpty()) {
            deviArticleListContainer.addView(deviArticleListEmpty);
            return;
        }
        for (int i = 0; i < articleEntries.size(); i++) {
            ArticleEntry entry = articleEntries.get(i);
            View row = getLayoutInflater().inflate(R.layout.item_article_entry, null);
            TextView info = row.findViewById(R.id.articleEntryInfo);
            MaterialButton removeBtn = row.findViewById(R.id.articleEntryRemove);

            info.setText((i + 1) + ". " + entry.article.getNom()
                    + "  |  Qty: " + entry.quantity.stripTrailingZeros().toPlainString()
                    + "  |  Price: " + entry.price.stripTrailingZeros().toPlainString());

            int idx = i;
            removeBtn.setOnClickListener(v -> removeArticle(idx));
            deviArticleListContainer.addView(row);
        }
    }

    private void generateDevi() {
        String site = deviSite.getText().toString().trim();
        if (site.isEmpty() || selectedClient == null || articleEntries.isEmpty()) {
            deviError.setText("Select site, client, and add at least one article");
            deviError.setVisibility(View.VISIBLE);
            return;
        }

        deviReviewSite.setText(site);
        deviReviewClient.setText(selectedClient.getCode() + " - " + selectedClient.getName());

        deviReviewArticleTable.removeAllViews();
        TableRow header = new TableRow(this);
        header.setPadding(0, 0, 0, 8);
        String[] headers = {"#", "Article", "Qty", "Price", "Coeff", "Cartons"};
        int[] weights = {1, 3, 1, 1, 1, 1};
        for (int i = 0; i < headers.length; i++) {
            TextView tv = new TextView(this);
            tv.setText(headers[i]);
            tv.setTextColor(getResources().getColor(R.color.on_surface_variant, getTheme()));
            tv.setTextSize(11);
            tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weights[i]));
            header.addView(tv);
        }
        deviReviewArticleTable.addView(header);

        for (int i = 0; i < articleEntries.size(); i++) {
            ArticleEntry entry = articleEntries.get(i);
            TableRow row = new TableRow(this);
            row.setPadding(0, 6, 0, 6);
            String[] vals = {
                    String.valueOf(i + 1),
                    entry.article.getNom(),
                    entry.quantity.stripTrailingZeros().toPlainString(),
                    entry.price.stripTrailingZeros().toPlainString(),
                    entry.coefficient.stripTrailingZeros().toPlainString(),
                    String.valueOf(entry.cartons)
            };
            for (int j = 0; j < vals.length; j++) {
                TextView tv = new TextView(this);
                tv.setText(vals[j]);
                tv.setTextColor(getResources().getColor(R.color.white, getTheme()));
                tv.setTextSize(13);
                tv.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weights[j]));
                row.addView(tv);
            }
            deviReviewArticleTable.addView(row);
        }

        deviError.setVisibility(View.GONE);
        deviFormContainer.setVisibility(View.GONE);
        deviReviewContainer.setVisibility(View.VISIBLE);
    }

    private void confirmDevi() {
        if (articleEntries.isEmpty()) return;

        deviProgress.setVisibility(View.VISIBLE);
        deviConfirmBtn.setEnabled(false);
        deviEditBtn.setEnabled(false);
        deviError.setVisibility(View.GONE);

        String site = deviSite.getText().toString().trim();
        String email = UserSession.getInstance().getEmail();
        // One mobile key per devis (shared across all articles)
        String devisMobileKey = "MOB" + String.format("%07d", System.currentTimeMillis() % 10000000);
        int[] submitted = {0};
        int[] total = {articleEntries.size()};
        int[] errors = {0};

        for (ArticleEntry entry : articleEntries) {
            DevisRequest request = new DevisRequest();
            request.setSite(site);
            request.setClientCode(selectedClient.getCode());
            request.setClientName(selectedClient.getName());
            request.setArticleRef(entry.article.getRef());
            request.setArticleName(entry.article.getNom());
            request.setQuantity(entry.quantity);
            request.setPrice(entry.price);
            request.setCoefficient(entry.coefficient);
            request.setCartons(entry.cartons);
            request.setCreusr0(email);
            request.setMobileKey(devisMobileKey);

            RetrofitClient.getApiService().submitDevis(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    submitted[0]++;
                    if (!response.isSuccessful()) {
                        errors[0]++;
                    }
                    checkDone(submitted[0], total[0], errors[0]);
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    submitted[0]++;
                    errors[0]++;
                    checkDone(submitted[0], total[0], errors[0]);
                }
            });
        }
    }

    private void checkDone(int submitted, int total, int errors) {
        if (submitted < total) return;
        runOnUiThread(() -> {
            deviProgress.setVisibility(View.GONE);
            deviConfirmBtn.setEnabled(true);
            deviEditBtn.setEnabled(true);
            if (errors == 0) {
                deviError.setText("Devi saved successfully! (" + total + " articles)");
                deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                deviReviewContainer.setVisibility(View.GONE);
                deviFormContainer.setVisibility(View.VISIBLE);
                deviError.setVisibility(View.VISIBLE);
                clearForm();
            } else {
                deviError.setText(errors + " of " + total + " articles failed");
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

    private void downloadCsv() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveCsvToDownloads();
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            saveCsvToDownloads();
        } else {
            storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void saveCsvToDownloads() {
        String site = deviReviewSite.getText().toString();
        String clientCode = selectedClient != null ? selectedClient.getCode() : "";
        // Generate one mobile key for this review CSV
        String mobileKey = "MOB" + String.format("%07d", System.currentTimeMillis() % 10000000);
        String today = new java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault()).format(new java.util.Date());

        StringBuilder csv = new StringBuilder();
        // E: ;TYPE;;SITE;SQN;;CLIENT_CODE;DATE;;SITE;CURRENCY;MOBILE_KEY
        csv.append("E;;")
           .append(escapeCsv(site)).append(";")
           .append("SQN").append(";;")  // SQN = fixed
           .append(escapeCsv(clientCode)).append(";")
           .append(today).append(";;")
           .append(escapeCsv(site)).append(";")
           .append("MAD;")
           .append(mobileKey).append("\n");
        for (ArticleEntry entry : articleEntries) {
            // L: ;TYPE;;ARTICLE_REF;UN;QTY;PRICE;MOBILE_KEY
            csv.append("L;;")
               .append(escapeCsv(entry.article.getRef())).append(";")
               .append("UN;")
               .append(entry.quantity.stripTrailingZeros().toPlainString()).append(";")
               .append(entry.price.stripTrailingZeros().toPlainString()).append(";")
               .append(mobileKey).append("\n");
        }

        String fileName = "devi_" + System.currentTimeMillis() + ".csv";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.IS_PENDING, 1);
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                deviError.setText("Failed to create file");
                deviError.setVisibility(View.VISIBLE);
                return;
            }
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out != null) {
                    out.write(csv.toString().getBytes());
                }
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(uri, values, null, null);
                deviError.setText("CSV saved to Downloads");
                deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                deviError.setText("Failed to save CSV: " + e.getMessage());
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "Failed to save CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                getContentResolver().delete(uri, null, null);
            }
        } else {
            java.io.File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            java.io.File csvFile = new java.io.File(downloadsDir, fileName);
            try (java.io.FileWriter writer = new java.io.FileWriter(csvFile)) {
                writer.write(csv.toString());
                deviError.setText("CSV saved to Downloads");
                deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show();
                Uri fileUri = Uri.fromFile(csvFile);
                Intent scanFile = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri);
                sendBroadcast(scanFile);
            } catch (IOException e) {
                deviError.setText("Failed to save CSV: " + e.getMessage());
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "Failed to save CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void downloadAllCsv() {
        deviProgress.setVisibility(View.VISIBLE);
        deviError.setVisibility(View.GONE);

        RetrofitClient.getApiService().exportDevisCsv().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                deviProgress.setVisibility(View.GONE);
                if (!response.isSuccessful() || response.body() == null) {
                    deviError.setText("Failed to export data");
                    deviError.setVisibility(View.VISIBLE);
                    Toast.makeText(DevisActivity.this, "Failed to export data", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    String csv = response.body().string();
                    saveExportedCsv(csv);
                } catch (IOException e) {
                    deviError.setText("Export error: " + e.getMessage());
                    deviError.setVisibility(View.VISIBLE);
                    Toast.makeText(DevisActivity.this, "Export error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                deviProgress.setVisibility(View.GONE);
                deviError.setText("Connection error: " + t.getMessage());
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveExportedCsv(String csv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveCsvToFile("devis_export.csv", csv);
        } else if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            saveCsvToFile("devis_export.csv", csv);
        } else {
            pendingCsvContent = csv;
            pendingCsvFileName = "devis_export.csv";
            storagePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void saveCsvToFile(String fileName, String csv) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
            values.put(MediaStore.Downloads.IS_PENDING, 1);
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) {
                deviError.setText("Failed to create file");
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "Failed to create file", Toast.LENGTH_SHORT).show();
                return;
            }
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                if (out != null) {
                    out.write(csv.getBytes());
                }
                values.clear();
                values.put(MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(uri, values, null, null);
                deviError.setText("CSV saved to Downloads");
                deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                deviError.setText("Failed to save: " + e.getMessage());
                deviError.setVisibility(View.VISIBLE);
                Toast.makeText(DevisActivity.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                if (uri != null) getContentResolver().delete(uri, null, null);
            }
        } else {
            java.io.File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            java.io.File csvFile = new java.io.File(downloadsDir, fileName);
            try (java.io.FileWriter writer = new java.io.FileWriter(csvFile)) {
                writer.write(csv);
        deviError.setText("CSV saved to Downloads");
        deviError.setTextColor(getResources().getColor(android.R.color.white, getTheme()));
        deviError.setVisibility(View.VISIBLE);
        Toast.makeText(DevisActivity.this, "CSV saved to Downloads", Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(csvFile)));
            } catch (IOException e) {
                deviError.setText("Failed to save: " + e.getMessage());
                deviError.setVisibility(View.VISIBLE);
            }
        }
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
        selectedArticle = null;
        selectedClient = null;
        clientList.clear();
        articleList.clear();
        articleEntries.clear();
        refreshArticleList();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
