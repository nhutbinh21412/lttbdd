package com.example.quanlylichhoc;

public class AttendanceModel {
    private int studentId;
    private String studentName;
    private String status;
    private String date;

    public AttendanceModel(int studentId, String studentName, String status, String date) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.status = status;
        this.date = date;
    }

    public int getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public void setStatus(String status) { this.status = status; }
}
