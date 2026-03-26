package com.example.miniproject02.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.miniproject02.R;
import com.example.miniproject02.data.entity.User;
import com.example.miniproject02.data.local.AppDatabase;
import com.example.miniproject02.data.local.UserDao;
import com.example.miniproject02.session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText edtUsername, edtPassword;
    private SessionManager sessionManager;
    private UserDao userDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        userDao = AppDatabase.getInstance(this).userDao();

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = userDao.findByUsername(username);
        if (user == null || !password.equals(user.password)) {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        sessionManager.login(user.id, user.username);
        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();

        // If login is requested from another screen for result, return result only.
        if (getCallingActivity() != null) {
            setResult(RESULT_OK, new Intent());
            finish();
            return;
        }

        Intent intent = new Intent(this, ProductsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
