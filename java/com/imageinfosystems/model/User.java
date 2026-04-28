package com.imageinfosystems.model;

public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String gender;
    private String country;
    private String createdAt;

    public User() {
    }

    public User(int id, String name, String email, String passwordHash, String gender, String country, String createdAt) {
      
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.gender = gender;
        this.country = country;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}