package com.example.quanlylichhoc;

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

    private EditText editName, editClassCode, editLesson, editTime, editRoom, editTeacher;
    private Spinner spinnerDay, spinnerType;
    private Subject existingSubject;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        // Ánh xạ
        TextView txtTitle = findViewById(R.id.txtAddTitle);
        editName = findViewById(R.id.editName);
        editClassCode = findViewById(R.id.editClassCode);
        editLesson = findViewById(R.id.editLesson);
        editTime = findViewById(R.id.editTime);
        editRoom = findViewById(R.id.editRoom);
        editTeacher = findViewById(R.id.editTeacher);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerType = findViewById(R.id.spinnerType);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        // Thiết lập Spinner Thứ
        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(dayAdapter);

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
            editLesson.setText(existingSubject.getLesson());
            editTime.setText(existingSubject.getTime());
            editRoom.setText(existingSubject.getRoom());
            editTeacher.setText(existingSubject.getTeacher());

            // Chọn đúng Thứ trong Spinner
            for (int i = 0; i < days.length; i++) {
                if (days[i].equals(existingSubject.getDayOfWeek())) {
                    spinnerDay.setSelection(i);
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
                        DataManager.getInstance().deleteSubject(existingSubject.getId());
                        Toast.makeText(this, "Đã xóa môn học!", Toast.LENGTH_SHORT).show();
                        
                        // Quay lại màn hình danh sách (bỏ qua màn hình chi tiết vì môn đó đã bị xóa)
                        Intent intent = new Intent(this, SubjectListActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String classCode = editClassCode.getText().toString().trim();
            String lesson = editLesson.getText().toString().trim();
            String time = editTime.getText().toString().trim();
            String room = editRoom.getText().toString().trim();
            String teacher = editTeacher.getText().toString().trim();
            String day = spinnerDay.getSelectedItem().toString();
            String type = spinnerType.getSelectedItem().toString();

            if (name.isEmpty() || classCode.isEmpty() || lesson.isEmpty() || time.isEmpty() || room.isEmpty()) {
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
                        String id = isEditMode ? existingSubject.getId() : String.valueOf(System.currentTimeMillis()).substring(7);
                        Subject subject = new Subject(id, name, room, time, teacher, day, lesson, classCode, color);
                        
                        if (isEditMode) {
                            DataManager.getInstance().updateSubject(subject);
                            Toast.makeText(this, "Đã cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            DataManager.getInstance().addSubject(subject);
                            Toast.makeText(this, "Đã thêm môn học thành công!", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}