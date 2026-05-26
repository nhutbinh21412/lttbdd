package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class AttendanceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AttendanceAdapter adapter;
    private List<AttendanceModel> attendanceList = new ArrayList<>();
    private String subjectId;
    private String userRole;
    private int userId;
    private ProgressBar progressBar;
    private TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        subjectId = getIntent().getStringExtra("SUBJECT_ID");
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRole = prefs.getString("role", "Student");
        userId = prefs.getInt("userId", -1);

        recyclerView = findViewById(R.id.recyclerViewAttendance);
        progressBar = findViewById(R.id.progressBar);
        txtEmpty = findViewById(R.id.txtEmpty);
        ImageView btnBack = findViewById(R.id.btnBack);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        Button btnSave = findViewById(R.id.btnSaveAttendance);
        
        btnBack.setOnClickListener(v -> finish());
        
        // Kiểm tra đúng vai trò là Giảng viên (Teacher hoặc Lecturer để tương thích)
        if (userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer")) {
            btnSave.setVisibility(View.VISIBLE);
            loadStudents();
        } else {
            btnSave.setVisibility(View.GONE);
            loadHistory();
        }

        btnSave.setOnClickListener(v -> {
            if (attendanceList.isEmpty()) {
                Toast.makeText(this, "Không có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
                return;
            }
            DataManager.getInstance().saveAttendance(subjectId, attendanceList);
            Toast.makeText(this, "Đã lưu điểm danh!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.GONE);
        DataManager.getInstance().loadStudentsForSubject(subjectId, new DataManager.AttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceModel> list) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    attendanceList = list;
                    if (attendanceList.isEmpty()) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("Không có sinh viên nào đăng ký môn học này");
                    } else {
                        adapter = new AttendanceAdapter(attendanceList, true);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AttendanceActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void loadHistory() {
        progressBar.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.GONE);
        DataManager.getInstance().loadMyAttendance(userId, subjectId, new DataManager.AttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceModel> list) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    attendanceList = list;
                    if (attendanceList.isEmpty()) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("Bạn chưa có dữ liệu điểm danh môn này");
                    } else {
                        adapter = new AttendanceAdapter(attendanceList, false);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AttendanceActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
