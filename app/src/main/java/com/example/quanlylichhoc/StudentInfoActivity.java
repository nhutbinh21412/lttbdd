package com.example.quanlylichhoc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StudentInfoActivity extends AppCompatActivity {

    private EditText etFullName;
    private View rowStatus, rowMSSV, rowFaculty, rowClass, rowEduLevel, rowTrainType, rowCourse, rowMajor, rowSpecialization;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_info);

        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        etFullName = findViewById(R.id.etFullName);
        rowStatus = findViewById(R.id.rowStatus);
        rowMSSV = findViewById(R.id.rowMSSV);
        rowFaculty = findViewById(R.id.rowFaculty);
        rowClass = findViewById(R.id.rowClass);
        rowEduLevel = findViewById(R.id.rowEduLevel);
        rowTrainType = findViewById(R.id.rowTrainType);
        rowCourse = findViewById(R.id.rowCourse);
        rowMajor = findViewById(R.id.rowMajor);
        rowSpecialization = findViewById(R.id.rowSpecialization);

        initRow(rowStatus, "Trạng thái");
        initRow(rowMSSV, "MSSV");
        initRow(rowFaculty, "Khoa");
        initRow(rowClass, "Lớp");
        initRow(rowEduLevel, "Bậc đào tạo");
        initRow(rowTrainType, "Loại hình đào tạo");
        initRow(rowCourse, "Khóa học");
        initRow(rowMajor, "Ngành");
        initRow(rowSpecialization, "Chuyên ngành");

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSave).setOnClickListener(v -> saveProfile());

        loadProfile();
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
                    setRowValue(rowStatus, profile.status);
                    setRowValue(rowMSSV, profile.mssv);
                    setRowValue(rowFaculty, profile.faculty);
                    setRowValue(rowClass, profile.className);
                    setRowValue(rowEduLevel, profile.eduLevel);
                    setRowValue(rowTrainType, profile.trainType);
                    setRowValue(rowCourse, profile.courseYear);
                    setRowValue(rowMajor, profile.major);
                    setRowValue(rowSpecialization, profile.specialization);
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
        p.status = getRowValue(rowStatus);
        p.mssv = getRowValue(rowMSSV);
        p.faculty = getRowValue(rowFaculty);
        p.className = getRowValue(rowClass);
        p.eduLevel = getRowValue(rowEduLevel);
        p.trainType = getRowValue(rowTrainType);
        p.courseYear = getRowValue(rowCourse);
        p.major = getRowValue(rowMajor);
        p.specialization = getRowValue(rowSpecialization);

        DataManager.getInstance().updateUserProfile(p, new DataManager.ProfileCallback() {
            @Override
            public void onSuccess(DataManager.UserProfile profile) {
                Log.d("StudentInfo", "Update success for user: " + profile.id);
                runOnUiThread(() -> {
                    Toast.makeText(StudentInfoActivity.this, "Đã lưu thông tin!", Toast.LENGTH_SHORT).show();
                    // Cập nhật lại FullName và MSSV trong SharedPreferences để các màn hình khác hiển thị đúng
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