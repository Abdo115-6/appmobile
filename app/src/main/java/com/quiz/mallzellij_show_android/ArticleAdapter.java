package com.quiz.mallzellij_show_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.quiz.mallzellij_show_android.model.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {

    private List<Article> articles = new ArrayList<>();
    private Set<String> favoriteRefs;
    private final OnArticleActionListener listener;

    public interface OnArticleActionListener {
        void onArticleClick(Article article);
        void onFavoriteClick(Article article, boolean isFavorited);
        void onAddToQuoteClick(Article article);
    }

    public ArticleAdapter(OnArticleActionListener listener) {
        this.listener = listener;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
        notifyDataSetChanged();
    }

    public void setFavoriteRefs(Set<String> favoriteRefs) {
        this.favoriteRefs = favoriteRefs;
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
        String ref = article.getRef();

        holder.name.setText(article.getNom());

        boolean inStock = article.getPhysicalStock() > 0;
        if (inStock) {
            holder.stockDot.setBackgroundResource(R.drawable.circle_green);
            holder.stockText.setText(R.string.in_stock);
            holder.stockText.setTextColor(holder.itemView.getContext().getColor(R.color.stock_green));
            holder.stockIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.stockDot.setBackgroundResource(R.drawable.circle_red);
            holder.stockText.setText(R.string.out_of_stock);
            holder.stockText.setTextColor(holder.itemView.getContext().getColor(R.color.stock_red));
            holder.stockIndicator.setVisibility(View.VISIBLE);
        }

        holder.physicalStockText.setText("Physical: " + article.getPhysicalStock());
        holder.availableStockText.setText("Reserved: " + article.getAvailableStock());

        boolean isFav = favoriteRefs != null && favoriteRefs.contains(ref);
        holder.favoriteBtn.setImageResource(isFav ? R.drawable.ic_star_filled : R.drawable.ic_star_outline);
        holder.itemView.setOnClickListener(v -> listener.onArticleClick(article));
        holder.favoriteBtn.setOnClickListener(v -> listener.onFavoriteClick(article, !isFav));

        holder.addToQuoteBtn.setOnClickListener(v -> listener.onAddToQuoteClick(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View stockDot;
        TextView stockText;
        LinearLayout stockIndicator;
        TextView name;
        TextView physicalStockText;
        TextView availableStockText;
        ImageButton favoriteBtn;
        MaterialButton addToQuoteBtn;

        ViewHolder(View itemView) {
            super(itemView);
            stockDot = itemView.findViewById(R.id.stockDot);
            stockText = itemView.findViewById(R.id.stockText);
            stockIndicator = itemView.findViewById(R.id.stockIndicator);
            name = itemView.findViewById(R.id.articleName);
            physicalStockText = itemView.findViewById(R.id.physicalStockText);
            availableStockText = itemView.findViewById(R.id.availableStockText);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
            addToQuoteBtn = itemView.findViewById(R.id.addToQuoteBtn);
        }
    }
}
