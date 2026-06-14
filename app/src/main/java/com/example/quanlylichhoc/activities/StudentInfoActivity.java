package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import com.example.quanlylichhoc.storage.SharedPrefsManager;

public class StudentInfoActivity extends AppCompatActivity {

    private EditText etFullName;
    private View rowMSSV, rowFaculty, rowClass, rowEmail, rowPhone, rowAddress;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        // Sử dụng SharedPrefsManager để lấy userId
        userId = SharedPrefsManager.getInstance(this).getUserId();
        
        if (userId == -1) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etFullName = findViewById(R.id.etFullName);
        rowMSSV = findViewById(R.id.rowMSSV);
        rowFaculty = findViewById(R.id.rowFaculty);
        rowClass = findViewById(R.id.rowClass);
        rowEmail = findViewById(R.id.rowEmail);
        rowPhone = findViewById(R.id.rowPhone);
        rowAddress = findViewById(R.id.rowAddress);

        initRow(rowMSSV, "MSSV");
        initRow(rowFaculty, "Khoa");
        initRow(rowClass, "Lớp");
        initRow(rowEmail, "Email");
        initRow(rowPhone, "Số điện thoại");
        initRow(rowAddress, "Địa chỉ");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());

        loadProfile();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("saved_name", etFullName.getText().toString());
        outState.putString("saved_email", getRowValue(rowEmail));
        outState.putString("saved_phone", getRowValue(rowPhone));
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        etFullName.setText(savedInstanceState.getString("saved_name"));
        setRowValue(rowEmail, savedInstanceState.getString("saved_email"));
        setRowValue(rowPhone, savedInstanceState.getString("saved_phone"));
    }

    private void initRow(View row, String label) {
        TextView tvLabel = row.findViewById(R.id.txtLabel);
        tvLabel.setText(label);
    }

    private String getRowValue(View row) {
        EditText et = row.findViewById(R.id.etValue);
        return et.getText().toString().trim();
    }

    private void setRowValue(View row, String value) {
        EditText et = row.findViewById(R.id.etValue);
        et.setText(value != null ? value : "");
    }

    private void loadProfile() {
        DataManager.getInstance().getUserProfile(userId, new DataManager.ProfileCallback() {
            @Override
            public void onSuccess(DataManager.UserProfile profile) {
                runOnUiThread(() -> {
                    etFullName.setText(profile.fullName);
                    setRowValue(rowMSSV, profile.mssv);
                    setRowValue(rowFaculty, profile.faculty);
                    setRowValue(rowClass, profile.className);
                    setRowValue(rowEmail, profile.email);
                    setRowValue(rowPhone, profile.phone);
                    setRowValue(rowAddress, profile.address);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(StudentInfoActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveProfile() {
        DataManager.UserProfile p = new DataManager.UserProfile();
        p.id = userId;
        p.fullName = etFullName.getText().toString().trim();
        p.mssv = getRowValue(rowMSSV);
        p.faculty = getRowValue(rowFaculty);
        p.className = getRowValue(rowClass);
        p.email = getRowValue(rowEmail);
        p.phone = getRowValue(rowPhone);
        p.address = getRowValue(rowAddress);

        DataManager.getInstance().updateUserProfile(p, new DataManager.ProfileCallback() {
            @Override
            public void onSuccess(DataManager.UserProfile profile) {
                Log.d("StudentInfo", "Update success for user: " + profile.id);
                runOnUiThread(() -> {
                    Toast.makeText(StudentInfoActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                    getSharedPreferences("UserPrefs", MODE_PRIVATE).edit()
                            .putString("fullName", p.fullName)
                            .putString("mssv", p.mssv)
                            .apply();
                });
            }

            @Override
            public void onError(String error) {
                Log.e("StudentInfo", "Update error: " + error);
                runOnUiThread(() -> Toast.makeText(StudentInfoActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
