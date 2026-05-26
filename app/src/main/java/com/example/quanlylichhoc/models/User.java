package com.example.quanlylichhoc.models;

import java.io.Serializable;

public class User implements Serializable {
    public int id;
    public String username;
    public String password;
    public String role;
    public String fullName;

    public User(int id, String username, String password, String role, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
    }
}
