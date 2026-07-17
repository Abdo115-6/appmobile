package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.Article;
import com.quiz.mallzellij_show_android.UserSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticlesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArticleAdapter adapter;
    private ProgressBar progress;
    private TextView errorView;
    private TextInputEditText searchInput;
    private List<Article> allArticles = new ArrayList<>();
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private SharedPreferences favPrefs;
    private Set<String> favoriteRefs = new HashSet<>();

    private int currentSortMode = 0; // 0 = fav first, 1 = in-stock first, 2 = alpha

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    lookupBarcode(result.getContents());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        favPrefs = getSharedPreferences("favorites", MODE_PRIVATE);
        favoriteRefs = new HashSet<>(favPrefs.getStringSet("refs", new HashSet<>()));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_logout) {
                UserSession.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            } else if (id == R.id.action_sort_fav) {
                currentSortMode = 0;
                item.setChecked(true);
                sortAndRefresh();
                return true;
            } else if (id == R.id.action_sort_stock) {
                currentSortMode = 1;
                item.setChecked(true);
                sortAndRefresh();
                return true;
            } else if (id == R.id.action_sort_alpha) {
                currentSortMode = 2;
                item.setChecked(true);
                sortAndRefresh();
                return true;
            }
            return false;
        });

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navView = findViewById(R.id.navView);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navView.setCheckedItem(R.id.nav_articles);
        if (!UserSession.getInstance().isAdmin() && !UserSession.getInstance().isSuperuser()) {
            navView.getMenu().findItem(R.id.nav_inventory).setVisible(false);
        }
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inventory) {
                startActivity(new Intent(this, InventoryActivity.class));
                finish();
            } else if (id == R.id.nav_devi) {
                startActivity(new Intent(this, DevisActivity.class));
                finish();
            } else if (id == R.id.nav_logout) {
                UserSession.getInstance().logout();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        recyclerView = findViewById(R.id.articlesRecyclerView);
        progress = findViewById(R.id.articlesProgress);
        errorView = findViewById(R.id.articlesError);
        searchInput = findViewById(R.id.searchInput);
        ImageButton scanButton = findViewById(R.id.scanButton);
        scanButton.setOnClickListener(v -> startScan());

        adapter = new ArticleAdapter(new ArticleAdapter.OnArticleActionListener() {
            @Override
            public void onArticleClick(Article article) {
                Intent intent = new Intent(ArticlesActivity.this, ArticleStockActivity.class);
                intent.putExtra("article_id", article.getId());
                intent.putExtra("article_name", article.getNom());
                startActivity(intent);
            }

            @Override
            public void onFavoriteClick(Article article, boolean isFavorited) {
                String ref = article.getRef();
                if (isFavorited) {
                    favoriteRefs.add(ref);
                } else {
                    favoriteRefs.remove(ref);
                }
                favPrefs.edit().putStringSet("refs", favoriteRefs).apply();
                adapter.setFavoriteRefs(favoriteRefs);
                Toast.makeText(ArticlesActivity.this,
                        isFavorited ? "Added to favorites" : "Removed from favorites",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAddToQuoteClick(Article article) {
                Intent intent = new Intent(ArticlesActivity.this, DevisActivity.class);
                intent.putExtra("article_ref", article.getRef());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setFavoriteRefs(favoriteRefs);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> filterArticles(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 400);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadArticles();
    }

    private void loadArticles() {
        progress.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        RetrofitClient.getApiService().getArticles().enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allArticles = response.body();
                    sortAndRefresh();
                } else {
                    showError("Failed to load articles");
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError("Connection error: " + t.getMessage());
            }
        });
    }

    private void sortAndRefresh() {
        List<Article> sorted = new ArrayList<>(allArticles);
        Comparator<Article> comp;
        switch (currentSortMode) {
            case 0:
                comp = (a, b) -> {
                    boolean aFav = favoriteRefs.contains(a.getRef());
                    boolean bFav = favoriteRefs.contains(b.getRef());
                    if (aFav != bFav) return aFav ? -1 : 1;
                    boolean aStock = a.getPhysicalStock() > 0;
                    boolean bStock = b.getPhysicalStock() > 0;
                    if (aStock != bStock) return aStock ? -1 : 1;
                    return a.getNom().compareToIgnoreCase(b.getNom());
                };
                break;
            case 1:
                comp = (a, b) -> {
                    boolean aStock = a.getPhysicalStock() > 0;
                    boolean bStock = b.getPhysicalStock() > 0;
                    if (aStock != bStock) return aStock ? -1 : 1;
                    return a.getNom().compareToIgnoreCase(b.getNom());
                };
                break;
            default:
                comp = Comparator.comparing(Article::getNom, String.CASE_INSENSITIVE_ORDER);
                break;
        }
        Collections.sort(sorted, comp);
        adapter.setArticles(sorted);
    }

    private void filterArticles(String query) {
        if (query.isEmpty()) {
            sortAndRefresh();
            return;
        }

        progress.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().searchArticles(query).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<Article> list = response.body();
                    Comparator<Article> comp;
                    switch (currentSortMode) {
                        case 0:
                            comp = (a, b) -> {
                                boolean aFav = favoriteRefs.contains(a.getRef());
                                boolean bFav = favoriteRefs.contains(b.getRef());
                                if (aFav != bFav) return aFav ? -1 : 1;
                                boolean aStock = a.getPhysicalStock() > 0;
                                boolean bStock = b.getPhysicalStock() > 0;
                                if (aStock != bStock) return aStock ? -1 : 1;
                                return a.getNom().compareToIgnoreCase(b.getNom());
                            };
                            break;
                        case 1:
                            comp = (a, b) -> {
                                boolean aStock = a.getPhysicalStock() > 0;
                                boolean bStock = b.getPhysicalStock() > 0;
                                if (aStock != bStock) return aStock ? -1 : 1;
                                return a.getNom().compareToIgnoreCase(b.getNom());
                            };
                            break;
                        default:
                            comp = Comparator.comparing(Article::getNom, String.CASE_INSENSITIVE_ORDER);
                            break;
                    }
                    Collections.sort(list, comp);
                    adapter.setArticles(list);
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void startScan() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats("QR_CODE");
        options.setPrompt("Scan a QR code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);
        barcodeLauncher.launch(options);
    }

    private void lookupBarcode(String barcode) {
        progress.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        String ref = barcode.replaceFirst("\\s.*", "");
        Log.d("SCAN", "raw barcode: [" + barcode + "]");
        Log.d("SCAN", "extracted ref: [" + ref + "]");
        RetrofitClient.getApiService().getArticleByBarcode(ref).enqueue(new Callback<Article>() {
            @Override
            public void onResponse(Call<Article> call, Response<Article> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    Article article = response.body();
                    Intent intent = new Intent(ArticlesActivity.this, DevisActivity.class);
                    intent.putExtra("article_ref", article.getRef());
                    startActivity(intent);
                } else {
                    String msg = "Server error: " + response.code() + " for ref: [" + ref + "]";
                    showError(msg);
                }
            }

            @Override
            public void onFailure(Call<Article> call, Throwable t) {
                progress.setVisibility(View.GONE);
                String msg = "Connection error: " + t.getMessage();
                showError(msg);
            }
        });
    }

    private void showError(String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
}
