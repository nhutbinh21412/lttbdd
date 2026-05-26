package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubjectRegistrationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SubjectAdapter adapter;
    private int studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_subject);

        studentId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("userId", -1);

        recyclerView = findViewById(R.id.rvAvailableSubjects);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        loadAvailableSubjects();
    }

    private void loadAvailableSubjects() {
        progressBar.setVisibility(View.VISIBLE);
        DataManager.getInstance().loadData(new DataManager.DataCallback() {
            @Override
            public void onDataLoaded(List<Subject> subjects) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    adapter = new SubjectAdapter(subjects, subject -> {
                        // Khi click vào môn học, hỏi xác nhận đăng ký
                        new androidx.appcompat.app.AlertDialog.Builder(SubjectRegistrationActivity.this)
                                .setTitle("Xác nhận đăng ký")
                                .setMessage("Bạn có muốn đăng ký môn học: " + subject.getName() + "?")
                                .setPositiveButton("Đăng ký", (dialog, which) -> {
                                    register(subject.getId());
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    });
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SubjectRegistrationActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void register(String subjectId) {
        DataManager.getInstance().registerSubject(studentId, subjectId, new DataManager.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> Toast.makeText(SubjectRegistrationActivity.this, "Đăng ký môn học thành công!", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(SubjectRegistrationActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }
}
