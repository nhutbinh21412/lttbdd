package com.example.quanlylichhoc;

import android.graphics.Color;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Subject> subjectList;

    private DataManager() {
        subjectList = new ArrayList<>();
        // Màu sắc: Lý thuyết (Xám nhẹ), Thực hành (Xanh lá), Thi (Vàng)
        int colorLyThuyet = Color.parseColor("#E0E0E0");
        int colorThucHanh = Color.parseColor("#8BC34A");
        int colorThi = Color.parseColor("#FFF176");

        // phan loai ly thuyet thuc hanh
        subjectList.add(new Subject("010100085302", "Lập trình Windows", "G502", "07:00 - 10:35", "Hồ Ngọc Thanh", "Thứ 3", "1 - 4", "24DHTT02", colorLyThuyet));
        subjectList.add(new Subject("010100085504", "Lập trình thiết bị di động", "G504", "12:00 - 15:35", "Lê Mạnh Hùng", "Thứ 3", "6 - 9", "24DHTT04", colorThucHanh));
        subjectList.add(new Subject("010100086005", "Quản trị mạng", "G503", "07:00 - 10:35", "Nguyễn Duy Hiếu", "Thứ 4", "1 - 4", "24DHTT05", colorLyThuyet));
        subjectList.add(new Subject("010100086105", "Quản trị dự án CNTT", "G503", "12:00 - 15:35", "Nguyễn Lương Anh Tuấn", "Thứ 5", "6 - 9", "24DHTT05", colorLyThuyet));
        subjectList.add(new Subject("010100085803", "Trí tuệ nhân tạo", "G502", "12:00 - 15:35", "Lê Minh Hưng", "Thứ 7", "6 - 9", "24DHTT03", colorLyThuyet));
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

    public void updateSubject(Subject updatedSubject) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getId().equals(updatedSubject.getId())) {
                subjectList.set(i, updatedSubject);
                return;
            }
        }
    }

    public void deleteSubject(String id) {
        subjectList.removeIf(subject -> subject.getId().equals(id));
    }

    public String generateId() {
        return String.valueOf(System.currentTimeMillis()); // ID duy nhất hơn
    }
}