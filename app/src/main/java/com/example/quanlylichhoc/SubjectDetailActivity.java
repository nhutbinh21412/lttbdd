package com.example.quanlylichhoc;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SubjectDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        TextView txtName = findViewById(R.id.detailName);
        TextView txtRoom = findViewById(R.id.detailRoom);
        TextView txtTime = findViewById(R.id.detailTime);
        TextView txtTeacher = findViewById(R.id.detailTeacher);
        Button btnBack = findViewById(R.id.btnBack);

        // Nhận object Subject từ Intent
        Subject subject = (Subject) getIntent().getSerializableExtra("SUBJECT_DATA");

        if (subject != null) {
            txtName.setText(subject.getName());
            txtRoom.setText("Phòng học: " + subject.getRoom());
            txtTime.setText("Thời gian: " + subject.getTime());
            txtTeacher.setText("Giảng viên: " + subject.getTeacher());
        }

        btnBack.setOnClickListener(v -> finish());
    }
}