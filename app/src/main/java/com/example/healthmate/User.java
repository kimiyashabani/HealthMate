package com.example.healthmate;

public class User {
    private String name;
    private String familyName;
    private String username;
    private String password;

    public User(String name, String familyName, String username, String password) {
        this.name = name;
        this.familyName = familyName;
        this.username = username;
        this.password = password;
    }
    public User() {}
    public String getName() {
        return name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
