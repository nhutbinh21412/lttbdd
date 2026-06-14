package com.example.quanlylichhoc.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.adapters.NewsAdapter;
import com.example.quanlylichhoc.database.DataManager;
import com.example.quanlylichhoc.models.News;
import com.example.quanlylichhoc.storage.NewsViewModel;
import com.example.quanlylichhoc.storage.SharedPrefsManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private RecyclerView rvNews;
    private NewsAdapter adapter;
    private List<News> newsList = new ArrayList<>();
    private String userRole;
    private NewsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        userRole = prefs.getString("role", "Student");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        rvNews = findViewById(R.id.rvNews);
        rvNews.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddNews = findViewById(R.id.fabAddNews);
        if (fabAddNews != null) {
            if ("Admin".equalsIgnoreCase(userRole)) {
                fabAddNews.setVisibility(View.VISIBLE);
                fabAddNews.setOnClickListener(v -> showNewsDialog(null));
            } else {
                fabAddNews.setVisibility(View.GONE);
            }
        }

        loadNewsFromDB();

        // Khôi phục Dialog nếu nó đang mở lúc xoay màn hình
        if (Boolean.TRUE.equals(viewModel.isDialogShowing())) {
            showNewsDialog(viewModel.getEditingNews());
        }
    }

    private void loadNewsFromDB() {
        DataManager.getInstance().getAllNews(new DataManager.NewsCallback() {
            @Override
            public void onSuccess(List<News> list) {
                runOnUiThread(() -> {
                    newsList = list;
                    adapter = new NewsAdapter(newsList, news -> {
                        if ("Admin".equalsIgnoreCase(userRole)) showAdminOptions(news);
                    });
                    rvNews.setAdapter(adapter);
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(NewsActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showAdminOptions(News news) {
        String[] options = {"Chỉnh sửa", "Xóa thông báo"};
        new AlertDialog.Builder(this)
                .setTitle("Tùy chọn quản trị")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showNewsDialog(news);
                    else deleteNewsConfirm(news);
                }).show();
    }

    private void showNewsDialog(News news) {
        viewModel.setEditingNews(news);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_news, null);
        EditText etTitle = view.findViewById(R.id.etNewsTitle);
        EditText etContent = view.findViewById(R.id.etNewsContent);

        // Khôi phục nháp từ ViewModel
        if (viewModel.getDraftTitle() != null) etTitle.setText(viewModel.getDraftTitle());
        if (viewModel.getDraftContent() != null) etContent.setText(viewModel.getDraftContent());

        if (news != null) {
            builder.setTitle("Sửa thông báo");
            if (viewModel.getDraftTitle() == null) etTitle.setText(news.getTitle());
            if (viewModel.getDraftContent() == null) etContent.setText(news.getContent());
        } else {
            builder.setTitle("Thêm thông báo mới");
        }

        // Theo dõi thay đổi để lưu vào ViewModel (Tình huống 1)
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDraftTitle(etTitle.getText().toString());
                viewModel.setDraftContent(etContent.getText().toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        etTitle.addTextChangedListener(watcher);
        etContent.addTextChangedListener(watcher);

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();
            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            
            viewModel.setDraftTitle(null); 
            viewModel.setDraftContent(null);
            viewModel.setEditingNews(null);
            viewModel.setDialogShowing(false);

            News n = new News(news != null ? news.getId() : "0", title, content, "");
            DataManager.SimpleCallback cb = new DataManager.SimpleCallback() {
                @Override public void onSuccess() { runOnUiThread(() -> loadNewsFromDB()); }
                @Override public void onError(String error) {}
            };
            if (news == null) DataManager.getInstance().addNews(n, cb);
            else DataManager.getInstance().updateNews(n, cb);
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> {
            viewModel.setDraftTitle(null);
            viewModel.setDraftContent(null);
            viewModel.setEditingNews(null);
            viewModel.setDialogShowing(false);
        });

        builder.setOnCancelListener(dialog -> {
            viewModel.setEditingNews(null);
            viewModel.setDialogShowing(false);
        });

        builder.show();
        viewModel.setDialogShowing(true);
    }

    private void deleteNewsConfirm(News news) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Xóa thông báo này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DataManager.getInstance().deleteNews(news.getId(), new DataManager.SimpleCallback() {
                        @Override public void onSuccess() { runOnUiThread(() -> loadNewsFromDB()); }
                        @Override public void onError(String error) {}
                    });
                }).setNegativeButton("Hủy", null).show();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu tiêu đề vào Bundle
        if (viewModel.getDraftTitle() != null) {
            outState.putString("news_title_bundle", viewModel.getDraftTitle());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Lưu vào SharedPreferences khi người dùng thoát app đột ngột
        if (viewModel.getDraftTitle() != null) {
            SharedPrefsManager.getInstance(this).saveSearchDraft("Draft News: " + viewModel.getDraftTitle());
        }
    }
}
