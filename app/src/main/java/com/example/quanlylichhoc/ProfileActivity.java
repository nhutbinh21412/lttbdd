package com.example.quanlylichhoc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Khởi tạo các view
        setupViews();

        // Xử lý Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavProfile);
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(this, SubjectListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_attendance) {
                startActivity(new Intent(this, SubjectListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại thông tin mỗi khi quay lại màn hình này
        setupViews();
    }

    private void setupViews() {
        TextView txtProfileName = findViewById(R.id.txtProfileName);
        TextView txtProfileId = findViewById(R.id.txtProfileId);
        LinearLayout btnLogout = findViewById(R.id.btnLogout);

        // Lấy thông tin từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "N/A");
        String mssv = prefs.getString("mssv", "");
        int userId = prefs.getInt("userId", 0);
        String role = prefs.getString("role", "Student");

        txtProfileName.setText(fullName);
        if (role.equalsIgnoreCase("Teacher") || role.equalsIgnoreCase("Lecturer")) {
            txtProfileId.setText("Mã giảng viên: " + userId);
        } else {
            if (mssv != null && !mssv.isEmpty()) {
                txtProfileId.setText("MSSV: " + mssv);
            } else {
                txtProfileId.setText("MSSV: " + userId);
            }
        }

        // Mở màn hình Thông tin sinh viên
        findViewById(R.id.btnStudentInfo).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, StudentInfoActivity.class));
        });

        // Mở màn hình Đổi mật khẩu
        findViewById(R.id.btnChangePassword).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
        });

        // Xử lý đăng xuất
        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
