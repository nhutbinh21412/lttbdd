package com.example.quanlylichhoc.activities;

import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.app.DatePickerDialog;
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
import androidx.lifecycle.ViewModelProvider;
import com.example.quanlylichhoc.storage.AddSubjectViewModel;
import com.example.quanlylichhoc.storage.SharedPrefsManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddSubjectActivity extends AppCompatActivity {

    private EditText editName, editClassCode, editRoom, editStartDate, editEndDate;
    private TextView txtTeacherName;
    private Spinner spinnerDay, spinnerType, spinnerLesson, spinnerTime, spinnerCredits;
    private Subject existingSubject;
    private boolean isEditMode = false;
    private String currentUserName;
    private AddSubjectViewModel viewModel;
    private SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_subject);

        viewModel = new ViewModelProvider(this).get(AddSubjectViewModel.class);

        editName = findViewById(R.id.editName);
        editClassCode = findViewById(R.id.editClassCode);
        spinnerLesson = findViewById(R.id.spinnerLesson);
        spinnerTime = findViewById(R.id.spinnerTime);
        editRoom = findViewById(R.id.editRoom);
        txtTeacherName = findViewById(R.id.txtTeacherName);
        spinnerDay = findViewById(R.id.spinnerDay);
        spinnerType = findViewById(R.id.spinnerType);
        spinnerCredits = findViewById(R.id.spinnerCredits);
        editStartDate = findViewById(R.id.editStartDate);
        editEndDate = findViewById(R.id.editEndDate);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnDelete = findViewById(R.id.btnDelete);

        setupDatePickers();

        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String userRole = prefs.getString("role", "Student");
        currentUserName = prefs.getString("fullName", "Unknown");
        txtTeacherName.setText("Giảng viên: " + currentUserName);

        // Thiết lập giá trị mặc định cho ngày
        if (!isEditMode && editStartDate.getText().toString().isEmpty()) {
            Calendar today = Calendar.getInstance();
            editStartDate.setText(dbSdf.format(today.getTime()));
            
            Calendar end = (Calendar) today.clone();
            end.add(Calendar.WEEK_OF_YEAR, 10);
            editEndDate.setText(dbSdf.format(end.getTime()));
        }

        if (userRole.equals("Student")) {
            btnSave.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            editName.setEnabled(false);
            editClassCode.setEnabled(false);
            spinnerLesson.setEnabled(false);
            spinnerTime.setEnabled(false);
            editRoom.setEnabled(false);
            spinnerDay.setEnabled(false);
            spinnerType.setEnabled(false);
            editStartDate.setEnabled(false);
            editEndDate.setEnabled(false);
            spinnerCredits.setEnabled(false);
        }

        String[] days = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        spinnerDay.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days));

        String[] lessons = {"1-4", "5-9", "10-14"};
        spinnerLesson.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, lessons));

        String[] times = {"7:00-10:35", "12:00-15:35", "16:25-20:00"};
        spinnerTime.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, times));

        String[] types = {"Lý thuyết", "Thực hành", "Trực tuyến", "Thi"};
        spinnerType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types));

        Integer[] credits = {1, 2, 3};
        spinnerCredits.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, credits));

        existingSubject = (Subject) getIntent().getSerializableExtra("EDIT_SUBJECT");
        if (existingSubject != null) {
            isEditMode = true;
            btnSave.setText("Cập nhật");
            btnDelete.setVisibility(View.VISIBLE);

            editName.setText(existingSubject.getName());
            editClassCode.setText(existingSubject.getClassCode());
            editRoom.setText(existingSubject.getRoom());
            editStartDate.setText(existingSubject.getStartDate());
            editEndDate.setText(existingSubject.getEndDate());
            txtTeacherName.setText("Giảng viên: " + existingSubject.getTeacher());
            
            for (int i = 0; i < credits.length; i++) if (credits[i] == existingSubject.getCredits()) spinnerCredits.setSelection(i);
            for (int i = 0; i < days.length; i++) if (days[i].equals(existingSubject.getDayOfWeek())) spinnerDay.setSelection(i);
            for (int i = 0; i < lessons.length; i++) if (lessons[i].equals(existingSubject.getLesson())) spinnerLesson.setSelection(i);
            for (int i = 0; i < times.length; i++) if (times[i].equals(existingSubject.getTime())) spinnerTime.setSelection(i);

            int color = existingSubject.getColor();
            if (color == Color.parseColor("#8BC34A")) spinnerType.setSelection(1);
            else if (color == Color.parseColor("#2196F3")) spinnerType.setSelection(2);
            else if (color == Color.parseColor("#FFF176")) spinnerType.setSelection(3);
            else spinnerType.setSelection(0);
        }

        btnSave.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String classCode = editClassCode.getText().toString().trim();
            String lesson = spinnerLesson.getSelectedItem().toString();
            String time = spinnerTime.getSelectedItem().toString();
            String room = editRoom.getText().toString().trim();
            String startDate = editStartDate.getText().toString().trim();
            String endDate = editEndDate.getText().toString().trim();
            int creds = (Integer) spinnerCredits.getSelectedItem();
            String teacher = isEditMode ? existingSubject.getTeacher() : currentUserName;
            String day = spinnerDay.getSelectedItem().toString();
            String type = spinnerType.getSelectedItem().toString();

            if (name.isEmpty() || classCode.isEmpty() || room.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
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

            String id = isEditMode ? existingSubject.getId() : String.valueOf((int) (Math.random() * 90000000) + 10000000);
            int currentUserId = prefs.getInt("userId", -1);
            Subject subject = new Subject(id, name, room, time, teacher, currentUserId, day, lesson, classCode, color, startDate, endDate, creds);

            if (isEditMode) {
                DataManager.getInstance().updateSubject(subject, new DataManager.SimpleCallback() {
                    @Override
                    public void onSuccess() { runOnUiThread(() -> { Toast.makeText(AddSubjectActivity.this, "Đã cập nhật!", Toast.LENGTH_SHORT).show(); finish(); }); }
                    @Override
                    public void onError(String error) { runOnUiThread(() -> Toast.makeText(AddSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show()); }
                });
            } else {
                DataManager.getInstance().addSubject(subject, new DataManager.SimpleCallback() {
                    @Override
                    public void onSuccess() { runOnUiThread(() -> { Toast.makeText(AddSubjectActivity.this, "Đã thêm!", Toast.LENGTH_SHORT).show(); finish(); }); }
                    @Override
                    public void onError(String error) { runOnUiThread(() -> Toast.makeText(AddSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show()); }
                });
            }
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa môn học này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        DataManager.getInstance().deleteSubject(existingSubject.getId(), new DataManager.SimpleCallback() {
                            @Override
                            public void onSuccess() { runOnUiThread(() -> { Toast.makeText(AddSubjectActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show(); finish(); }); }
                            @Override
                            public void onError(String error) { runOnUiThread(() -> Toast.makeText(AddSubjectActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show()); }
                        });
                    }).setNegativeButton("Hủy", null).show();
        });
    }

    private void setupDatePickers() {
        View.OnClickListener dateListener = v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                String date = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                ((EditText)v).setText(date);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        };
        editStartDate.setOnClickListener(dateListener);
        editEndDate.setOnClickListener(dateListener);
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("class_code_bundle", editClassCode.getText().toString());
        viewModel.setSubjectName(editName.getText().toString());
        viewModel.setRoom(editRoom.getText().toString());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isEditMode) SharedPrefsManager.getInstance(this).saveSearchDraft(editName.getText().toString());
    }
}
