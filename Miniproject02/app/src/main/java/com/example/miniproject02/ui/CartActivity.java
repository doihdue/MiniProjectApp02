package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.Order;
import com.example.miniproject02.data.entity.OrderDetail;
import com.example.miniproject02.data.entity.Product;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.OrderDao;
import com.example.miniproject02.data.local.OrderDetailDao;
import com.example.miniproject02.data.local.ProductDao;
import com.example.miniproject02.session.SessionManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        TextView txtCartStatus = findViewById(R.id.txtCartStatus);
        TextView txtCartSummary = findViewById(R.id.txtCartSummary);
        Button btnCartAction = findViewById(R.id.btnCartAction);
        Button btnContinueShopping = findViewById(R.id.btnContinueShopping);

        btnContinueShopping.setOnClickListener(v -> {
            startActivity(new Intent(this, ProductsActivity.class));
            finish();
        });

        SessionManager sessionManager = new SessionManager(this);
        AppDatabase db = AppDatabase.getInstance(this);
        OrderDao orderDao = db.orderDao();
        OrderDetailDao detailDao = db.orderDetailDao();
        ProductDao productDao = db.productDao();

        if (!sessionManager.isLoggedIn()) {
            txtCartStatus.setText("Ban can dang nhap de xem gio hang");
            txtCartSummary.setText("Vui long dang nhap roi quay lai gio hang.");
            btnCartAction.setText("Dang nhap");
            btnCartAction.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
            btnContinueShopping.setVisibility(View.GONE);
            return;
        }

        Order pendingOrder = orderDao.getPendingOrderForUser(sessionManager.getUserId());
        if (pendingOrder == null) {
            txtCartStatus.setText("Gio hang trong");
            txtCartSummary.setText("Ban chua co san pham nao trong gio.");
            btnCartAction.setText("Mua san pham");
            btnCartAction.setOnClickListener(v -> startActivity(new Intent(this, ProductsActivity.class)));
            btnContinueShopping.setVisibility(View.GONE);
            return;
        }

        List<OrderDetail> details = detailDao.getByOrder(pendingOrder.id);
        if (details.isEmpty()) {
            txtCartStatus.setText("Gio hang trong");
            txtCartSummary.setText("Ban chua co san pham nao trong gio.");
            btnCartAction.setText("Mua san pham");
            btnCartAction.setOnClickListener(v -> startActivity(new Intent(this, ProductsActivity.class)));
            btnContinueShopping.setVisibility(View.GONE);
            return;
        }

        double total = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("Don tam #").append(pendingOrder.id).append("\n\n");

        for (OrderDetail detail : details) {
            Product product = productDao.findById(detail.productId);
            if (product == null) {
                continue;
            }
            double line = product.price * detail.quantity;
            total += line;
            builder.append(product.name)
                    .append(" x ")
                    .append(detail.quantity)
                    .append(" = ")
                    .append(currencyFormat.format(line))
                    .append("\n");
        }

        builder.append("\nTong tien: ").append(currencyFormat.format(total));
        txtCartStatus.setText("Gio hang cua ban");
        txtCartSummary.setText(builder.toString());
        btnCartAction.setText("Di toi thanh toan");
        btnContinueShopping.setVisibility(View.VISIBLE);
        btnCartAction.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("order_id", pendingOrder.id);
            startActivity(intent);
            finish();
        });
    }
}
