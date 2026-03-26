package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.Category;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.CategoryDao;

import java.util.List;

public class CategoriesActivity extends AppCompatActivity implements CategoryAdapter.OnCategoryClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        CategoryDao categoryDao = AppDatabase.getInstance(this).categoryDao();
        List<Category> categories = categoryDao.getAll();

        RecyclerView recyclerView = findViewById(R.id.recyclerCategories);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CategoryAdapter(categories, this));
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, ProductsActivity.class);
        intent.putExtra("category_id", category.id);
        intent.putExtra("category_name", category.name);
        startActivity(intent);
    }
}
