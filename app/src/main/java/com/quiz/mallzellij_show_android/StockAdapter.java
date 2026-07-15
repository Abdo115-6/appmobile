package com.quiz.mallzellij_show_android;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.quiz.mallzellij_show_android.model.ArticleStock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StockAdapter extends RecyclerView.Adapter<StockAdapter.ViewHolder> {

    private List<ArticleStock> stocks = new ArrayList<>();

    public void setStocks(List<ArticleStock> stocks) {
        Collections.sort(stocks, (a, b) -> {
            boolean aZero = a.getQuantity() == 0;
            boolean bZero = b.getQuantity() == 0;
            if (aZero != bZero) return aZero ? 1 : -1;
            return Integer.compare(a.getQuantity(), b.getQuantity());
        });
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
        holder.siteText.setText(stock.getSiteName());
        holder.quantityText.setText(String.format("%,d", stock.getQuantity()));
        if (stock.getQuantiteALouer() != null) {
            holder.quantiteALouer.setText(String.format("%,d", stock.getQuantiteALouer()));
            holder.quantiteALouer.setVisibility(View.VISIBLE);
            holder.aLouerSite.setVisibility(View.VISIBLE);
        } else {
            holder.quantiteALouer.setVisibility(View.GONE);
            holder.aLouerSite.setVisibility(View.GONE);
        }
        if (stock.getPrix() != null) {
            holder.prixValue.setText(String.format("%.2f", stock.getPrix().doubleValue()));
            holder.prixValue.setVisibility(View.VISIBLE);
            holder.prixLabel.setVisibility(View.VISIBLE);
        } else {
            holder.prixValue.setVisibility(View.GONE);
            holder.prixLabel.setVisibility(View.GONE);
        }
        boolean canSeeAll = UserSession.getInstance().isSuperuser() || UserSession.getInstance().isAdmin();
        if (canSeeAll && stock.getPrixPromo() != null) {
            holder.prixPromoRow.setVisibility(View.VISIBLE);
            holder.prixPromoValue.setText(String.format("%.2f", stock.getPrixPromo()));
        } else {
            holder.prixPromoRow.setVisibility(View.GONE);
        }
        if (canSeeAll && stock.getPrixPrevendor() != null) {
            holder.prixPrevendorRow.setVisibility(View.VISIBLE);
            holder.prixPrevendorValue.setText(String.format("%.2f", stock.getPrixPrevendor()));
        } else {
            holder.prixPrevendorRow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stocks.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView siteText, quantityText, aLouerSite, quantiteALouer, prixLabel, prixValue;
        LinearLayout prixPromoRow, prixPrevendorRow;
        TextView prixPromoValue, prixPrevendorValue;

        ViewHolder(View itemView) {
            super(itemView);
            siteText = itemView.findViewById(R.id.siteText);
            quantityText = itemView.findViewById(R.id.quantityText);
            aLouerSite = itemView.findViewById(R.id.aLouerSite);
            quantiteALouer = itemView.findViewById(R.id.quantiteALouer);
            prixLabel = itemView.findViewById(R.id.prixLabel);
            prixValue = itemView.findViewById(R.id.prixValue);
            prixPromoRow = itemView.findViewById(R.id.prixPromoRow);
            prixPrevendorRow = itemView.findViewById(R.id.prixPrevendorRow);
            prixPromoValue = itemView.findViewById(R.id.prixPromoValue);
            prixPrevendorValue = itemView.findViewById(R.id.prixPrevendorValue);
        }
    }
}
