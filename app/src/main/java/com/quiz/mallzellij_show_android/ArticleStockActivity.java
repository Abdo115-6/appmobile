package com.quiz.mallzellij_show_android;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
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
        toolbar.setNavigationOnClickListener(v -> finish());

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
