package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinnerSemester;
    private EditText edtSearchSubject;
    private RecyclerView recycleViewSearchResult;
    private SubjectAdapter adapter;
    private List<Subject> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinnerSemester       = findViewById(R.id.spinnerSemester);
        edtSearchSubject      = findViewById(R.id.edtSearchSubject);
        Button btnSearchAction = findViewById(R.id.btnSearchAction);
        recycleViewSearchResult = findViewById(R.id.recycleViewSearchResult);

        // Spinner học kỳ
        String[] semesters = {"Tất cả", "Học kỳ 1", "Học kỳ 2", "Học kỳ hè"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, semesters);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(spinnerAdapter);

        // RecyclerView kết quả
        searchResults = new ArrayList<>();
        recycleViewSearchResult.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(searchResults, subject -> {
            Intent intent = new Intent(SearchActivity.this, SubjectDetailActivity.class);
            intent.putExtra("SUBJECT_DATA", subject);
            startActivity(intent);
        });
        recycleViewSearchResult.setAdapter(adapter);

        btnSearchAction.setOnClickListener(v -> performSearch());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn làm mới dữ liệu từ SQL để tìm kiếm chính xác
        DataManager.getInstance().loadData(null);
    }

    private void performSearch() {
        String keyword = edtSearchSubject.getText().toString().trim().toLowerCase();
        List<Subject> allSubjects = DataManager.getInstance().getSubjectList();

        searchResults.clear();
        for (Subject subject : allSubjects) {
            boolean matchesKeyword = keyword.isEmpty()
                    || subject.getName().toLowerCase().contains(keyword)
                    || subject.getTeacher().toLowerCase().contains(keyword)
                    || subject.getRoom().toLowerCase().contains(keyword);

            if (matchesKeyword) {
                searchResults.add(subject);
            }
        }

        adapter.notifyDataSetChanged();

        if (searchResults.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy môn học phù hợp.", Toast.LENGTH_SHORT).show();
        }
    }
}
