package com.example.quanlylichhoc;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
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

        // Phân quyền: Nếu không phải là Giảng viên (Teacher/Lecturer) thì ẩn nút "Thêm môn"
        if (!(userRole.equalsIgnoreCase("Teacher") || userRole.equalsIgnoreCase("Lecturer"))) {
            btnAddSubject.setVisibility(View.GONE);
        } else {
            // Nếu là Teacher thì ẩn nút "Đăng ký" (chỉ dành cho sinh viên)
            btnRegisterSubject.setVisibility(View.GONE);
        }

        // Tải lịch học hôm nay
        loadTodaySchedule(txtTodayStatus, cardToday);

        // Xử lý sự kiện click cho các chức năng
        btnSchedule.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubjectListActivity.class));
        });

        btnAddSubject.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddSubjectActivity.class));
        });

        btnSearch.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SearchActivity.class));
        });

        btnRegisterSubject.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubjectRegistrationActivity.class));
        });

        // Xử lý Bottom Navigation
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_schedule) {
                startActivity(new Intent(MainActivity.this, SubjectListActivity.class));
                return true;
            } else if (itemId == R.id.nav_attendance) {
                // Giả sử có màn hình chung hoặc danh sách môn để chọn điểm danh
                startActivity(new Intent(MainActivity.this, SubjectListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            }
            return false;
        });
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
                runOnUiThread(() -> {
                    txtStatus.setText("Lỗi khi tải lịch: " + error);
                    Log.e("MainActivity", "Error loading data: " + error);
                });
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
