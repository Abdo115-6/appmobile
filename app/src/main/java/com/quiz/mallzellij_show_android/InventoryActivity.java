package com.quiz.mallzellij_show_android;

import android.app.AlertDialog;
import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.quiz.mallzellij_show_android.UserSession;
import com.quiz.mallzellij_show_android.api.ApiService;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.InventoryRequest;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryActivity extends AppCompatActivity {

    private static final int WRITE_PERMISSION_CODE = 100;
    private byte[] pendingCsvData;

    private AutoCompleteTextView inventoryEquipe, inventoryDepot, inventoryZone;
    private TextInputEditText inventoryArticle, inventoryPallet, inventoryCarton, inventoryMetreCarre;
    private TextView inventoryError;
    private ProgressBar inventoryProgress;
    private MaterialButton inventoryBtn;
    private TextInputLayout inventoryEquipeLayout, inventoryDepotLayout, inventoryZoneLayout;
    private TextInputLayout inventoryArticleLayout, inventoryPalletLayout, inventoryCartonLayout, inventoryMetreCarreLayout;

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    inventoryArticle.setText(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setCheckedItem(R.id.nav_inventory);
        if (!UserSession.getInstance().isAdmin()) {
            navView.getMenu().findItem(R.id.nav_inventory).setVisible(false);
        }
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_articles) {
                startActivity(new Intent(this, ArticlesActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                UserSession.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        inventoryEquipe = findViewById(R.id.inventoryEquipe);
        inventoryDepot = findViewById(R.id.inventoryDepot);
        inventoryZone = findViewById(R.id.inventoryZone);
        inventoryArticle = findViewById(R.id.inventoryArticle);
        inventoryPallet = findViewById(R.id.inventoryPallet);
        inventoryCarton = findViewById(R.id.inventoryCarton);
        inventoryMetreCarre = findViewById(R.id.inventoryMetreCarre);
        inventoryError = findViewById(R.id.inventoryError);
        inventoryProgress = findViewById(R.id.inventoryProgress);
        inventoryBtn = findViewById(R.id.inventoryBtn);

        inventoryEquipeLayout = (TextInputLayout) inventoryEquipe.getParent().getParent();
        inventoryDepotLayout = (TextInputLayout) inventoryDepot.getParent().getParent();
        inventoryZoneLayout = (TextInputLayout) inventoryZone.getParent().getParent();
        inventoryArticleLayout = (TextInputLayout) inventoryArticle.getParent().getParent();
        inventoryPalletLayout = (TextInputLayout) inventoryPallet.getParent().getParent();
        inventoryCartonLayout = (TextInputLayout) inventoryCarton.getParent().getParent();
        inventoryMetreCarreLayout = (TextInputLayout) inventoryMetreCarre.getParent().getParent();

        inventoryBtn.setOnClickListener(v -> submitInventory());

        MaterialButton downloadCsvBtn = findViewById(R.id.downloadCsvBtn);
        downloadCsvBtn.setOnClickListener(v -> downloadCsv());

        ImageButton scanArticleBtn = findViewById(R.id.scanArticleBtn);
        scanArticleBtn.setOnClickListener(v -> startScan());

        setupDropdowns();

        attachClearErrorListeners();
    }

    private void attachClearErrorListeners() {
        TextWatcher clearErrorWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                inventoryError.setVisibility(View.GONE);
                clearErrors();
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        inventoryEquipe.addTextChangedListener(clearErrorWatcher);
        inventoryDepot.addTextChangedListener(clearErrorWatcher);
        inventoryZone.addTextChangedListener(clearErrorWatcher);
        inventoryArticle.addTextChangedListener(clearErrorWatcher);
        inventoryPallet.addTextChangedListener(clearErrorWatcher);
        inventoryCarton.addTextChangedListener(clearErrorWatcher);
        inventoryMetreCarre.addTextChangedListener(clearErrorWatcher);
    }

    private void clearErrors() {
        inventoryEquipeLayout.setErrorEnabled(false);
        inventoryDepotLayout.setErrorEnabled(false);
        inventoryZoneLayout.setErrorEnabled(false);
        inventoryArticleLayout.setErrorEnabled(false);
        inventoryPalletLayout.setErrorEnabled(false);
        inventoryCartonLayout.setErrorEnabled(false);
        inventoryMetreCarreLayout.setErrorEnabled(false);
    }

    private void setLoading(boolean loading) {
        inventoryProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        inventoryBtn.setEnabled(!loading);
        inventoryBtn.setText((CharSequence) (loading ? getString(R.string.inventory_loading) : getString(R.string.inventory_btn)));
        inventoryEquipe.setEnabled(!loading);
        inventoryDepot.setEnabled(!loading);
        inventoryZone.setEnabled(!loading);
        inventoryArticle.setEnabled(!loading);
        inventoryPallet.setEnabled(!loading);
        inventoryCarton.setEnabled(!loading);
        inventoryMetreCarre.setEnabled(!loading);
    }

    private void submitInventory() {
        String equipe = inventoryEquipe.getText().toString().trim();
        String depot = inventoryDepot.getText().toString().trim();
        String zone = inventoryZone.getText().toString().trim();
        String article = inventoryArticle.getText().toString().trim();
        String pallet = inventoryPallet.getText().toString().trim();
        String carton = inventoryCarton.getText().toString().trim();
        String metreCarre = inventoryMetreCarre.getText().toString().trim();

        clearErrors();
        if (!validateFields(article, pallet, carton, metreCarre)) return;

        showConfirmationDialog(equipe, depot, zone, article, pallet, carton, metreCarre);
    }

    private void showConfirmationDialog(String equipe, String depot, String zone,
                                         String article, String pallet, String carton,
                                         String metreCarre) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 24, 40, 8);

        String[][] rows = {
                {"Équipe", equipe},
                {"Dépôt", depot},
                {"Zone", zone},
                {"Article", article},
                {"Pallet", pallet},
                {"Carton", carton},
                {"Mètre Carré", metreCarre + " m²"},
                {"Opérateur", UserSession.getInstance().getEmail()}
        };

        for (String[] row : rows) {
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setPadding(0, 8, 0, 8);

            TextView label = new TextView(this);
            label.setText(row[0] + ":  ");
            label.setTextSize(15);
            label.setTextColor(getColor(R.color.on_surface));
            label.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            TextView value = new TextView(this);
            value.setText(row[1]);
            value.setTextSize(15);
            value.setTextColor(getColor(R.color.on_surface));
            value.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));

            rowLayout.addView(label);
            rowLayout.addView(value);

            View divider = new View(this);
            divider.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(getColor(R.color.outline));

            layout.addView(rowLayout);
            layout.addView(divider);
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Inventory")
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("Confirm", (dialog, which) -> {
                    InventoryRequest request = new InventoryRequest(
                            "",
                            depot,
                            equipe,
                            zone,
                            article,
                            pallet.isEmpty() ? BigDecimal.ZERO : new BigDecimal(pallet),
                            carton.isEmpty() ? BigDecimal.ZERO : new BigDecimal(carton),
                            metreCarre.isEmpty() ? BigDecimal.ZERO : new BigDecimal(metreCarre),
                            UserSession.getInstance().getEmail()
                    );
                    sendInventory(request);
                })
                .setNegativeButton("Edit", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sendInventory(InventoryRequest request) {
        setLoading(true);

        ApiService api = RetrofitClient.getApiService();
        api.submitInventory(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                setLoading(false);
                if (response.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.inventoryBtn), getString(R.string.inventory_submitted), Snackbar.LENGTH_SHORT).show();
                    inventoryArticle.setText(null);
                    inventoryPallet.setText(null);
                    inventoryCarton.setText(null);
                    inventoryMetreCarre.setText(null);
                    inventoryArticle.requestFocus();
                } else {
                    inventoryError.setText(getString(R.string.inventory_error));
                    inventoryError.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                setLoading(false);
                inventoryError.setText(getString(R.string.inventory_error));
                inventoryError.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean validateFields(String article, String pallet, String carton, String metreCarre) {
        if (article.isEmpty()) {
            inventoryArticleLayout.setError(getString(R.string.error_required));
            inventoryArticle.requestFocus();
            return false;
        }
        if (pallet.isEmpty()) {
            inventoryPalletLayout.setError(getString(R.string.error_required));
            inventoryPallet.requestFocus();
            return false;
        }
        if (carton.isEmpty()) {
            inventoryCartonLayout.setError(getString(R.string.error_required));
            inventoryCarton.requestFocus();
            return false;
        }
        if (metreCarre.isEmpty()) {
            inventoryMetreCarreLayout.setError(getString(R.string.error_required));
            inventoryMetreCarre.requestFocus();
            return false;
        }
        return true;
    }

    private void setupDropdowns() {
        ArrayAdapter<CharSequence> equipeAdapter = ArrayAdapter.createFromResource(this,
                R.array.equipe_options, android.R.layout.simple_dropdown_item_1line);
        inventoryEquipe.setAdapter(equipeAdapter);

        ArrayAdapter<CharSequence> depotAdapter = ArrayAdapter.createFromResource(this,
                R.array.depot_options, android.R.layout.simple_dropdown_item_1line);
        inventoryDepot.setAdapter(depotAdapter);

        ArrayAdapter<CharSequence> zoneAdapter = ArrayAdapter.createFromResource(this,
                R.array.zone_options, android.R.layout.simple_dropdown_item_1line);
        inventoryZone.setAdapter(zoneAdapter);


        //to stop writing
           inventoryEquipe.setInputType(InputType.TYPE_NULL);
           inventoryDepot.setInputType(InputType.TYPE_NULL);
           inventoryZone.setInputType(InputType.TYPE_NULL);

           inventoryEquipe.setOnClickListener(v -> inventoryEquipe.showDropDown());
           inventoryDepot.setOnClickListener(v -> inventoryDepot.showDropDown());
           inventoryZone.setOnClickListener(v -> inventoryZone.showDropDown());
    }

    private String downloadDepot, downloadEquipe, downloadZone;

    private void downloadCsv() {
        String[] equipes = getResources().getStringArray(R.array.equipe_options);
        String[] depots = getResources().getStringArray(R.array.depot_options);
        String[] zones = getResources().getStringArray(R.array.zone_options);

        String[] allEquipes = new String[equipes.length + 1];
        String[] allDepots = new String[depots.length + 1];
        String[] allZones = new String[zones.length + 1];

        allEquipes[0] = "All"; allDepots[0] = "All"; allZones[0] = "All";
        System.arraycopy(equipes, 0, allEquipes, 1, equipes.length);
        System.arraycopy(depots, 0, allDepots, 1, depots.length);
        System.arraycopy(zones, 0, allZones, 1, zones.length);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 16, 40, 8);

        AutoCompleteTextView depotDropdown = addDropdown(layout, "Dépôt", allDepots);
        AutoCompleteTextView equipeDropdown = addDropdown(layout, "Équipe", allEquipes);
        AutoCompleteTextView zoneDropdown = addDropdown(layout, "Zone", allZones);

        new AlertDialog.Builder(this)
                .setTitle("Download CSV - Filters")
                .setView(layout)
                .setPositiveButton("Download", (dialog, which) -> {
                    downloadDepot = depotDropdown.getText().toString().equals("All") ? null : depotDropdown.getText().toString();
                    downloadEquipe = equipeDropdown.getText().toString().equals("All") ? null : equipeDropdown.getText().toString();
                    downloadZone = zoneDropdown.getText().toString().equals("All") ? null : zoneDropdown.getText().toString();
                    fetchAndSaveCsv();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private AutoCompleteTextView addDropdown(LinearLayout parent, String hint, String[] items) {
        TextView label = new TextView(this);
        label.setText(hint);
        label.setTextColor(getColor(R.color.on_surface));
        label.setTextSize(14);
        parent.addView(label);

        AutoCompleteTextView ac = new AutoCompleteTextView(this);
        ac.setInputType(InputType.TYPE_NULL);
        ac.setFocusable(false);
        ac.setClickable(true);
        ac.setText(items[0]);
        ac.setTextColor(getColor(R.color.on_surface));
        ac.setHintTextColor(getColor(R.color.on_surface_variant));
        ac.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, items));
        ac.setOnClickListener(v -> ac.showDropDown());
        ac.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 24));

        parent.addView(ac);
        parent.addView(spacer);
        return ac;
    }

    private void fetchAndSaveCsv() {
        setLoading(true);

        ApiService api = RetrofitClient.getApiService();
        api.downloadCsv(downloadDepot, downloadEquipe, downloadZone)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                final byte[] data = response.body().bytes();
                                File cacheFile = new File(getCacheDir(), "inventory.csv");
                                FileOutputStream fos = new FileOutputStream(cacheFile);
                                fos.write(data);
                                fos.close();

                                String msg = "Saved to " + cacheFile.getAbsolutePath();

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.Downloads.DISPLAY_NAME, "inventory.csv");
                                    values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
                                    values.put(MediaStore.Downloads.IS_PENDING, 1);
                                    Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                                    if (uri != null) {
                                        java.io.InputStream is = new java.io.FileInputStream(cacheFile);
                                        java.io.OutputStream os = getContentResolver().openOutputStream(uri);
                                        if (os != null) {
                                            byte[] buf = new byte[4096];
                                            int n;
                                            while ((n = is.read(buf)) > 0) os.write(buf, 0, n);
                                            is.close();
                                            os.close();
                                            values.clear();
                                            values.put(MediaStore.Downloads.IS_PENDING, 0);
                                            getContentResolver().update(uri, values, null, null);
                                            msg = "Saved to Downloads/inventory.csv";
                                        }
                                    }
                                } else {
                                    int permission = ContextCompat.checkSelfPermission(InventoryActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    if (permission != PackageManager.PERMISSION_GRANTED) {
                                        pendingCsvData = data;
                                        ActivityCompat.requestPermissions(InventoryActivity.this,
                                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                                WRITE_PERMISSION_CODE);
                                        Toast.makeText(InventoryActivity.this, msg, Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    msg = saveToDownloads(data);
                                }

                                Toast.makeText(InventoryActivity.this, msg, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                inventoryError.setText("Save error: " + e.getMessage());
                                inventoryError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            inventoryError.setText(getString(R.string.inventory_error));
                            inventoryError.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        setLoading(false);
                        inventoryError.setText(getString(R.string.inventory_error));
                        inventoryError.setVisibility(View.VISIBLE);
                    }
                });
    }

    private String saveToDownloads(byte[] data) {
        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (dir.exists() || dir.mkdirs()) {
                File dest = new File(dir, "inventory.csv");
                FileOutputStream out = new FileOutputStream(dest);
                out.write(data);
                out.close();
                return "Saved to Downloads/inventory.csv";
            }
        } catch (Exception ignored) {}
        return "Saved to " + getCacheDir() + "/inventory.csv";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_CODE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && pendingCsvData != null) {
            String msg = saveToDownloads(pendingCsvData);
            pendingCsvData = null;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats("QR_CODE");
        options.setPrompt("Scan a QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }
}