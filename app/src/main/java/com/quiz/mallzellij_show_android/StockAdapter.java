package com.quiz.mallzellij_show_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiz.mallzellij_show_android.model.ArticleStock;

import java.util.ArrayList;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<ArticleStock> stocks = new ArrayList<>();

    public void setStocks(List<ArticleStock> stocks) {
        this.stocks = stocks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleStock stock = stocks.get(position);
        holder.siteName.setText(stock.getSiteName());
        holder.quantity.setText(String.valueOf(stock.getQuantity()));
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteName, quantity;

        ViewHolder(View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.siteName);
            quantity = itemView.findViewById(R.id.quantity);
        }
    }
}
