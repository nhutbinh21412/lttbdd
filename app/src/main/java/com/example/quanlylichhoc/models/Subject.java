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
    private int teacherId;
    private String teacher;
    private String startDate;
    private String endDate;
    private int credits;

    public Subject(String id, String name, String room, String time, String teacher, int teacherId, 
                   String dayOfWeek, String lesson, String classCode, int color, 
                   String startDate, String endDate, int credits) {
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
        this.startDate = startDate;
        this.endDate = endDate;
        this.credits = credits;
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
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getCredits() { return credits; }

    public void setTeacher(String teacher) { this.teacher = teacher; }
}
