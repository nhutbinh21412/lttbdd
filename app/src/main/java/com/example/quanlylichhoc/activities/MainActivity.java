package com.example.quanlylichhoc.activities;

import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.database.*;
import com.example.quanlylichhoc.models.*;
import com.example.quanlylichhoc.adapters.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo DataManager
        DataManager.init(this);

        // Ánh xạ các view
        TextView txtGreeting = findViewById(R.id.txtGreeting);
        com.google.android.material.imageview.ShapeableImageView imgProfile = findViewById(R.id.imgProfile);
        
        LinearLayout btnSchedule = findViewById(R.id.btnSchedule);
        LinearLayout btnAddSubject = findViewById(R.id.btnAddSubject);
        LinearLayout btnSearch = findViewById(R.id.btnSearch);
        LinearLayout btnRegisterSubject = findViewById(R.id.btnRegisterSubject);
        LinearLayout btnAdminDashboard = findViewById(R.id.btnAdminDashboard);
        TextView txtScheduleLabel = findViewById(R.id.txtScheduleLabel);
        
        CardView cardToday = findViewById(R.id.cardToday);
        TextView txtTodayStatus = findViewById(R.id.txtTodayStatus);
        
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Đặt ảnh mặc định là hình user
        imgProfile.setImageResource(R.drawable.ic_user_profile);

        // Lấy thông tin người dùng
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("fullName", "User");
        String userRole = prefs.getString("role", "Student");

        txtGreeting.setText("Xin chào, " + fullName);

        // Phân quyền
        if (!(userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer"))) {
            btnAddSubject.setVisibility(View.GONE);
        } else {
            btnRegisterSubject.setVisibility(View.GONE);
            txtScheduleLabel.setText("Lịch dạy");
            bottomNav.getMenu().findItem(R.id.nav_schedule).setTitle("Lịch dạy");
        }

        if (userRole.equalsIgnoreCase("Admin")) {
            btnAdminDashboard.setVisibility(View.VISIBLE);
            btnAdminDashboard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, UserManagementActivity.class)));
        }

        // Tải lịch học hôm nay
        loadTodaySchedule(txtTodayStatus, cardToday);
        
        // Thiết lập phần Tin tức trực tiếp
        setupNewsList();

        // Xử lý sự kiện click
        btnSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
            String title = (userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer")) 
                    ? "Lịch dạy của tôi" : "Lịch học của tôi";
            intent.putExtra("TITLE", title);
            startActivity(intent);
        });

        btnAddSubject.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AddSubjectActivity.class)));
        btnSearch.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SearchActivity.class)));
        btnRegisterSubject.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SubjectRegistrationActivity.class)));

        // Xử lý Bottom Navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) return true;
            else if (itemId == R.id.nav_schedule) {
                Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
                intent.putExtra("TITLE", (userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer")) ? "Lịch dạy" : "Lịch học");
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_attendance) {
                Intent intent = new Intent(MainActivity.this, SubjectListActivity.class);
                intent.putExtra("TITLE", "Điểm danh");
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void setupNewsList() {
        RecyclerView rvNews = findViewById(R.id.rvMainNews);
        rvNews.setLayoutManager(new LinearLayoutManager(this));

        List<News> newsList = new ArrayList<>();
        newsList.add(new News("1", "Thông báo nghỉ học ngày 15/06", "Tất cả sinh viên được nghỉ học vào ngày 15/06 do nhà trường bảo trì hệ thống điện.", "14/06/2024"));
        newsList.add(new News("2", "Kế hoạch thi học kỳ 2", "Thời gian thi học kỳ 2 sẽ bắt đầu từ ngày 01/07/2024. Sinh viên lưu ý theo dõi lịch thi trên trang web trường.", "12/06/2024"));
        newsList.add(new News("3", "Lễ tốt nghiệp năm 2024", "Buổi lễ tốt nghiệp sẽ được tổ chức long trọng vào sáng Thứ 7 tuần tới tại hội trường A.", "10/06/2024"));

        NewsAdapter adapter = new NewsAdapter(newsList, news -> {
            startActivity(new Intent(MainActivity.this, NewsActivity.class));
        });
        rvNews.setAdapter(adapter);
    }

    private void loadTodaySchedule(TextView txtStatus, CardView cardToday) {
        DataManager.getInstance().loadData(new DataManager.DataCallback() {
            @Override
            public void onDataLoaded(List<Subject> subjects) {
                runOnUiThread(() -> {
                    String todayDayOfWeek = getTodayDayOfWeek();
                    Subject todaySubject = null;
                    for (Subject s : subjects) {
                        if (s.getDayOfWeek().equalsIgnoreCase(todayDayOfWeek)) {
                            todaySubject = s;
                            break;
                        }
                    }

                    if (todaySubject != null) {
                        txtStatus.setText(todaySubject.getName() + " - " + todaySubject.getTime());
                        txtStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.primaryColor));
                        final Subject finalSubject = todaySubject;
                        cardToday.setOnClickListener(v -> {
                            Intent intent = new Intent(MainActivity.this, SubjectDetailActivity.class);
                            intent.putExtra("SUBJECT_DATA", finalSubject);
                            startActivity(intent);
                        });
                    } else {
                        txtStatus.setText("Hôm nay không có lịch học");
                        txtStatus.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.textGray));
                        cardToday.setOnClickListener(null);
                    }
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> txtStatus.setText("Lỗi khi tải lịch: " + error));
            }
        });
    }

    private String getTodayDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.MONDAY: return "Thứ 2";
            case Calendar.TUESDAY: return "Thứ 3";
            case Calendar.WEDNESDAY: return "Thứ 4";
            case Calendar.THURSDAY: return "Thứ 5";
            case Calendar.FRIDAY: return "Thứ 6";
            case Calendar.SATURDAY: return "Thứ 7";
            case Calendar.SUNDAY: return "Chủ nhật";
            default: return "";
        }
    }
}
