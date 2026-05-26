package com.example.quanlylichhoc.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quanlylichhoc.R;
import com.example.quanlylichhoc.adapters.UserAdapter;
import com.example.quanlylichhoc.database.DataManager;
import com.example.quanlylichhoc.models.User;
import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        recyclerView = findViewById(R.id.rvUsers);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.fabAddUser).setOnClickListener(v -> showUserDialog(null));

        loadUsers();
    }

    private void loadUsers() {
        progressBar.setVisibility(View.VISIBLE);
        DataManager.getInstance().getAllUsers(new DataManager.UsersCallback() {
            @Override
            public void onSuccess(List<User> users) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    userList = users;
                    adapter = new UserAdapter(userList, new UserAdapter.OnUserActionListener() {
                        @Override
                        public void onEdit(User user) { showUserDialog(user); }
                        @Override
                        public void onDelete(User user) { deleteUser(user); }
                    });
                    recyclerView.setAdapter(adapter);
                });
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UserManagementActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user, null);
        
        EditText etUsername = view.findViewById(R.id.etDialogUsername);
        EditText etPassword = view.findViewById(R.id.etDialogPassword);
        EditText etFullName = view.findViewById(R.id.etDialogFullName);
        Spinner spRole = view.findViewById(R.id.spDialogRole);

        String[] roles = {"Student", "Teacher", "Admin"};
        spRole.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles));

        if (user != null) {
            builder.setTitle("Chỉnh sửa tài khoản");
            etUsername.setText(user.username);
            etPassword.setText(user.password);
            etFullName.setText(user.fullName);
            for (int i=0; i<roles.length; i++) if(roles[i].equals(user.role)) spRole.setSelection(i);
        } else {
            builder.setTitle("Tạo tài khoản mới");
        }

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String uname = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            String fname = etFullName.getText().toString().trim();
            String role = spRole.getSelectedItem().toString();

            if (uname.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Username và Password", Toast.LENGTH_SHORT).show();
                return;
            }

            User u = new User(user != null ? user.id : 0, uname, pass, role, fname);
            if (user == null) {
                DataManager.getInstance().addUser(u, new DataManager.SimpleCallback() {
                    @Override
                    public void onSuccess() { runOnUiThread(() -> loadUsers()); }
                    @Override
                    public void onError(String error) { runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, error, Toast.LENGTH_SHORT).show()); }
                });
            } else {
                DataManager.getInstance().updateUser(u, new DataManager.SimpleCallback() {
                    @Override
                    public void onSuccess() { runOnUiThread(() -> loadUsers()); }
                    @Override
                    public void onError(String error) { runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, error, Toast.LENGTH_SHORT).show()); }
                });
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tài khoản: " + user.username + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    DataManager.getInstance().deleteUser(user.id, new DataManager.SimpleCallback() {
                        @Override
                        public void onSuccess() { runOnUiThread(() -> loadUsers()); }
                        @Override
                        public void onError(String error) { runOnUiThread(() -> Toast.makeText(UserManagementActivity.this, error, Toast.LENGTH_SHORT).show()); }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
