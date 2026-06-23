package com.quiz.mallzellij_show_android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.quiz.mallzellij_show_android.api.RetrofitClient;
import com.quiz.mallzellij_show_android.model.Article;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });

        recyclerView = findViewById(R.id.articlesRecyclerView);
        progress = findViewById(R.id.articlesProgress);
        errorView = findViewById(R.id.articlesError);
        searchInput = findViewById(R.id.searchInput);

        adapter = new ArticleAdapter(article -> {
            Intent intent = new Intent(this, ArticleStockActivity.class);
            intent.putExtra("article_id", article.getId());
            intent.putExtra("article_name", article.getNom());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                    adapter.setArticles(allArticles);
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

    private void filterArticles(String query) {
        if (query.isEmpty()) {
            adapter.setArticles(allArticles);
            return;
        }

        progress.setVisibility(View.VISIBLE);

        RetrofitClient.getApiService().searchArticles(query).enqueue(new Callback<List<Article>>() {
            @Override
            public void onResponse(Call<List<Article>> call, Response<List<Article>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setArticles(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Article>> call, Throwable t) {
                progress.setVisibility(View.GONE);
            }
        });
    }

    private void showError(String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
}
