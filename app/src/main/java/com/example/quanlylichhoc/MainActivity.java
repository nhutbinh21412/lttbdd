package com.example.quanlylichhoc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo các thành phần đúng ID đã khai báo trong XML
        CardView cardViewSchedule = findViewById(R.id.cardViewSchedule);
        CardView cardViewAdd = findViewById(R.id.cardViewAdd);
        CardView cardViewSearch = findViewById(R.id.cardViewSearch);
        Button btnStart = findViewById(R.id.btnStart);

        // Chuyển màn hình bằng Explicit Intent
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubjectListActivity.class));
        });

        cardViewSchedule.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubjectListActivity.class));
        });

        cardViewAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddSubjectActivity.class));
        });

        cardViewSearch.setOnClickListener(v -> {
            // Chuyển sang màn hình tìm kiếm thay vì hiện Toast
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }
}