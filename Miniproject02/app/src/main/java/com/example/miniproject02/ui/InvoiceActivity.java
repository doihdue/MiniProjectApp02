package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject02.MainActivity;
import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.Order;
import com.example.miniproject02.data.entity.OrderDetail;
import com.example.miniproject02.data.entity.Product;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.OrderDao;
import com.example.miniproject02.data.local.OrderDetailDao;
import com.example.miniproject02.data.local.ProductDao;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        long orderId = getIntent().getLongExtra("order_id", -1);
        if (orderId == -1) {
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);
        OrderDao orderDao = db.orderDao();
        OrderDetailDao detailDao = db.orderDetailDao();
        ProductDao productDao = db.productDao();

        Order order = orderDao.findById(orderId);
        if (order == null) {
            finish();
            return;
        }

        TextView txtInvoice = findViewById(R.id.txtInvoice);
        Button btnBack = findViewById(R.id.btnInvoiceBack);

        List<OrderDetail> details = detailDao.getByOrder(orderId);
        double total = 0;
        StringBuilder builder = new StringBuilder();
        builder.append("Hóa đơn #").append(order.id).append("\nTrạng thái: ").append(order.status).append("\n");
        if (order.paidAt != null) {
            builder.append("Ngày thanh toán: ").append(DateFormat.getDateTimeInstance().format(new Date(order.paidAt))).append("\n\n");
        }

        for (OrderDetail d : details) {
            Product p = productDao.findById(d.productId);
            if (p == null) {
                continue;
            }
            double line = p.price * d.quantity;
            total += line;
            builder.append(p.name)
                    .append(" x ")
                    .append(d.quantity)
                    .append(" = ")
                    .append(currencyFormat.format(line))
                    .append("\n");
        }
        builder.append("\nTổng tiền: ").append(currencyFormat.format(total));

        txtInvoice.setText(builder.toString());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
