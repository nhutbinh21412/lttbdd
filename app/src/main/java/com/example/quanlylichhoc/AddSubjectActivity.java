package com.example.quanlylichhoc;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText editName, editRoom, editTime, editTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        // Ánh xạ tất cả các trường nhập liệu
        editName    = findViewById(R.id.editName);
        editRoom    = findViewById(R.id.editRoom);
        editTime    = findViewById(R.id.editTime);
        editTeacher = findViewById(R.id.editTeacher);
        Button btnSave = findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String name    = editName.getText().toString().trim();
            String room    = editRoom.getText().toString().trim();
            String time    = editTime.getText().toString().trim();
            String teacher = editTeacher.getText().toString().trim();

            // Kiểm tra các trường bắt buộc
            if (name.isEmpty()) {
                editName.setError("Vui lòng nhập tên môn học!");
                editName.requestFocus();
                return;
            }
            if (room.isEmpty()) {
                editRoom.setError("Vui lòng nhập phòng học!");
                editRoom.requestFocus();
                return;
            }
            if (time.isEmpty()) {
                editTime.setError("Vui lòng nhập thời gian!");
                editTime.requestFocus();
                return;
            }
            if (teacher.isEmpty()) {
                editTeacher.setError("Vui lòng nhập tên giảng viên!");
                editTeacher.requestFocus();
                return;
            }

            // Hiển thị Dialog xác nhận
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có muốn lưu môn học \"" + name + "\" không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        String id = DataManager.getInstance().generateId();
                        Subject newSubject = new Subject(id, name, room, time, teacher);
                        DataManager.getInstance().addSubject(newSubject);
                        Toast.makeText(this, "Đã lưu môn: " + name, Toast.LENGTH_SHORT).show();
                        finish(); // Trả về SubjectListActivity
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }
}