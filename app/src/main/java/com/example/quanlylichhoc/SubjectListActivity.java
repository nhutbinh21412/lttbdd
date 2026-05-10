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
        // Làm mới danh sách khi quay lại từ AddSubjectActivity
        adapter.notifyDataSetChanged();
    }
}