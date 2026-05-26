package com.example.quanlylichhoc.activities;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText editName, editClassCode, editRoom;
    private TextView txtTeacherName;
    private Spinner spinnerDay, spinnerType, spinnerLesson, spinnerTime;
    private Subject existingSubject;
    private boolean isEditMode = false;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        // Ánh xạ
        TextView txtTitle = findViewById(R.id.txtAddTitle);
        editName = findViewById(R.id.editName);
        editClassCode = findViewById(R.id.editClassCode);
        spinnerLesson = findViewById(R.id.spinnerLesson);
        spinnerTime = findViewById(R.id.spinnerTime);
        editRoom = findViewById(R.id.editRoom);
        txtTeacherName = findViewById(R.id.txtTeacherName);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerType = findViewById(R.id.spinnerType);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Kiểm tra quyền
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("role", "Student");
        currentUserName = prefs.getString("fullName", "Unknown");
        txtTeacherName.setText("Giảng viên: " + currentUserName);

        if (userRole.equals("Student")) {
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            // Vô hiệu hóa các ô nhập liệu
            editName.setEnabled(false);
            editClassCode.setEnabled(false);
            spinnerLesson.setEnabled(false);
            spinnerTime.setEnabled(false);
            editRoom.setEnabled(false);
            spinnerDay.setEnabled(false);
            spinnerType.setEnabled(false);
        }

        // Thiết lập Spinner Thứ
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

        // Thiết lập Spinner Tiết học
        String[] lessons = {"1-4", "5-9", "10-14"};
        ArrayAdapter<String> lessonAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lessons);
        lessonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLesson.setAdapter(lessonAdapter);

        // Thiết lập Spinner Giờ học
        String[] times = {"7:00-10:35", "12:00-15:35", "16:25-20:00"};
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);

        // Thiết lập Spinner Loại lịch
        String[] types = {"Lý thuyết", "Thực hành", "Trực tuyến", "Thi"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(typeAdapter);

        // Kiểm tra xem có phải chế độ chỉnh sửa không
        existingSubject = (Subject) getIntent().getSerializableExtra("EDIT_SUBJECT");
        if (existingSubject != null) {
            isEditMode = true;
            if (txtTitle != null) txtTitle.setText("Chỉnh sửa môn học");
            btnSave.setText("Cập nhật");
            btnDelete.setVisibility(View.VISIBLE); // Hiện nút xóa khi ở chế độ sửa

            // Đổ dữ liệu cũ vào các ô nhập
            editName.setText(existingSubject.getName());
            editClassCode.setText(existingSubject.getClassCode());
            editRoom.setText(existingSubject.getRoom());
            txtTeacherName.setText("Giảng viên: " + existingSubject.getTeacher());

            // Chọn đúng Thứ trong Spinner
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(existingSubject.getDayOfWeek())) {
                    spinnerDay.setSelection(i);
                    break;
                }
            }

            // Chọn đúng Tiết học
            for (int i = 0; i < lessons.length; i++) {
                if (lessons[i].equals(existingSubject.getLesson())) {
                    spinnerLesson.setSelection(i);
                    break;
                }
            }

            // Chọn đúng Giờ học
            for (int i = 0; i < times.length; i++) {
                if (times[i].equals(existingSubject.getTime())) {
                    spinnerTime.setSelection(i);
                    break;
                }
            }

            // Chọn đúng Loại lịch dựa trên màu sắc
            int color = existingSubject.getColor();
            if (color == Color.parseColor("#8BC34A")) spinnerType.setSelection(1); // Thực hành
            else if (color == Color.parseColor("#2196F3")) spinnerType.setSelection(2); // Trực tuyến
            else if (color == Color.parseColor("#FFF176")) spinnerType.setSelection(3); // Thi
            else spinnerType.setSelection(0); // Lý thuyết
        }

        // Xử lý nút Xóa
        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa môn học này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DataManager.getInstance().deleteSubject(existingSubject.getId(), new DataManager.SimpleCallback() {
                            @Override
                            public void onSuccess() {
                                runOnUiThread(() -> {
                                    Toast.makeText(AddSubjectActivity.this, "Đã xóa môn học!", Toast.LENGTH_SHORT).show();
                                    // Quay lại màn hình danh sách
                                    Intent intent = new Intent(AddSubjectActivity.this, SubjectListActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(intent);
                                    finish();
                                });
                            }

                            @Override
                            public void onError(String error) {
                                runOnUiThread(() -> Toast.makeText(AddSubjectActivity.this, "Lỗi khi xóa: " + error, Toast.LENGTH_SHORT).show());
                            }
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String classCode = editClassCode.getText().toString().trim();
            String lesson = spinnerLesson.getSelectedItem().toString();
            String time = spinnerTime.getSelectedItem().toString();
            String room = editRoom.getText().toString().trim();
            String teacher = isEditMode ? existingSubject.getTeacher() : currentUserName;
            String day = spinnerDay.getSelectedItem().toString();
            String type = spinnerType.getSelectedItem().toString();

            if (name.isEmpty() || classCode.isEmpty() || room.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            int color;
            switch (type) {
                case "Thực hành": color = Color.parseColor("#8BC34A"); break;
                case "Trực tuyến": color = Color.parseColor("#2196F3"); break;
                case "Thi": color = Color.parseColor("#FFF176"); break;
                default: color = Color.parseColor("#E0E0E0"); break;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage(isEditMode ? "Cập nhật thay đổi?" : "Lưu môn học này?")
                    .setPositiveButton("Đồng ý", (dialog, which) -> {
                        btnSave.setEnabled(false); // Vô hiệu hóa nút để tránh nhấn đúp
                        
                        String id;
                        if (isEditMode) {
                            id = existingSubject.getId();
                        } else {
                            // Tạo ID ngẫu nhiên 8 chữ số để tránh trùng lặp
                            id = String.valueOf((int) (Math.random() * 90000000) + 10000000);
                        }
                        
                        // Lấy ID người dùng hiện tại (Giảng viên)
                        int currentUserId = prefs.getInt("userId", -1);
                        
                        Subject subject = new Subject(id, name, room, time, teacher, currentUserId, day, lesson, classCode, color);
                        
                        if (isEditMode) {
                            DataManager.getInstance().updateSubject(subject, new DataManager.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddSubjectActivity.this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        btnSave.setEnabled(true);
                                        Toast.makeText(AddSubjectActivity.this, "Lỗi cập nhật: " + error, Toast.LENGTH_LONG).show();
                                    });
                                }
                            });
                        } else {
                            DataManager.getInstance().addSubject(subject, new DataManager.SimpleCallback() {
                                @Override
                                public void onSuccess() {
                                    runOnUiThread(() -> {
                                        Toast.makeText(AddSubjectActivity.this, "Đã thêm môn học thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        btnSave.setEnabled(true);
                                        Toast.makeText(AddSubjectActivity.this, "Lỗi thêm mới: " + error, Toast.LENGTH_LONG).show();
                                    });
                                }
                            });
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}
