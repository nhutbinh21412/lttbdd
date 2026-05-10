package com.example.quanlylichhoc;

import java.io.Serializable;

public class Subject implements Serializable {
    private String id;
    private String name;
    private String room;
    private String time;
    private String teacher;

    public Subject(String id, String name, String room, String time, String teacher) {
        this.id = id;
        this.name = name;
        this.room = room;
        this.time = time;
        this.teacher = teacher;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getRoom() { return room; }
    public String getTime() { return time; }
    public String getTeacher() { return teacher; }
}