package com.example.quanlylichhoc;

import android.content.Context;
import android.graphics.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Subject> subjectList;
    private static Context context;
    private static final String FILE_NAME = "subjects.dat";

    private DataManager() {
        subjectList = new ArrayList<>();
        loadData();
        
        // Nếu file chưa có dữ liệu, thêm dữ liệu mẫu lần đầu
        if (subjectList.isEmpty()) {
            addDefaultSubjects();
            saveData();
        }
    }

    public static void init(Context ctx) {
        context = ctx.getApplicationContext();
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void addDefaultSubjects() {
        int colorLyThuyet = Color.parseColor("#E0E0E0");
        int colorThucHanh = Color.parseColor("#8BC34A");
        int colorThi = Color.parseColor("#FFF176");

        subjectList.add(new Subject("010100085302", "Lập trình Windows", "G502", "07:00 - 10:35", "Hồ Ngọc Thanh", "Thứ 3", "1 - 4", "24DHTT02", colorLyThuyet));
        subjectList.add(new Subject("010100085504", "Lập trình thiết bị di động", "G504", "12:00 - 15:35", "Lê Mạnh Hùng", "Thứ 3", "6 - 9", "24DHTT04", colorThucHanh));
        subjectList.add(new Subject("010100086005", "Quản trị mạng", "G503", "07:00 - 10:35", "Nguyễn Duy Hiếu", "Thứ 4", "1 - 4", "24DHTT05", colorLyThuyet));
        subjectList.add(new Subject("010100086105", "Quản trị dự án CNTT", "G503", "12:00 - 15:35", "Nguyễn Lương Anh Tuấn", "Thứ 5", "6 - 9", "24DHTT05", colorLyThuyet));
        subjectList.add(new Subject("010100085803", "Trí tuệ nhân tạo", "G502", "12:00 - 15:35", "Lê Minh Hưng", "Thứ 6", "6 - 9", "24DHTT03", colorLyThuyet));
    }

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public void addSubject(Subject subject) {
        subjectList.add(subject);
        saveData();
    }

    public void updateSubject(Subject updatedSubject) {
        for (int i = 0; i < subjectList.size(); i++) {
            if (subjectList.get(i).getId().equals(updatedSubject.getId())) {
                subjectList.set(i, updatedSubject);
                saveData();
                return;
            }
        }
    }

    public void deleteSubject(String id) {
        subjectList.removeIf(subject -> subject.getId().equals(id));
        saveData();
    }

    private void saveData() {
        if (context == null) return;
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(subjectList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        if (context == null) return;
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            subjectList = (List<Subject>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // File chưa tồn tại hoặc lỗi đọc file, giữ danh sách trống
            subjectList = new ArrayList<>();
        }
    }

    public String generateId() {
        return String.valueOf(System.currentTimeMillis());
    }
}
