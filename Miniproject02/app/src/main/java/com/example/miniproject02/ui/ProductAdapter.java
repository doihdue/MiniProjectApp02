package com.example.miniproject02.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.Product;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> originalProducts;
    private final List<Product> displayProducts;
    private final OnProductClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private String searchKeyword = "";
    private long selectedCategoryId = -1;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.originalProducts = new ArrayList<>(products);
        this.displayProducts = new ArrayList<>(products);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = displayProducts.get(position);
        holder.txtName.setText(product.name);
        holder.txtDescription.setText(product.description);
        holder.txtPrice.setText(currencyFormat.format(product.price));
        holder.txtBadge.setText(position % 2 == 0 ? "Hot" : "Moi");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductClick(product);
        });
    }

    @Override
    public int getItemCount() {
        return displayProducts.size();
    }

    public void filter(String keyword) {
        searchKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        applyFilters();
    }

    public void setCategoryFilter(long categoryId) {
        selectedCategoryId = categoryId;
        applyFilters();
    }

    private void applyFilters() {
        displayProducts.clear();

        for (Product product : originalProducts) {
            String name = product.name == null ? "" : product.name.toLowerCase(Locale.ROOT);
            String description = product.description == null ? "" : product.description.toLowerCase(Locale.ROOT);

            boolean matchesKeyword = searchKeyword.isEmpty()
                    || name.contains(searchKeyword)
                    || description.contains(searchKeyword);
            boolean matchesCategory = selectedCategoryId <= 0 || product.categoryId == selectedCategoryId;

            if (matchesKeyword && matchesCategory) {
                displayProducts.add(product);
            }
        }

        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtBadge, txtName, txtDescription, txtPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBadge = itemView.findViewById(R.id.txtProductBadge);
            txtName = itemView.findViewById(R.id.txtProductName);
            txtDescription = itemView.findViewById(R.id.txtProductDescription);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
        }
    }
}
