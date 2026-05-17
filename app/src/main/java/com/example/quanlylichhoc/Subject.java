package com.example.quanlylichhoc;

import java.io.Serializable;

public class Subject implements Serializable {
    private String id;
    private String name;
    private String room;
    private String time; // Giờ cụ thể (vd: 07:00 - 10:35)
    private String teacher;
    private String dayOfWeek; // Thứ 2, Thứ 3...
    private String lesson; // Tiết: 1-4
    private String classCode; // Mã lớp: 24DHTT02
    private int color; // Màu sắc phân loại (mã màu hexa)

    public Subject(String id, String name, String room, String time, String teacher, 
                   String dayOfWeek, String lesson, String classCode, int color) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.time = time;
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.lesson = lesson;
        this.classCode = classCode;
        this.color = color;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRoom() { return room; }
    public String getTime() { return time; }
    public String getTeacher() { return teacher; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getLesson() { return lesson; }
    public String getClassCode() { return classCode; }
    public int getColor() { return color; }
}