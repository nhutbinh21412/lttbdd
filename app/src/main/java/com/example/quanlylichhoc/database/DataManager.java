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

    // Interface để nhận kết quả khi load xong
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
                        Subject subject = new Subject(
                                rs.getString("Id"),
                                rs.getString("Name"),
                                rs.getString("Room"),
                                rs.getString("Time"),
                                rs.getString("TeacherName"), // Lấy tên giảng viên từ bảng Users
                                rs.getInt("TeacherId"),
                                rs.getString("DayOfWeek"),
                                rs.getString("Session"),
                                rs.getString("ClassCode"),
                                rs.getInt("Color")
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
                    String query = "INSERT INTO Subjects (Id, Name, Room, Time, TeacherId, DayOfWeek, Session, ClassCode, Color) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                    stmt.executeUpdate();
                    
                    // Thêm vào list local để UI update nhanh
                    subjectList.add(s);
                    
                    if (callback != null) callback.onSuccess();
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

    public void updateSubject(Subject s, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE Subjects SET Name=?, Room=?, Time=?, TeacherId=?, DayOfWeek=?, Session=?, ClassCode=?, Color=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, s.getName());
                    stmt.setString(2, s.getRoom());
                    stmt.setString(3, s.getTime());
                    stmt.setInt(4, s.getTeacherId());
                    stmt.setString(5, s.getDayOfWeek());
                    stmt.setString(6, s.getLesson());
                    stmt.setString(7, s.getClassCode());
                    stmt.setInt(8, s.getColor());
                    stmt.setString(9, s.getId());
                    stmt.executeUpdate();
                    
                    // Update list local
                    for (int i = 0; i < subjectList.size(); i++) {
                        if (subjectList.get(i).getId().equals(s.getId())) {
                            subjectList.set(i, s);
                            break;
                        }
                    }
                    
                    if (callback != null) callback.onSuccess();
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
                } else {
                    if (callback != null) callback.onError("Không thể kết nối database");
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) callback.onError(e.getMessage());
            }
        }).start();
    }

    // --- CÁC HÀM XỬ LÝ ĐIỂM DANH ---
    public interface AttendanceCallback {
        void onSuccess(List<AttendanceModel> list);
        void onError(String error);
    }

    public void loadStudentsForSubject(String subjectId, AttendanceCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    List<AttendanceModel> list = new ArrayList<>();
                    // Lấy danh sách sinh viên đăng ký môn học này
                    String query = "SELECT u.Id, u.FullName FROM Users u " +
                                 "JOIN StudentSubjects ss ON u.Id = ss.StudentId " +
                                 "WHERE ss.SubjectId = ?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, subjectId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        list.add(new AttendanceModel(rs.getInt("Id"), rs.getString("FullName"), "Có mặt", ""));
                    }
                    callback.onSuccess(list);
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void saveAttendance(String subjectId, List<AttendanceModel> list) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "INSERT INTO Attendance (SubjectId, StudentId, Status) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    for (AttendanceModel item : list) {
                        stmt.setString(1, subjectId);
                        stmt.setInt(2, item.getStudentId());
                        stmt.setString(3, item.getStatus());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                    conn.close();
                }
            } catch (Exception e) { e.printStackTrace(); }
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
        public String fullName, status, mssv, faculty, className, eduLevel, trainType, courseYear, major, specialization;

        public UserProfile() {}
    }

    public interface ProfileCallback {
        void onSuccess(UserProfile profile);
        void onError(String error);
    }

    public void getUserProfile(int userId, ProfileCallback callback) {
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
                        profile.status = rs.getString("Status");
                        profile.mssv = rs.getString("MSSV");
                        profile.faculty = rs.getString("Faculty");
                        profile.className = rs.getString("ClassName");
                        profile.eduLevel = rs.getString("EducationLevel");
                        profile.trainType = rs.getString("TrainingType");
                        profile.courseYear = rs.getString("CourseYear");
                        profile.major = rs.getString("Major");
                        profile.specialization = rs.getString("Specialization");
                        callback.onSuccess(profile);
                    }
                    conn.close();
                }
            } catch (Exception e) { callback.onError(e.getMessage()); }
        }).start();
    }

    public void updateUserProfile(UserProfile p, ProfileCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    String query = "UPDATE Users SET FullName=?, Status=?, MSSV=?, Faculty=?, ClassName=?, EducationLevel=?, TrainingType=?, CourseYear=?, Major=?, Specialization=? WHERE Id=?";
                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setString(1, p.fullName);
                    stmt.setString(2, p.status);
                    stmt.setString(3, p.mssv);
                    stmt.setString(4, p.faculty);
                    stmt.setString(5, p.className);
                    stmt.setString(6, p.eduLevel);
                    stmt.setString(7, p.trainType);
                    stmt.setString(8, p.courseYear);
                    stmt.setString(9, p.major);
                    stmt.setString(10, p.specialization);
                    stmt.setInt(11, p.id);
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
                    // Kiểm tra mật khẩu cũ
                    String checkQuery = "SELECT * FROM Users WHERE Id = ? AND Password = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                    checkStmt.setInt(1, userId);
                    checkStmt.setString(2, currentPassword);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        // Cập nhật mật khẩu mới
                        String updateQuery = "UPDATE Users SET Password = ? WHERE Id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                        updateStmt.setString(1, newPassword);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        callback.onSuccess(null);
                    } else {
                        callback.onError("Mật khẩu hiện tại không chính xác");
                    }
                    conn.close();
                } else {
                    callback.onError("Không thể kết nối database");
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    // --- ĐĂNG KÝ MÔN HỌC ---
    public interface SimpleCallback {
        void onSuccess();
        void onError(String error);
    }

    public void registerSubject(int studentId, String subjectId, SimpleCallback callback) {
        new Thread(() -> {
            try {
                Connection conn = SQLHelper.getConnection();
                if (conn != null) {
                    // Kiểm tra xem đã đăng ký chưa
                    String check = "SELECT * FROM StudentSubjects WHERE StudentId = ? AND SubjectId = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(check);
                    checkStmt.setInt(1, studentId);
                    checkStmt.setString(2, subjectId);
                    if (checkStmt.executeQuery().next()) {
                        callback.onError("Bạn đã đăng ký môn học này rồi");
                        return;
                    }

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

    // --- QUẢN LÝ NGƯỜI DÙNG (ADMIN) ---
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
                        users.add(new User(
                                rs.getInt("Id"),
                                rs.getString("Username"),
                                rs.getString("Password"),
                                rs.getString("Role"),
                                rs.getString("FullName")
                        ));
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
}
