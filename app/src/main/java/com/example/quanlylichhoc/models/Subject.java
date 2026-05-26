package com.example.quanlylichhoc.models;

import java.io.Serializable;

public class Subject implements Serializable {
    private String id;
    private String name;
    private String room;
    private String time;
    private String dayOfWeek;
    private String lesson;
    private String classCode;
    private int color;
    private int teacherId; // Lưu ID của Giảng viên quản lý
    private String teacher; // Tên giảng viên

    public Subject(String id, String name, String room, String time, String teacher, int teacherId, 
                   String dayOfWeek, String lesson, String classCode, int color) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.time = time;
        this.teacher = teacher;
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.lesson = lesson;
        this.classCode = classCode;
        this.color = color;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRoom() { return room; }
    public String getTime() { return time; }
    public String getTeacher() { return teacher; }
    public int getTeacherId() { return teacherId; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getLesson() { return lesson; }
    public String getClassCode() { return classCode; }
    public int getColor() { return color; }

    public void setTeacher(String teacher) { this.teacher = teacher; }
}
