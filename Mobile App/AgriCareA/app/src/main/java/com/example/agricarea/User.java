package com.example.agricarea;


public class User {
    private  int id;
    private String email;
    private String pwd;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPwd() {
        return pwd;
    }

    public String getCity() {
        return city;
    }

    public String getBirth() {
        return birth;
    }

    public User(String email, String pwd, String city, String birth) {
        this.email = email;
        this.pwd = pwd;
        this.city = city;
        this.birth = birth;
    }

    private String city;

    public User(int id, String email, String pwd, String city, String birth) {
        this.id = id;
        this.email = email;
        this.pwd = pwd;
        this.city = city;
        this.birth = birth;
    }

    private String birth;



}
