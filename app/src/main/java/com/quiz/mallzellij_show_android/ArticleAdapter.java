package com.quiz.mallzellij_show_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.quiz.mallzellij_show_android.model.Article;

import java.util.ArrayList;
import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private final OnArticleClickListener listener;

    public interface OnArticleClickListener {
        void onArticleClick(Article article);
    }

    public ArticleAdapter(OnArticleClickListener listener) {
        this.listener = listener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.name.setText(article.getNom());
        Integer qty = article.getQuantiteALouer();
        if (qty != null && qty > 0) {
            holder.quantite.setVisibility(View.VISIBLE);
            holder.quantite.setText("À louer: " + qty);
        } else {
            holder.quantite.setVisibility(View.GONE);
        }
        holder.viewStockBtn.setOnClickListener(v -> listener.onArticleClick(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView quantite;
        MaterialButton viewStockBtn;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.articleName);
            quantite = itemView.findViewById(R.id.articleQuantite);
            viewStockBtn = itemView.findViewById(R.id.viewStockBtn);
        }
    }
}
