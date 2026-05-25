package com.example.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etRegUsername);
        etPassword = findViewById(R.id.etRegPassword);
        etConfirmPassword = findViewById(R.id.etRegConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Quay lại màn hình Đăng nhập
            }
        });
    }

    private void registerUser() {
        String user = etUsername.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirmPass)) {
            Toast.makeText(this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    // Kiểm tra username đã tồn tại chưa
                    String checkQuery = "SELECT * FROM Users WHERE Username = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setString(1, user);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Tên đăng nhập đã tồn tại", Toast.LENGTH_SHORT).show());
                    } else {
                        // Thêm user mới với Role mặc định là Student và FullName mặc định là Username
                        String insertQuery = "INSERT INTO Users (Username, Password, Role, FullName) VALUES (?, ?, 'Student', ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                        insertStmt.setString(1, user);
                        insertStmt.setString(2, pass);
                        insertStmt.setString(3, user);
                        insertStmt.executeUpdate();

                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                    conn.close();
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Kết nối database thất bại", Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
