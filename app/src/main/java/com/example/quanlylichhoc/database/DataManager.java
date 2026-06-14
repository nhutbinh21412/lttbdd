package com.example.quanlylichhoc.database;
import com.example.quanlylichhoc.models.*;

import android.content.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Subject> subjectList;
    private static Context context;

    private DataManager() {
        subjectList = new ArrayList<>();
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

    public List<Subject> getSubjectList() {
        return subjectList;
    }

    public interface DataCallback {
        void onDataLoaded(List<Subject> subjects);
        void onError(String error);
    }

    public void loadData(DataCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<Subject> newList = new ArrayList<>();
                    String query = "SELECT s.*, u.FullName as TeacherName FROM Subjects s LEFT JOIN Users u ON s.TeacherId = u.Id";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        String start = "";
                        String end = "";
                        int credits = 0;
                        try { start = rs.getString("StartDate"); } catch (Exception ignored) {}
                        try { end = rs.getString("EndDate"); } catch (Exception ignored) {}
                        try { credits = rs.getInt("Credits"); } catch (Exception ignored) {}

                        Subject subject = new Subject(
                                rs.getString("Id"),
                                rs.getString("Name"),
                                rs.getString("Room"),
                                rs.getString("Time"),
                                rs.getString("TeacherName"),
                                rs.getInt("TeacherId"),
                                rs.getString("DayOfWeek"),
                                rs.getString("Session"),
                                rs.getString("ClassCode"),
                                rs.getInt("Color"),
                                start,
                                end,
                                credits
                        );
                        newList.add(subject);
                    }
                    subjectList = newList;
                    if (callback != null) callback.onDataLoaded(subjectList);
                    conn.close();
                } else {
                    if (callback != null) callback.onError("Không thể kết nối database");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    public void addSubject(Subject s, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "INSERT INTO Subjects (Id, Name, Room, Time, TeacherId, DayOfWeek, Session, ClassCode, Color, StartDate, EndDate, Credits) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, s.getId());
                    stmt.setString(2, s.getName());
                    stmt.setString(3, s.getRoom());
                    stmt.setString(4, s.getTime());
                    stmt.setInt(5, s.getTeacherId());
                    stmt.setString(6, s.getDayOfWeek());
                    stmt.setString(7, s.getLesson());
                    stmt.setString(8, s.getClassCode());
                    stmt.setInt(9, s.getColor());
                    stmt.setString(10, s.getStartDate());
                    stmt.setString(11, s.getEndDate());
                    stmt.setInt(12, s.getCredits());
                    stmt.executeUpdate();
                    subjectList.add(s);
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public void updateSubject(Subject s, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE Subjects SET Name=?, Room=?, Time=?, TeacherId=?, DayOfWeek=?, Session=?, ClassCode=?, Color=?, StartDate=?, EndDate=?, Credits=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, s.getName());
                    stmt.setString(2, s.getRoom());
                    stmt.setString(3, s.getTime());
                    stmt.setInt(4, s.getTeacherId());
                    stmt.setString(5, s.getDayOfWeek());
                    stmt.setString(6, s.getLesson());
                    stmt.setString(7, s.getClassCode());
                    stmt.setInt(8, s.getColor());
                    stmt.setString(9, s.getStartDate());
                    stmt.setString(10, s.getEndDate());
                    stmt.setInt(11, s.getCredits());
                    stmt.setString(12, s.getId());
                    stmt.executeUpdate();
                    for (int i = 0; i < subjectList.size(); i++) {
                        if (subjectList.get(i).getId().equals(s.getId())) {
                            subjectList.set(i, s);
                            break;
                        }
                    }
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public void deleteSubject(String id, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "DELETE FROM Subjects WHERE Id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, id);
                    stmt.executeUpdate();
                    subjectList.removeIf(s -> s.getId().equals(id));
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public interface AttendanceCallback {
        void onSuccess(List<AttendanceModel> list);
        void onError(String error);
    }

    public void loadStudentsForSubject(String subjectId, int weekNumber, AttendanceCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<AttendanceModel> list = new ArrayList<>();
                    // Sử dụng DISTINCT hoặc GROUP BY để đảm bảo không bị lặp sinh viên
                    String query = "SELECT u.Id, u.FullName, ISNULL(MAX(a.Status), N'Có mặt') as Status " +
                                 "FROM Users u " +
                                 "JOIN StudentSubjects ss ON u.Id = ss.StudentId " +
                                 "LEFT JOIN Attendance a ON u.Id = a.StudentId AND a.SubjectId = ss.SubjectId " +
                                 "AND a.WeekNumber = ? " +
                                 "WHERE ss.SubjectId = ? " +
                                 "GROUP BY u.Id, u.FullName";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, weekNumber);
                    stmt.setString(2, subjectId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        list.add(new AttendanceModel(rs.getInt("Id"), rs.getString("FullName"), rs.getString("Status"), "Tuần " + weekNumber));
                    }
                    callback.onSuccess(list);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void saveAttendance(String subjectId, int weekNumber, List<AttendanceModel> list, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    // Xóa điểm danh của tuần này trước khi lưu mới (Chống lặp)
                    String deleteQuery = "DELETE FROM Attendance WHERE SubjectId = ? AND WeekNumber = ?";
                    PreparedStatement delStmt = conn.prepareStatement(deleteQuery);
                    delStmt.setString(1, subjectId);
                    delStmt.setInt(2, weekNumber);
                    delStmt.executeUpdate();

                    String query = "INSERT INTO Attendance (SubjectId, StudentId, Status, WeekNumber) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    for (AttendanceModel item : list) {
                        stmt.setString(1, subjectId);
                        stmt.setInt(2, item.getStudentId());
                        stmt.setString(3, item.getStatus());
                        stmt.setInt(4, weekNumber);
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    conn.close();
                    if (callback != null) callback.onSuccess();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public void loadMyAttendance(int studentId, String subjectId, AttendanceCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<AttendanceModel> list = new ArrayList<>();
                    String query = "SELECT AttendanceDate, Status FROM Attendance WHERE StudentId = ? AND SubjectId = ? ORDER BY AttendanceDate DESC";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, studentId);
                    stmt.setString(2, subjectId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        list.add(new AttendanceModel(studentId, "", rs.getString("Status"), rs.getString("AttendanceDate")));
                    }
                    callback.onSuccess(list);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    // --- CÁC HÀM XỬ LÝ PROFILE NGƯỜI DÙNG ---
    public static class UserProfile {
        public int id;
        public String fullName, mssv, faculty, className, email, phone, address;
        public UserProfile() {}
    }

    public interface ProfileCallback {
        void onSuccess(UserProfile profile);
        void onError(String error);
    }

    public void getUserProfile(int userId, ProfileCallback callback) {
        if (userId == -1) {
            callback.onError("User ID không hợp lệ");
            return;
        }
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "SELECT * FROM Users WHERE Id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, userId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        UserProfile profile = new UserProfile();
                        profile.id = rs.getInt("Id");
                        profile.fullName = rs.getString("FullName");
                        profile.mssv = rs.getString("MSSV");
                        profile.faculty = rs.getString("Faculty");
                        profile.className = rs.getString("ClassName");
                        
                        try { profile.email = rs.getString("Email"); } catch (Exception e) { profile.email = ""; }
                        try { profile.phone = rs.getString("Phone"); } catch (Exception e) { profile.phone = ""; }
                        try { profile.address = rs.getString("Address"); } catch (Exception e) { profile.address = ""; }
                        
                        callback.onSuccess(profile);
                    } else {
                        callback.onError("Không tìm thấy thông tin người dùng");
                    }
                    conn.close();
                } else {
                    callback.onError("Không thể kết nối đến máy chủ");
                }
            } catch (Exception e) { 
                e.printStackTrace();
                callback.onError("Lỗi truy vấn: " + e.getMessage()); 
            }
        }).start();
    }

    public void updateUserProfile(UserProfile p, ProfileCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE Users SET FullName=?, MSSV=?, Faculty=?, ClassName=?, Email=?, Phone=?, Address=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, p.fullName);
                    stmt.setString(2, p.mssv);
                    stmt.setString(3, p.faculty);
                    stmt.setString(4, p.className);
                    stmt.setString(5, p.email);
                    stmt.setString(6, p.phone);
                    stmt.setString(7, p.address);
                    stmt.setInt(8, p.id);
                    stmt.executeUpdate();
                    callback.onSuccess(p);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void changePassword(int userId, String currentPassword, String newPassword, ProfileCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String checkQuery = "SELECT * FROM Users WHERE Id = ? AND Password = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setInt(1, userId);
                    checkStmt.setString(2, currentPassword);
                    ResultSet rs = checkStmt.executeQuery();
                    if (rs.next()) {
                        String updateQuery = "UPDATE Users SET Password = ? WHERE Id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newPassword);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        callback.onSuccess(null);
                    } else { callback.onError("Mật khẩu hiện tại không chính xác"); }
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    public void registerSubject(int studentId, String subjectId, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "INSERT INTO StudentSubjects (StudentId, SubjectId) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, studentId);
                    stmt.setString(2, subjectId);
                    stmt.executeUpdate();
                    callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public interface UsersCallback {
        void onSuccess(List<User> users);
        void onError(String error);
    }

    public void getAllUsers(UsersCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<User> users = new ArrayList<>();
                    String query = "SELECT Id, Username, Password, Role, FullName FROM Users";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        users.add(new User(rs.getInt("Id"), rs.getString("Username"), rs.getString("Password"), rs.getString("Role"), rs.getString("FullName")));
                    }
                    callback.onSuccess(users);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void addUser(User u, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "INSERT INTO Users (Username, Password, Role, FullName) VALUES (?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, u.username);
                    stmt.setString(2, u.password);
                    stmt.setString(3, u.role);
                    stmt.setString(4, u.fullName);
                    stmt.executeUpdate();
                    callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void updateUser(User u, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE Users SET Username=?, Password=?, Role=?, FullName=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, u.username);
                    stmt.setString(2, u.password);
                    stmt.setString(3, u.role);
                    stmt.setString(4, u.fullName);
                    stmt.setInt(5, u.id);
                    stmt.executeUpdate();
                    callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void deleteUser(int id, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "DELETE FROM Users WHERE Id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, id);
                    stmt.executeUpdate();
                    callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    // --- CÁC HÀM XỬ LÝ TIN TỨC (NEWS) ---
    public interface NewsCallback {
        void onSuccess(List<News> newsList);
        void onError(String error);
    }

    public void getAllNews(NewsCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<News> list = new ArrayList<>();
                    String query = "SELECT Id, Title, Content, CONVERT(VARCHAR, DatePosted, 103) as DateStr FROM News ORDER BY DatePosted DESC";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        list.add(new News(rs.getString("Id"), rs.getString("Title"), rs.getString("Content"), rs.getString("DateStr")));
                    }
                    callback.onSuccess(list);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void addNews(News n, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "INSERT INTO News (Title, Content) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, n.getTitle());
                    stmt.setString(2, n.getContent());
                    stmt.executeUpdate();
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public void updateNews(News n, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE News SET Title=?, Content=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, n.getTitle());
                    stmt.setString(2, n.getContent());
                    stmt.setInt(3, Integer.parseInt(n.getId()));
                    stmt.executeUpdate();
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }

    public void deleteNews(String id, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "DELETE FROM News WHERE Id = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, Integer.parseInt(id));
                    stmt.executeUpdate();
                    if (callback != null) callback.onSuccess();
                    conn.close();
                }
            } catch (Exception e) { if (callback != null) callback.onError(e.getMessage()); }
        }).start();
    }
}
