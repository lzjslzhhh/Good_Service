package com.example.goodservice.dto.auth;

public class RegisterUserDTO {

    private String username;
    private String intro;
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
