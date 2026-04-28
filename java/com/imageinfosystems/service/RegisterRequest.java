package com.imageinfosystems.service;

public class RegisterRequest {

    private final String name;
    private final String email;
    private final String password;
    private final String conformpassord;
    private final String gender;
    private final String country;

    public RegisterRequest(String name, String email, String password,
                           String conformpassord, String gender, String country) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.conformpassord = conformpassord;
        this.gender = gender;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getConformpassord() {
        return conformpassord;
    }

    public String getGender() {
        return gender;
    }

    public String getCountry() {
        return country;
    }
}