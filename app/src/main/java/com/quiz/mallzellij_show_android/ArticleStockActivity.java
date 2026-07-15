package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.ArticleStock;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleStockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockAdapter adapter;
    private ProgressBar progress;
    private TextView errorView, articleNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        long articleId = getIntent().getLongExtra("article_id", -1);
        String articleName = getIntent().getStringExtra("article_name");

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
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
            } else if (id == R.id.nav_articles) {
                startActivity(new Intent(this, ArticlesActivity.class));
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        articleNameView = findViewById(R.id.articleName);
        articleNameView.setText(articleName);

        recyclerView = findViewById(R.id.stockRecyclerView);
        progress = findViewById(R.id.stockProgress);
        errorView = findViewById(R.id.stockError);

        adapter = new StockAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadStocks(articleId);
    }

    private void loadStocks(Long articleId) {
        progress.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);

        RetrofitClient.getApiService().getArticleStocks(articleId).enqueue(new Callback<List<ArticleStock>>() {
            @Override
            public void onResponse(Call<List<ArticleStock>> call, Response<List<ArticleStock>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setStocks(response.body());
                } else {
                    showError("Failed to load stock details");
                }
            }

            @Override
            public void onFailure(Call<List<ArticleStock>> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError("Connection error: " + t.getMessage());
            }
        });
    }

    private void showError(String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
}
