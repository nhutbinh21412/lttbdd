package com.example.quanlylichhoc.database;
import com.example.quanlylichhoc.models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {

    public static Connection getConnection() {
        Connection conn = null;

        // 1. Cấu hình IP máy tính chứa SQL Server
        // Nếu bạn chạy bằng GIẢ LẬP của Android Studio: dùng IP đặc biệt "10.0.2.2"
        // Nếu bạn chạy bằng ĐIỆN THOẠI THẬT: Cắm chung Wifi với máy tính và nhập IP của máy tính (VD: "192.168.1.5")
        String ip = "10.0.2.2";

        String port = "1433";
        String databaseName = "QuanLyLichHocDB";
        String username = "sa"; // Tài khoản sa đã bật ở SSMS
        String password = "123"; // Mật khẩu của tài khoản sa

        try {
            // Khai báo driver jTDS
            Class.forName("net.sourceforge.jtds.jdbc.Driver");

            // Chuỗi kết nối đặc biệt dành cho SQL Server Express
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + "/" + databaseName + ";user=" + username + ";password=" + password + ";";

            conn = DriverManager.getConnection(connectionUrl);
            System.out.println("Kết nối SQL Server thành công!");
        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy Driver jTDS: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối dữ liệu: " + e.getMessage());
        }

        return conn;
    }
}
