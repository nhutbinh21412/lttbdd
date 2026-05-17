package com.example.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SubjectDetailActivity extends AppCompatActivity {
    
    private TextView txtName, txtClassCode, txtDay, txtLesson, txtTime, txtRoom, txtTeacher;
    private String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);

        txtName = findViewById(R.id.detailName);
        txtClassCode = findViewById(R.id.detailClassCode);
        txtDay = findViewById(R.id.detailDay);
        txtLesson = findViewById(R.id.detailLesson);
        txtTime = findViewById(R.id.detailTime);
        txtRoom = findViewById(R.id.detailRoom);
        txtTeacher = findViewById(R.id.detailTeacher);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnEdit = findViewById(R.id.btnEdit);

        // Lấy dữ liệu ban đầu
        Subject subject = (Subject) getIntent().getSerializableExtra("SUBJECT_DATA");
        if (subject != null) {
            subjectId = subject.getId();
            displaySubject(subject);
        }

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Subject currentSubject = findSubjectById(subjectId);
            if (currentSubject != null) {
                Intent intent = new Intent(this, AddSubjectActivity.class);
                intent.putExtra("EDIT_SUBJECT", currentSubject);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại giao diện khi quay lại từ màn hình chỉnh sửa
        Subject updatedSubject = findSubjectById(subjectId);
        if (updatedSubject != null) {
            displaySubject(updatedSubject);
        }
    }

    private void displaySubject(Subject subject) {
        txtName.setText(subject.getName());
        txtClassCode.setText("Mã lớp: " + subject.getClassCode() + " - " + subject.getId());
        txtDay.setText("Thứ: " + subject.getDayOfWeek());
        txtLesson.setText("Tiết học: " + subject.getLesson());
        txtTime.setText("Giờ học: " + subject.getTime());
        txtRoom.setText("Phòng học: " + subject.getRoom());
        txtTeacher.setText("Giảng viên: " + subject.getTeacher());
    }

    private Subject findSubjectById(String id) {
        for (Subject s : DataManager.getInstance().getSubjectList()) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }
}