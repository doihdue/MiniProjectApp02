package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.Category;
import com.example.miniproject02.data.entity.Product;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.CategoryDao;
import com.example.miniproject02.data.local.ProductDao;
import com.example.miniproject02.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {

    private ProductDao productDao;
    private CategoryDao categoryDao;
    private ProductAdapter adapter;
    private TextView txtSubtitle;
    private final List<Long> categoryIds = new ArrayList<>();
    private SessionManager sessionManager;
    private Button btnCart;
    private Button btnLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        AppDatabase db = AppDatabase.getInstance(this);
        productDao = db.productDao();
        categoryDao = db.categoryDao();
        sessionManager = new SessionManager(this);

        TextView txtTitle = findViewById(R.id.txtProductsTitle);
        txtSubtitle = findViewById(R.id.txtProductsSubtitle);
        EditText edtSearch = findViewById(R.id.edtSearchProducts);
        Spinner spnCategory = findViewById(R.id.spnCategoryFilter);
        btnCart = findViewById(R.id.btnProductsCart);
        btnLogout = findViewById(R.id.btnProductsLogout);

        RecyclerView recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        long categoryId = getIntent().getLongExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");
        List<Product> products = productDao.getAll();
        txtTitle.setText("Danh sach san pham");

        adapter = new ProductAdapter(products, this);
        recyclerView.setAdapter(adapter);
        setupCategorySpinner(spnCategory, categoryId);

        if (categoryId > 0 && !TextUtils.isEmpty(categoryName)) {
            Toast.makeText(this, "Dang loc danh muc: " + categoryName, Toast.LENGTH_SHORT).show();
        }

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s == null ? "" : s.toString());
                updateResultCount();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCart.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));

        btnLogout.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logout();
                Toast.makeText(this, "Da dang xuat", Toast.LENGTH_SHORT).show();
                updateAuthButtons();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        updateAuthButtons();
        updateResultCount();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAuthButtons();
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product_id", product.id);
        startActivity(intent);
    }

    private void updateResultCount() {
        int size = adapter == null ? 0 : adapter.getItemCount();
        txtSubtitle.setText(size + " san pham");
    }

    private void setupCategorySpinner(Spinner spinner, long selectedCategoryId) {
        List<String> labels = new ArrayList<>();
        labels.add("Tat ca danh muc");
        categoryIds.clear();
        categoryIds.add(-1L);

        List<Category> categories = categoryDao.getAll();
        int preselect = 0;
        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            labels.add(category.name);
            categoryIds.add(category.id);
            if (category.id == selectedCategoryId) {
                preselect = i + 1;
            }
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                labels
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setSelection(preselect, false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                long categoryId = categoryIds.get(position);
                adapter.setCategoryFilter(categoryId);
                updateResultCount();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void updateAuthButtons() {
        boolean isLoggedIn = sessionManager != null && sessionManager.isLoggedIn();
        if (isLoggedIn) {
            btnLogout.setText("Dang xuat");
            btnCart.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setText("Dang nhap");
            btnCart.setVisibility(View.GONE);
        }
    }
}
