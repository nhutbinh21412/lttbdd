package com.example.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class SubjectListActivity extends AppCompatActivity {

    private SubjectAdapter adapter;
    private List<Subject> subjectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        // Thiết lập Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Lấy dữ liệu từ DataManager (dùng chung toàn app)
        subjectList = DataManager.getInstance().getSubjectList();

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SubjectAdapter(subjectList, subject -> {
            Intent intent = new Intent(SubjectListActivity.this, SubjectDetailActivity.class);
            intent.putExtra("SUBJECT_DATA", subject);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v ->
                startActivity(new Intent(SubjectListActivity.this, AddSubjectActivity.class))
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải dữ liệu từ SQL Server mỗi khi vào màn hình này
        DataManager.getInstance().loadData(new DataManager.DataCallback() {
            @Override
            public void onDataLoaded(List<Subject> subjects) {
                runOnUiThread(() -> {
                    // Lấy thông tin quyền và ID người dùng
                    android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    String role = prefs.getString("role", "Student");
                    int currentUserId = prefs.getInt("userId", -1);

                    subjectList.clear();
                    
                    if (role.equals("Teacher")) {
                        // Nếu là Giảng viên, chỉ hiện môn học do mình quản lý
                        for (Subject s : subjects) {
                            if (s.getTeacherId() == currentUserId) {
                                subjectList.add(s);
                            }
                        }
                    } else {
                        // Nếu là Admin hoặc Sinh viên, hiện tất cả
                        subjectList.addAll(subjects);
                    }

                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                // Có thể hiển thị thông báo lỗi nếu cần
            }
        });
    }
}