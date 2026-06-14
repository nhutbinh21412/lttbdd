package com.example.quanlylichhoc.activities;

import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
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
    private TextView txtEmpty, txtWeekDisplay;
    private Button btnSave;
    private int currentWeek = 1; // Mặc định là tuần 1

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
        txtWeekDisplay = findViewById(R.id.txtWeekDisplay);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSaveAttendance);
        ImageButton btnPrevWeek = findViewById(R.id.btnPrevWeekAtt);
        ImageButton btnNextWeek = findViewById(R.id.btnNextWeekAtt);
        
        // Cập nhật nút Back: Gọi hệ thống quay lại
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Điều hướng tuần
        btnPrevWeek.setOnClickListener(v -> { if (currentWeek > 1) { currentWeek--; updateWeekUI(); } });
        btnNextWeek.setOnClickListener(v -> { if (currentWeek < 15) { currentWeek++; updateWeekUI(); } });

        if (userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer")) {
            btnSave.setVisibility(View.VISIBLE);
            loadStudents();
        } else {
            findViewById(R.id.weekNavAttendance).setVisibility(View.GONE); // Sinh viên chỉ xem lịch sử tổng
            btnSave.setVisibility(View.GONE);
            loadHistory();
        }

        btnSave.setOnClickListener(v -> saveCurrentAttendance());
        
        updateWeekUI();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái điểm danh hiện tại để không phải load lại từ DB khi xoay màn hình
        outState.putInt("current_week_saved", currentWeek);
    }

    private void updateWeekUI() {
        txtWeekDisplay.setText("Tuần học thứ " + currentWeek);
        if (userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer")) {
            loadStudents();
        }
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        txtEmpty.setVisibility(View.GONE);
        DataManager.getInstance().loadStudentsForSubject(subjectId, currentWeek, new DataManager.AttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceModel> list) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    attendanceList = list;
                    adapter = new AttendanceAdapter(attendanceList, true);
                    recyclerView.setAdapter(adapter);
                    if (attendanceList.isEmpty()) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("Không có sinh viên đăng ký môn này");
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

    private void saveCurrentAttendance() {
        if (attendanceList.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        DataManager.getInstance().saveAttendance(subjectId, currentWeek, attendanceList, new DataManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AttendanceActivity.this, "Đã lưu điểm danh tuần " + currentWeek + "!", Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(AttendanceActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
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
                    adapter = new AttendanceAdapter(attendanceList, false);
                    recyclerView.setAdapter(adapter);
                    if (attendanceList.isEmpty()) {
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("Chưa có dữ liệu điểm danh");
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
