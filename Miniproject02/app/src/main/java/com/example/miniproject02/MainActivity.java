package com.example.miniproject02;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miniproject02.data.SeedDataInitializer;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.session.SessionManager;
import com.example.miniproject02.ui.CartActivity;
import com.example.miniproject02.ui.CategoriesActivity;
import com.example.miniproject02.ui.LoginActivity;
import com.example.miniproject02.ui.ProductsActivity;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView txtHomeStatus;
    private TextView txtHomeSubStatus;
    private TextView txtStatProducts;
    private TextView txtStatCategories;
    private Button btnLogin;
    private Button btnLogout;
    private Button btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Seed initial data
        SeedDataInitializer.seed(this);

        sessionManager = new SessionManager(this);

        txtHomeStatus = findViewById(R.id.txtHomeStatus);
        txtHomeSubStatus = findViewById(R.id.txtHomeSubStatus);
        txtStatProducts = findViewById(R.id.txtStatProducts);
        txtStatCategories = findViewById(R.id.txtStatCategories);
        btnLogin = findViewById(R.id.btnLoginHome);
        btnLogout = findViewById(R.id.btnLogoutHome);
        Button btnProducts = findViewById(R.id.btnProductsHome);
        Button btnCategories = findViewById(R.id.btnCategoriesHome);
        btnCart = findViewById(R.id.btnCartHome);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logout();
                Toast.makeText(MainActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                updateLoginState();
            }
        });

        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class));
            }
        });

        btnCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CategoriesActivity.class));
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CartActivity.class));
            }
        });

        updateLoginState();
        updateStats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginState();
        updateStats();
    }

    private void updateLoginState() {
        boolean isLoggedIn = sessionManager.isLoggedIn();
        if (isLoggedIn) {
            txtHomeStatus.setText("Xin chao, " + sessionManager.getUsername());
            txtHomeSubStatus.setText("San sang tao don va thanh toan nhanh");
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
            btnCart.setVisibility(View.VISIBLE);
        } else {
            txtHomeStatus.setText("Ban chua dang nhap");
            txtHomeSubStatus.setText("Dang nhap de tao hoa don nhanh hon");
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
            btnCart.setVisibility(View.GONE);
        }
    }

    private void updateStats() {
        AppDatabase db = AppDatabase.getInstance(this);
        txtStatProducts.setText(String.valueOf(db.productDao().count()));
        txtStatCategories.setText(String.valueOf(db.categoryDao().count()));
    }
}