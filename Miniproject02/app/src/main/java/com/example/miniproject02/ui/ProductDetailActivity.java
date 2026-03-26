package com.example.miniproject02.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {

    private static final int REQ_LOGIN = 1001;

    private ProductDao productDao;
    private OrderDao orderDao;
    private OrderDetailDao orderDetailDao;
    private SessionManager sessionManager;
    private Product product;
    private boolean pendingGoToCheckout = false;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        long productId = getIntent().getLongExtra("product_id", -1);
        if (productId == -1) {
            finish();
            return;
        }

        AppDatabase db = AppDatabase.getInstance(this);
        productDao = db.productDao();
        orderDao = db.orderDao();
        orderDetailDao = db.orderDetailDao();
        sessionManager = new SessionManager(this);

        product = productDao.findById(productId);

        TextView txtName = findViewById(R.id.txtDetailName);
        TextView txtPrice = findViewById(R.id.txtDetailPrice);
        TextView txtDescription = findViewById(R.id.txtDetailDescription);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        if (product != null) {
            txtName.setText(product.name);
            txtPrice.setText("Giá: " + currencyFormat.format(product.price));
            txtDescription.setText(product.description);
        }

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePickProduct(false);
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePickProduct(true);
            }
        });
    }

    private void handlePickProduct(boolean goToCheckout) {
        // Nhặt hàng -> Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn()) {
            // Chưa đăng nhập: chuyển sang Login và đợi kết quả
            pendingGoToCheckout = goToCheckout;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, REQ_LOGIN);
        } else {
            // Đã đăng nhập: tạo Order (nếu chưa có) + OrderDetails
            addToCartAndContinue(goToCheckout);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_LOGIN) {
            if (resultCode == RESULT_OK && sessionManager.isLoggedIn()) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                // Sau khi login thành công: tạo Order (nếu chưa có) + OrderDetails
                addToCartAndContinue(pendingGoToCheckout);
            } else {
                Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addToCartAndContinue(boolean goToCheckout) {
        long userId = sessionManager.getUserId();
        Order order = orderDao.getPendingOrderForUser(userId);
        if (order == null) {
            order = new Order();
            order.userId = userId;
            order.status = "PENDING";
            order.createdAt = System.currentTimeMillis();
            long orderId = orderDao.insert(order);
            order.id = orderId;
        }

        OrderDetail detail = new OrderDetail();
        OrderDetail existing = orderDetailDao.findByOrderAndProduct(order.id, product.id);
        if (existing != null) {
            existing.quantity += 1;
            orderDetailDao.update(existing);
        } else {
            detail.orderId = order.id;
            detail.productId = product.id;
            detail.quantity = 1;
            orderDetailDao.insert(detail);
        }

        Toast.makeText(this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();

        if (goToCheckout) {
            Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
            intent.putExtra("order_id", order.id);
            startActivity(intent);
            return;
        }

        // Hỏi người dùng có muốn tiếp tục chọn sản phẩm hay không
        showContinueDialog(order.id);
    }

    private void showContinueDialog(long orderId) {
        new AlertDialog.Builder(this)
                .setTitle("Tiếp tục mua hàng")
                .setMessage("Bạn có muốn tiếp tục chọn sản phẩm không?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Quay lại danh sách Products
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ProductDetailActivity.this, CheckoutActivity.class);
                        intent.putExtra("order_id", orderId);
                        startActivity(intent);
                    }
                })
                .show();
    }
}
