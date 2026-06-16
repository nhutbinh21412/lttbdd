package com.example.quanlylichhoc.activities;

import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubjectListActivity extends AppCompatActivity {

    private SubjectAdapter adapter;
    private List<Subject> fullSubjectList = new ArrayList<>();
    private List<Subject> displayedSubjectList = new ArrayList<>();
    private Calendar currentWeekMonday;
    private TextView txtWeekRange;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
    private SimpleDateFormat dbSdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private String userRole = "Student";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra("TITLE");
        if (title != null && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtWeekRange = findViewById(R.id.txtWeekRange);
        ImageButton btnPrev = findViewById(R.id.btnPrevWeek);
        ImageButton btnNext = findViewById(R.id.btnNextWeek);

        currentWeekMonday = Calendar.getInstance();
        currentWeekMonday.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        btnPrev.setOnClickListener(v -> { currentWeekMonday.add(Calendar.WEEK_OF_YEAR, -1); updateUI(); });
        btnNext.setOnClickListener(v -> { currentWeekMonday.add(Calendar.WEEK_OF_YEAR, 1); updateUI(); });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewSubjects);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubjectAdapter(displayedSubjectList, subject -> {
            Intent intent = new Intent(SubjectListActivity.this, SubjectDetailActivity.class);
            intent.putExtra("SUBJECT_DATA", subject);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.fabAdd).setOnClickListener(v -> startActivity(new Intent(this, AddSubjectActivity.class)));
        
        // Ẩn nút thêm môn học nếu là Sinh viên
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        if ("Student".equals(prefs.getString("role", "Student"))) {
            findViewById(R.id.fabAdd).setVisibility(android.view.View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRole = prefs.getString("role", "Student");
        int userId = prefs.getInt("userId", -1);

        DataManager.getInstance().loadDataForUser(userId, userRole, new DataManager.DataCallback() {
            @Override
            public void onDataLoaded(List<Subject> subjects) {
                runOnUiThread(() -> {
                    fullSubjectList.clear();
                    fullSubjectList.addAll(subjects);
                    updateUI();
                });
            }
            @Override
            public void onError(String error) {}
        });
    }

    private void updateUI() {
        Calendar weekEnd = (Calendar) currentWeekMonday.clone();
        weekEnd.add(Calendar.DAY_OF_YEAR, 6);
        txtWeekRange.setText("Tuần: " + sdf.format(currentWeekMonday.getTime()) + " - " + sdf.format(weekEnd.getTime()));

        displayedSubjectList.clear();
        for (Subject s : fullSubjectList) {
            if (isSubjectInWeek(s, currentWeekMonday, weekEnd)) {
                displayedSubjectList.add(s);
            }
        }

        Collections.sort(displayedSubjectList, (s1, s2) -> {
            int dayCompare = Integer.compare(getDayValue(s1.getDayOfWeek()), getDayValue(s2.getDayOfWeek()));
            if (dayCompare != 0) return dayCompare;
            
            // Sắp xếp theo tiết bắt đầu (ví dụ: "1-4" -> 1, "10-14" -> 10)
            try {
                int l1 = Integer.parseInt(s1.getLesson().split("-")[0]);
                int l2 = Integer.parseInt(s2.getLesson().split("-")[0]);
                return Integer.compare(l1, l2);
            } catch (Exception e) {
                return s1.getLesson().compareTo(s2.getLesson());
            }
        });

        // Kiểm tra xem có môn nào bị trùng trong tuần này không để thông báo
        checkAndWarnConflicts(displayedSubjectList);
        
        adapter.notifyDataSetChanged();
    }

    private void checkAndWarnConflicts(List<Subject> list) {
        java.util.Set<String> conflicts = new java.util.HashSet<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = i + 1; j < list.size(); j++) {
                Subject s1 = list.get(i);
                Subject s2 = list.get(j);
                
                // Kiểm tra cùng ngày và cùng tiết
                if (s1.getDayOfWeek().equals(s2.getDayOfWeek()) && s1.getLesson().equals(s2.getLesson())) {
                    boolean isConflict = false;
                    
                    if ("Admin".equals(userRole)) {
                        // Admin: Chỉ coi là trùng nếu cùng phòng HOẶC cùng giảng viên
                        if (s1.getRoom().equals(s2.getRoom()) || s1.getTeacherId() == s2.getTeacherId()) {
                            isConflict = true;
                        }
                    } else {
                        // Sinh viên/Giảng viên: Vì danh sách đã được lọc theo user, 
                        // nên hễ trùng thời gian là trùng lịch của chính họ.
                        isConflict = true;
                    }

                    if (isConflict) {
                        conflicts.add(s1.getId());
                        conflicts.add(s2.getId());
                    }
                }
            }
        }
        if (adapter != null) {
            adapter.setConflictIds(conflicts);
            if (!conflicts.isEmpty()) {
                android.widget.Toast.makeText(this, "Phát hiện " + (conflicts.size()/2) + " cặp môn học bị trùng lịch!", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isSubjectInWeek(Subject s, Calendar weekStart, Calendar weekEnd) {
        try {
            Date subStart = dbSdf.parse(s.getStartDate());
            Date subEnd = dbSdf.parse(s.getEndDate());
            Date wStart = weekStart.getTime();
            Date wEnd = weekEnd.getTime();

            // Cắt giờ để so sánh chính xác ngày
            wStart = resetTime(wStart);
            wEnd = resetTime(wEnd);
            subStart = resetTime(subStart);
            subEnd = resetTime(subEnd);

            return !(subStart.after(wEnd) || subEnd.before(wStart));
        } catch (Exception e) {
            return true; // Nếu lỗi date thì hiện cho chắc
        }
    }

    private Date resetTime(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private int getDayValue(String day) {
        if (day == null) return 8;
        switch (day) {
            case "Thứ 2": return 1;
            case "Thứ 3": return 2;
            case "Thứ 4": return 3;
            case "Thứ 5": return 4;
            case "Thứ 6": return 5;
            case "Thứ 7": return 6;
            case "Chủ nhật": return 7;
            default: return 8;
        }
    }
}
