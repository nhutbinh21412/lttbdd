package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

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

        Button btnEdit = findViewById(R.id.btnEdit);

        // Kiểm tra an toàn cho nút Back Header
        View btnBackHeader = findViewById(R.id.btnBackHeader);
        if (btnBackHeader != null) {
            btnBackHeader.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }
        
        // Kiểm tra an toàn cho nút Back cũ nếu còn tồn tại
        View btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        }

        // Lấy dữ liệu ban đầu
        Subject subject = (Subject) getIntent().getSerializableExtra("SUBJECT_DATA");
        if (subject != null) {
            subjectId = subject.getId();
            displaySubject(subject);
        }

        

        if (btnEdit != null) {
            btnEdit.setOnClickListener(v -> {
                Subject currentSubject = findSubjectById(subjectId);
                if (currentSubject != null) {
                    Intent intent = new Intent(this, AddSubjectActivity.class);
                    intent.putExtra("EDIT_SUBJECT", currentSubject);
                    startActivity(intent);
                }
            });
        }

        // Nút Điểm danh
        Button btnAttendance = new Button(this);
        btnAttendance.setText("Điểm danh / Xem điểm danh");
        btnAttendance.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4CAF50")));
        btnAttendance.setTextColor(android.graphics.Color.WHITE);
        ((android.widget.LinearLayout)txtName.getParent()).addView(btnAttendance);
        
        btnAttendance.setOnClickListener(v -> {
            Intent intent = new Intent(this, AttendanceActivity.class);
            intent.putExtra("SUBJECT_ID", subjectId);
            startActivity(intent);
        });

        // Phân quyền: Sinh viên có thể xem điểm danh, Giảng viên có thể thực hiện điểm danh
        // Hiện tại nút này được thêm vào layout cho cả hai vai trò.

        // Hiển thị danh sách sinh viên đăng ký nếu là Teacher
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", "Student");
        if (role.equalsIgnoreCase("Teacher") || role.equalsIgnoreCase("Lecturer")) {
            loadRegisteredStudents(subjectId);
        }
    }

    private void loadRegisteredStudents(String subjectId) {
        LinearLayout layout = (LinearLayout) txtName.getParent();

        TextView tvLabel = new TextView(this);
        tvLabel.setText("\nDanh sách sinh viên đăng ký:");
        tvLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        tvLabel.setTextColor(android.graphics.Color.BLACK);
        layout.addView(tvLabel);

        // Truyền thêm số tuần
        DataManager.getInstance().loadStudentsForSubject(subjectId, 1, new DataManager.AttendanceCallback() {
            @Override
            public void onSuccess(List<AttendanceModel> list) {
                runOnUiThread(() -> {
                    if (list.isEmpty()) {
                        TextView tvEmpty = new TextView(SubjectDetailActivity.this);
                        tvEmpty.setText("Chưa có sinh viên đăng ký.");
                        layout.addView(tvEmpty);
                    } else {
                        for (AttendanceModel student : list) {
                            TextView tvStudent = new TextView(SubjectDetailActivity.this);
                            tvStudent.setText("• " + student.getStudentName());
                            tvStudent.setPadding(20, 5, 0, 5);
                            layout.addView(tvStudent);
                        }
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(SubjectDetailActivity.this, "Lỗi tải SV: " + error, Toast.LENGTH_SHORT).show());
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
        txtClassCode.setText("Mã lớp: " + subject.getClassCode());
        txtDay.setText("Thứ: " + subject.getDayOfWeek());
        txtLesson.setText("Tiết học: " + subject.getLesson());
        txtTime.setText("Giờ học: " + subject.getTime());
        txtRoom.setText("Phòng học: " + subject.getRoom());
        txtTeacher.setText("Giảng viên: " + subject.getTeacher());
        
        // Thêm hiển thị tín chỉ và thời gian học
        LinearLayout layout = (LinearLayout) txtName.getParent();
        
        // Xóa thông tin cũ nếu có (tránh lặp khi onResume)
        View oldInfo = layout.findViewWithTag("more_info_tag");
        if (oldInfo != null) layout.removeView(oldInfo);

        TextView tvMoreInfo = new TextView(this);
        tvMoreInfo.setTag("more_info_tag");
        tvMoreInfo.setText(String.format(java.util.Locale.getDefault(),
            "\nSố tín chỉ: %d\nThời gian: %s đến %s",
            subject.getCredits(), subject.getStartDate(), subject.getEndDate()));
        tvMoreInfo.setTextColor(android.graphics.Color.GRAY);
        layout.addView(tvMoreInfo);
    }

    private Subject findSubjectById(String id) {
        for (Subject s : DataManager.getInstance().getSubjectList()) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }
}
