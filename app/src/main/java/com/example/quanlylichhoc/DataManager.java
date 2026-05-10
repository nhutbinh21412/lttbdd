package com.example.quanlylichhoc;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton quản lý dữ liệu môn học dùng chung giữa các Activity.
 * Thay bằng SQLite/Room nếu cần lưu trữ lâu dài.
 */
public class DataManager {
    private static DataManager instance;
    private List<Subject> subjectList;

    private DataManager() {
        subjectList = new ArrayList<>();
        // Dữ liệu mẫu ban đầu
        subjectList.add(new Subject("1", "Lập trình Android", "Phòng 302", "Thứ 2 - Tiết 1-3", "Thầy A"));
        subjectList.add(new Subject("2", "Đồ họa máy tính", "Phòng 501", "Thứ 4 - Tiết 7-9", "Cô B"));
        subjectList.add(new Subject("3", "Cơ sở dữ liệu", "Phòng 201", "Thứ 3 - Tiết 4-6", "Thầy C"));
        subjectList.add(new Subject("4", "Mạng máy tính", "Phòng 401", "Thứ 6 - Tiết 1-3", "Cô D"));
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void addSubject(Subject subject) {
        subjectList.add(subject);
    }

    /** Tạo ID tự động tăng */
    public String generateId() {
        return String.valueOf(subjectList.size() + 1);
    }
}