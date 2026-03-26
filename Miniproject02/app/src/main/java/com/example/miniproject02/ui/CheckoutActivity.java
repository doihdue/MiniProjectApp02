package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private long orderId;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        orderId = getIntent().getLongExtra("order_id", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);
        OrderDao orderDao = db.orderDao();
        OrderDetailDao detailDao = db.orderDetailDao();
        ProductDao productDao = db.productDao();

        TextView txtSummary = findViewById(R.id.txtSummary);
        Button btnPay = findViewById(R.id.btnPay);

        List<OrderDetail> details = detailDao.getByOrder(orderId);
        double total = 0;
        StringBuilder summary = new StringBuilder();
        for (OrderDetail d : details) {
            Product p = productDao.findById(d.productId);
            if (p == null) {
                continue;
            }
            double line = p.price * d.quantity;
            total += line;
            summary.append(p.name)
                    .append(" x ")
                    .append(d.quantity)
                    .append(" = ")
                    .append(currencyFormat.format(line))
                    .append("\n");
        }
        summary.append("\nTổng: ").append(currencyFormat.format(total));
        txtSummary.setText(summary.toString());

        btnPay.setOnClickListener(v -> {
            Order order = orderDao.findById(orderId);
            if (order != null) {
                order.status = "PAID";
                order.paidAt = System.currentTimeMillis();
                orderDao.update(order);

                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("order_id", orderId);
                startActivity(intent);
                finish();
            }
        });
    }
}
